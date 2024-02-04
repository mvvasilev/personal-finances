package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.data.AbstractEntity;
import dev.mvvasilev.common.exceptions.CommonFinancesException;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.finances.dtos.*;
import dev.mvvasilev.finances.entity.*;
import dev.mvvasilev.finances.persistence.CategorizationRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionCategoryRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
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
                .map(entity -> new CategoryDTO(entity.getId(), entity.getName(), entity.getRuleBehavior()))
                .collect(Collectors.toList());
    }

    public int update(Long categoryId, UpdateCategoryDTO dto) {
        return transactionCategoryRepository.updateTransactionCategoryName(
                categoryId,
                dto.name(),
                dto.ruleBehavior()
        );
    }

    public int delete(Long categoryId) {
        transactionCategoryRepository.deleteById(categoryId);
        return 1; // Affected rows. TODO: Actually fetch from database
    }

    public void categorizeForUser(int userId) {
        final var categories = transactionCategoryRepository.fetchTransactionCategoriesWithUserId(userId);
        final var categorizations = categorizationRepository.fetchForUser(userId);
        final var transactions = processedTransactionRepository.fetchForUser(userId);

        processedTransactionCategoryRepository.deleteAllForTransactions(transactions.stream().map(AbstractEntity::getId).toList());

        // Run each category's rules for all transactions concurrently to each other
        final var futures = categorizations.stream()
                .filter(Categorization::isRoot)
                .collect(Collectors.groupingBy(Categorization::getCategoryId, HashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> CompletableFuture.supplyAsync(() -> {
                    final var categoryId = entry.getKey();
                    final var rules = entry.getValue();

                    final var category = categories.stream().filter(c -> c.getId() == categoryId).findFirst();

                    if (category.isEmpty()) {
                        throw new CommonFinancesException("Orphaned categorization, invalid categoryId");
                    }

                    return transactions.stream()
                            .map(transaction -> {
                                final var matches = switch (category.get().getRuleBehavior()) {
                                    case ANY -> rules.stream().anyMatch(r -> matchesRule(categorizations, r, transaction));
                                    case ALL -> rules.stream().allMatch(r -> matchesRule(categorizations, r, transaction));
                                    case NONE -> rules.stream().noneMatch(r -> matchesRule(categorizations, r, transaction));
                                };

                                if (matches) {
                                    return Optional.of(new ProcessedTransactionCategory(transaction.getId(), categoryId));
                                } else {
                                    return Optional.<ProcessedTransactionCategory>empty();
                                }
                            })
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                }))
                .toArray(length -> (CompletableFuture<List<ProcessedTransactionCategory>>[]) new CompletableFuture[length]);

        // Run them all concurrently/in parallel
        final var ptcs = CompletableFuture.allOf(futures).thenApply((v) ->
                Arrays.stream(futures)
                        .flatMap(future -> future.join().stream())
                        .toList()
        ).join();

        processedTransactionCategoryRepository.saveAllAndFlush(ptcs);
    }



    public boolean matchesRule(final Collection<Categorization> allCategorizations, final Categorization categorization, final ProcessedTransaction processedTransaction) {
        return switch (categorization.getCategorizationRule()) {
            // string operations
            case STRING_REGEX, STRING_EQ, STRING_CONTAINS, STRING_IS_EMPTY -> {
                final String fieldValue = fetchTransactionStringValue(categorization, processedTransaction);

                yield switch (categorization.getCategorizationRule()) {
                    case STRING_EQ -> fieldValue.equalsIgnoreCase(categorization.getStringValue());
                    case STRING_REGEX -> fieldValue.matches(categorization.getStringValue());
                    case STRING_CONTAINS -> fieldValue.toLowerCase().contains(categorization.getStringValue().toLowerCase());
                    case STRING_IS_EMPTY -> fieldValue == null || fieldValue.isBlank();
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

                        // TODO: Avoid recursion
                        yield matchesRule(allCategorizations, left.get(), processedTransaction) && matchesRule(allCategorizations, right.get(), processedTransaction);
                    }
                    case OR -> {
                        if (right.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: right does not exist");
                        }

                        if (left.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: left does not exist");
                        }

                        // TODO: Avoid recursion
                        yield matchesRule(allCategorizations, left.get(), processedTransaction) || matchesRule(allCategorizations, right.get(), processedTransaction);
                    }
                    case NOT -> {
                        if (right.isEmpty()) {
                            throw new CommonFinancesException("Invalid categorization: right does not exist");
                        }

                        // TODO: Avoid recursion
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
        final var categorizations = categorizationRepository.fetchForCategory(categoryId);
        return categorizationRepository.fetchForCategory(categoryId).stream()
                .filter(Categorization::isRoot)
                .map(c -> mapCategorization(categorizations, c))
                .toList();
    }

    private CategorizationDTO mapCategorization(final Collection<Categorization> all, Categorization categorization) {
        return new CategorizationDTO(
                categorization.getId(),
                categorization.getCategoryId(),
                new CategorizationRuleDTO(
                        categorization.getCategorizationRule(),
                        categorization.getCategorizationRule().applicableForType()
                ),
                categorization.getRuleBasedOn() != null ?
                new ProcessedTransactionFieldDTO(
                        categorization.getRuleBasedOn(),
                        categorization.getRuleBasedOn().type()
                ) : null,
                categorization.getStringValue(),
                categorization.getNumericGreaterThan(),
                categorization.getNumericLessThan(),
                categorization.getNumericValue(),
                categorization.getTimestampGreaterThan(),
                categorization.getTimestampLessThan(),
                categorization.getBooleanValue(),
                all.stream()
                        .filter(lc -> categorization.getLeftCategorizationId() != null && lc.getId() == categorization.getLeftCategorizationId())
                        .findFirst()
                        .map(c -> mapCategorization(all, c))
                        .orElse(null),
                all.stream()
                        .filter(lc -> categorization.getRightCategorizationId() != null && lc.getId() == categorization.getRightCategorizationId())
                        .findFirst()
                        .map(c -> mapCategorization(all, c))
                        .orElse(null)
        );
    }

    public Collection<Long> createCategorizationRules(Long categoryId, Integer userId, Collection<CreateCategorizationDTO> dtos) {
        categorizationRepository.deleteAllForCategory(categoryId);

        return dtos.stream()
                .map(dto -> saveCategorizationRule(true, categoryId, userId, dto).getId())
                .toList();
    }

    private Categorization saveCategorizationRule(boolean isRoot, Long categoryId, Integer userId, CreateCategorizationDTO dto) {
        // TODO: Avoid recursion

        final var categorization = new Categorization();

        categorization.setCategorizationRule(dto.rule());
        categorization.setRoot(isRoot);
        categorization.setUserId(userId);
        categorization.setRuleBasedOn(dto.ruleBasedOn());
        categorization.setCategoryId(categoryId);
        categorization.setStringValue(dto.stringValue().orElse(null));
        categorization.setNumericGreaterThan(dto.numericGreaterThan().orElse(null));
        categorization.setNumericLessThan(dto.numericLessThan().orElse(null));
        categorization.setNumericValue(dto.numericValue().orElse(null));
        categorization.setTimestampGreaterThan(dto.timestampGreaterThan().orElse(null));
        categorization.setTimestampLessThan(dto.timestampLessThan().orElse(null));
        categorization.setBooleanValue(dto.booleanValue().orElse(false));

        // Only root rules have category id set, to differentiate them from non-roots
        // TODO: This smells bad. Add an isRoot property instead?
        if (dto.left() != null) {
            final var leftCat = saveCategorizationRule(false, categoryId, userId, dto.left());
            categorization.setLeftCategorizationId(leftCat.getId());
        }

        if (dto.right() != null) {
            final var rightCat = saveCategorizationRule(false, categoryId, userId, dto.right());
            categorization.setRightCategorizationId(rightCat.getId());
        }

        return categorizationRepository.saveAndFlush(categorization);
    }

    public ImportExportCategoriesDTO exportCategoriesForUser(int userId) {
        final var categories = listForUser(userId);

        return new ImportExportCategoriesDTO(
                categories,
                categories.stream().map(c -> fetchCategorizationRules(c.id())).flatMap(Collection::stream).toList()
        );
    }

    public Collection<Long> importCategoriesForUser(ImportExportCategoriesDTO dto, boolean deleteExisting, int userId) {
        if (deleteExisting) {
            transactionCategoryRepository.deleteAllByUserId(userId);
        }

        return dto.categories().stream().map(c -> {
            var category = new TransactionCategory();

            category.setUserId(userId);
            category.setName(c.name());
            category.setRuleBehavior(c.ruleBehavior());

            category = transactionCategoryRepository.saveAndFlush(category);

            createCategorizationRules(
                    category.getId(),
                    category.getUserId(),
                    dto.categorizationRules().stream()
                            .filter(cr -> Objects.equals(cr.categoryId(), c.id()))
                            .map(this::mapCategorizationForImport)
                            .toList()
            );

            return category.getId();
        }).toList();
    }

    private CreateCategorizationDTO mapCategorizationForImport(CategorizationDTO cr) {
        return new CreateCategorizationDTO (
                cr.rule().rule(),
                cr.ruleBasedOn() == null ? null : cr.ruleBasedOn().field(),
                Optional.ofNullable(cr.stringValue()),
                Optional.ofNullable(cr.numericGreaterThan()),
                Optional.ofNullable(cr.numericLessThan()),
                Optional.ofNullable(cr.numericValue()),
                Optional.ofNullable(cr.timestampGreaterThan()),
                Optional.ofNullable(cr.timestampLessThan()),
                Optional.ofNullable(cr.booleanValue()),
                cr.left() != null ? mapCategorizationForImport(cr.left()) : null,
                cr.right() != null ? mapCategorizationForImport(cr.right()) : null
        );
    }
}
