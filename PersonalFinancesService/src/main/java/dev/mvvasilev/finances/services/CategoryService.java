package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.exceptions.CommonFinancesException;
import dev.mvvasilev.finances.dtos.*;
import dev.mvvasilev.finances.entity.Categorization;
import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.entity.ProcessedTransactionCategory;
import dev.mvvasilev.finances.entity.TransactionCategory;
import dev.mvvasilev.finances.persistence.CategorizationRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionCategoryRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    final private TransactionCategoryRepository transactionCategoryRepository;

    final private CategorizationRepository categorizationRepository;

    final private ProcessedTransactionRepository processedTransactionRepository;

    final private ProcessedTransactionCategoryRepository processedTransactionCategoryRepository;

    @Autowired
    public CategoryService(
            TransactionCategoryRepository transactionCategoryRepository,
            CategorizationRepository categorizationRepository,
            ProcessedTransactionRepository processedTransactionRepository,
            ProcessedTransactionCategoryRepository processedTransactionCategoryRepository
    ) {
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.categorizationRepository = categorizationRepository;
        this.processedTransactionRepository = processedTransactionRepository;
        this.processedTransactionCategoryRepository = processedTransactionCategoryRepository;
    }

    public Long createForUser(CreateCategoryDTO dto, int userId) {
        var transactionCategory = new TransactionCategory();

        transactionCategory.setName(dto.name());
        transactionCategory.setUserId(userId);

        transactionCategory = transactionCategoryRepository.saveAndFlush(transactionCategory);

        return transactionCategory.getId();
    }

    public Collection<CategoryDTO> listForUser(int userId) {
        return transactionCategoryRepository.fetchTransactionCategoriesWithUserId(userId)
                .stream()
                .map(entity -> new CategoryDTO(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }

    public int update(Long categoryId, UpdateCategoryDTO dto) {
        return transactionCategoryRepository.updateTransactionCategoryName(
                categoryId,
                dto.name()
        );
    }

    public int delete(Long categoryId) {
        transactionCategoryRepository.deleteById(categoryId);
        return 1; // Affected rows. TODO: Actually fetch from database
    }

    public void categorizeForUser(int userId) {
        final var categorizations = categorizationRepository.fetchForUser(userId);
        final var transactions = processedTransactionRepository.fetchForUser(userId);

        // Run all the categorization rules async
        final var futures = categorizations.stream()
                .map(c -> CompletableFuture.supplyAsync(() ->
                        transactions.stream()
                                .map((transaction) -> categorizeTransaction(categorizations, c, transaction))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList())
                )
                .toArray(length -> (CompletableFuture<List<ProcessedTransactionCategory>>[]) new CompletableFuture[length]);

        // Run them all in parallel
        final var categories = CompletableFuture.allOf(futures).thenApply((v) ->
                Arrays.stream(futures)
                        .flatMap(future -> future.join().stream())
                        .toList()
        ).join();

        processedTransactionCategoryRepository.saveAllAndFlush(categories);
    }

    private Optional<ProcessedTransactionCategory> categorizeTransaction(final Collection<Categorization> allCategorizations, Categorization categorization, ProcessedTransaction processedTransaction) {
        if (matchesRule(allCategorizations, categorization, processedTransaction)) {
            return Optional.of(new ProcessedTransactionCategory(processedTransaction.getId(), categorization.getCategoryId()));
        } else {
            return Optional.empty();
        }
    }

    private boolean matchesRule(final Collection<Categorization> allCategorizations, final Categorization categorization, final ProcessedTransaction processedTransaction) {
        return switch (categorization.getCategorizationRule()) {
            // string operations
            case STRING_REGEX, STRING_EQ, STRING_CONTAINS -> {
                final String fieldValue = fetchTransactionStringValue(categorization, processedTransaction);

                yield switch (categorization.getCategorizationRule()) {
                    case STRING_EQ -> fieldValue.equalsIgnoreCase(categorization.getStringValue());
                    case STRING_REGEX -> fieldValue.matches(categorization.getStringValue());
                    case STRING_CONTAINS -> fieldValue.contains(categorization.getStringValue());
                    default -> throw new CommonFinancesException("Unsupported string rule: %s", categorization.getCategorizationRule());
                };
            }
            // numeric operations
            case NUMERIC_GREATER_THAN, NUMERIC_LESS_THAN, NUMERIC_EQUALS, NUMERIC_BETWEEN -> {
                final double fieldValue = fetchTransactionNumericValue(categorization, processedTransaction);

                yield switch (categorization.getCategorizationRule()) {
                    case NUMERIC_GREATER_THAN -> fieldValue > categorization.getNumericGreaterThan();
                    case NUMERIC_LESS_THAN -> fieldValue < categorization.getNumericLessThan();
                    case NUMERIC_EQUALS -> fieldValue == categorization.getNumericValue();
                    case NUMERIC_BETWEEN -> fieldValue > categorization.getNumericGreaterThan() && fieldValue < categorization.getNumericLessThan();
                    default -> throw new CommonFinancesException("Unsupported numeric rule: %s", categorization.getCategorizationRule());
                };
            }
            // datetime/timestamp operations
            case TIMESTAMP_GREATER_THAN, TIMESTAMP_LESS_THAN, TIMESTAMP_BETWEEN -> {
                final LocalDateTime fieldValue = fetchTransactionTimestampValue(categorization, processedTransaction);

                yield switch (categorization.getCategorizationRule()) {
                    case TIMESTAMP_GREATER_THAN -> fieldValue.isBefore(categorization.getTimestampGreaterThan());
                    case TIMESTAMP_LESS_THAN -> fieldValue.isAfter(categorization.getTimestampLessThan());
                    case TIMESTAMP_BETWEEN -> fieldValue.isBefore(categorization.getTimestampGreaterThan()) && fieldValue.isAfter(categorization.getTimestampLessThan());
                    default -> throw new CommonFinancesException("Unsupported timestamp rule: %s", categorization.getCategorizationRule());
                };
            }
            // boolean operations
            case BOOLEAN_EQ -> {
                final boolean equalsValue = categorization.getBooleanValue();

                boolean fieldValue = fetchTransactionBooleanValue(categorization, processedTransaction);

                yield equalsValue == fieldValue;
            }
            // logical operations
            case OR, AND, NOT -> {
                var leftId = categorization.getLeftCategorizationId();
                var rightId = categorization.getRightCategorizationId();

                final var left = allCategorizations.stream()
                        .filter(c -> c.getId() == leftId)
                        .findFirst();

                final var right = allCategorizations.stream()
                        .filter(c -> c.getId() == rightId)
                        .findFirst();

                yield switch (categorization.getCategorizationRule()) {
                    case AND -> {
                        if (right.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: right does not exist");
                        }

                        if (left.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: left does not exist");
                        }

                        yield matchesRule(allCategorizations, left.get(), processedTransaction) && matchesRule(allCategorizations, right.get(), processedTransaction);
                    }
                    case OR -> {
                        if (right.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: right does not exist");
                        }

                        if (left.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: left does not exist");
                        }

                        yield matchesRule(allCategorizations, left.get(), processedTransaction) || matchesRule(allCategorizations, right.get(), processedTransaction);
                    }
                    case NOT -> {
                        if (right.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: right does not exist");
                        }

                        yield !matchesRule(allCategorizations, right.get(), processedTransaction);
                    }
                    default -> throw new CommonFinancesException("Invalid logical operation: %s", categorization.getCategorizationRule());
                };
            }
        };
    }

    private String fetchTransactionStringValue(Categorization categorization, ProcessedTransaction processedTransaction) {
        return switch (categorization.getRuleBasedOn()) {
            case DESCRIPTION -> processedTransaction.getDescription();
            case AMOUNT, IS_INFLOW, TIMESTAMP -> throw invalidCategorizationRule(categorization);
        };
    }

    private double fetchTransactionNumericValue(Categorization categorization, ProcessedTransaction processedTransaction) {
        return switch (categorization.getRuleBasedOn()) {
            case AMOUNT -> processedTransaction.getAmount();
            case DESCRIPTION, IS_INFLOW, TIMESTAMP -> throw invalidCategorizationRule(categorization);
        };
    }

    private LocalDateTime fetchTransactionTimestampValue(Categorization categorization, ProcessedTransaction processedTransaction) {
        return switch (categorization.getRuleBasedOn()) {
            case TIMESTAMP -> processedTransaction.getTimestamp();
            case DESCRIPTION, IS_INFLOW, AMOUNT -> throw invalidCategorizationRule(categorization);
        };
    }

    private boolean fetchTransactionBooleanValue(Categorization categorization, ProcessedTransaction processedTransaction) {
        return switch (categorization.getRuleBasedOn()) {
            case IS_INFLOW -> processedTransaction.isInflow();
            case DESCRIPTION, TIMESTAMP, AMOUNT -> throw invalidCategorizationRule(categorization);
        };
    }

    private CommonFinancesException invalidCategorizationRule(Categorization categorization) {
        throw new CommonFinancesException(
                "Invalid categorization rule: field %s is of type %s, while the rule is applicable only for type %s",
                categorization.getRuleBasedOn(),
                categorization.getRuleBasedOn().type(),
                categorization.getCategorizationRule().applicableForType()
        );
    }

    public Collection<CategorizationDTO> fetchCategorizationRules(Long categoryId) {
        return categorizationRepository.fetchForCategory(categoryId).stream()
                .map(entity -> {
                    // TODO: Recursion
                })
                .toList();
    }

    public Long createCategorizationRule(Long categoryId, Collection<CreateCategorizationDTO> dto) {
        // TODO: Clear previous rules for category and replace with new ones

//        final var categorization = new Categorization();
//
//        categorization.setCategorizationRule(dto.rule());
//        categorization.setCategoryId(categoryId);
//        categorization.setStringValue(dto.stringValue());
//        categorization.setNumericGreaterThan(dto.numericGreaterThan());
//        categorization.setNumericLessThan(dto.numericLessThan());
//        categorization.setNumericValue(dto.numericValue());
//        categorization.setTimestampGreaterThan(dto.timestampGreaterThan());
//        categorization.setTimestampLessThan(dto.timestampLessThan());
//        categorization.setBooleanValue(dto.booleanValue());
//
//        if (dto.left() != null) {
//            final var leftCat = createCategorizationRule(null, dto.left());
//            categorization.setLeftCategorizationId(leftCat);
//        }
//
//        if (dto.right() != null) {
//            final var rightCat = createCategorizationRule(null, dto.right());
//            categorization.setRightCategorizationId(rightCat);
//        }
//
//        return categorizationRepository.save(categorization).getId();
    }
}
