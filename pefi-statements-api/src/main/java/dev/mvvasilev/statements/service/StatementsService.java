package dev.mvvasilev.statements.service;

import dev.mvvasilev.common.dto.KafkaProcessedTransactionDTO;
import dev.mvvasilev.common.dto.KafkaReplaceProcessedTransactionsDTO;
import dev.mvvasilev.common.dto.ProcessedTransactionFieldDTO;
import dev.mvvasilev.common.enums.ProcessedTransactionField;
import dev.mvvasilev.common.enums.RawTransactionValueType;
import dev.mvvasilev.common.exceptions.CommonFinancesException;
import dev.mvvasilev.common.web.CrudResponseDTO;
import dev.mvvasilev.statements.configuration.KafkaConfiguration;
import dev.mvvasilev.statements.dto.*;
import dev.mvvasilev.statements.entity.RawTransactionValue;
import dev.mvvasilev.statements.entity.TransactionMapping;
import dev.mvvasilev.statements.enums.MappingConversionType;
import dev.mvvasilev.statements.persistence.RawStatementRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueGroupRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueRepository;
import dev.mvvasilev.statements.persistence.TransactionMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static dev.mvvasilev.common.enums.ProcessedTransactionField.*;
import static dev.mvvasilev.common.enums.ProcessedTransactionField.TIMESTAMP;

@Service
@Transactional
public class StatementsService {

    private final Logger logger = LoggerFactory.getLogger(StatementsService.class);

    private final RawStatementRepository rawStatementRepository;

    private final RawTransactionValueGroupRepository rawTransactionValueGroupRepository;

    private final RawTransactionValueRepository rawTransactionValueRepository;

    private final TransactionMappingRepository transactionMappingRepository;

    private final KafkaTemplate<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsKafkaTemplate;

    @Autowired
    public StatementsService(
            RawStatementRepository rawStatementRepository,
            RawTransactionValueGroupRepository rawTransactionValueGroupRepository,
            RawTransactionValueRepository rawTransactionValueRepository,
            TransactionMappingRepository transactionMappingRepository,
            KafkaTemplate<String, KafkaReplaceProcessedTransactionsDTO> replaceTransactionsKafkaTemplate
    ) {
        this.rawStatementRepository = rawStatementRepository;
        this.rawTransactionValueGroupRepository = rawTransactionValueGroupRepository;
        this.rawTransactionValueRepository = rawTransactionValueRepository;
        this.transactionMappingRepository = transactionMappingRepository;
        this.replaceTransactionsKafkaTemplate = replaceTransactionsKafkaTemplate;
    }

    public Collection<UploadedStatementDTO> fetchStatementsForUser(final int userId) {
        return rawStatementRepository.fetchAllForUser(userId)
                .stream()
                .map(dto -> new UploadedStatementDTO(dto.getId(), dto.getName(), dto.getTimeCreated()))
                .collect(Collectors.toList());
    }

    public Collection<TransactionValueGroupDTO> fetchTransactionValueGroupsForUserStatement(final Long statementId) {
        return rawTransactionValueGroupRepository.fetchAllForStatement(statementId)
                .stream()
                .map(dto -> new TransactionValueGroupDTO(dto.getId(), dto.getName(), RawTransactionValueType.values()[dto.getType()]))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStatement(final Long statementId) {
        rawStatementRepository.deleteById(statementId);
        rawStatementRepository.flush();
    }

    public Collection<TransactionMappingDTO> fetchMappingsForStatement(Long statementId) {
        return transactionMappingRepository.fetchTransactionMappingsWithStatementId(statementId)
                .stream()
                .map(entity -> new TransactionMappingDTO(
                                entity.getId(),
                                entity.getRawTransactionValueGroupId(),
                                new ProcessedTransactionFieldDTO(
                                        entity.getProcessedTransactionField(),
                                        entity.getProcessedTransactionField().type()
                                ),
                                entity.getConversionType() != null ?
                                new SupportedMappingConversionDTO(
                                        entity.getConversionType(),
                                        entity.getConversionType().getFrom(),
                                        entity.getConversionType().getTo()
                                ) : null,
                                entity.getTrueBranchStringValue(),
                                entity.getFalseBranchStringValue()
                        )
                )
                .toList();
    }

    public Collection<CrudResponseDTO> createTransactionMappingsForStatement(Long statementId, Collection<CreateTransactionMappingDTO> dtos) {
        transactionMappingRepository.deleteAllForStatement(statementId);

        return transactionMappingRepository.saveAllAndFlush(
                    dtos.stream()
                        .map(dto -> {
                            final var mapping = new TransactionMapping();

                            mapping.setRawTransactionValueGroupId(dto.rawTransactionValueGroupId());
                            mapping.setProcessedTransactionField(dto.field());

                            if (dto.conversionType() != null) {
                                mapping.setConversionType(dto.conversionType());
                                mapping.setTrueBranchStringValue(dto.trueBranchStringValue());
                                mapping.setFalseBranchStringValue(dto.falseBranchStringValue());
                            }

                            return mapping;
                        })
                        .toList()
                )
                .stream()
                .map(entity -> new CrudResponseDTO(entity.getId(), 1))
                .toList();

    }

    public void processStatement(Long statementId, Integer userId) {

        final var mappings = transactionMappingRepository.fetchTransactionMappingsWithStatementId(statementId);

        final var processedTransactions = rawTransactionValueRepository
                .fetchAllValuesForValueGroups(mappings.stream().map(TransactionMapping::getRawTransactionValueGroupId).toList())
                .stream()
                .collect(Collectors.groupingBy(RawTransactionValue::getRowIndex, HashMap::new, Collectors.toCollection(ArrayList::new)))
                .values()
                .stream()
                .map(rawTransactionValues -> {
                    final var transaction = mapValuesToTransaction(rawTransactionValues, mappings);

                    transaction.setStatementId(statementId);
                    transaction.setUserId(userId);

                    return transaction;
                })
                .toList();

        replaceTransactionsKafkaTemplate.send(KafkaConfiguration.REPLACE_TRANSACTIONS_TOPIC, new KafkaReplaceProcessedTransactionsDTO(
                statementId,
                userId,
                processedTransactions
        ));
    }

    // This const is a result of the limitations of the JVM.
    // It is impossible to refer to either a field, or method of a class statically.
    // Because of this, it is very difficult to tie the ProcessedTransactionField values to the actual class fields they represent.
    // To resolve this imperfection, this const lives here, in plain view, so when one of the fields is changed,
    // Hopefully the programmer remembers to change the value inside as well.
    private static final Map<ProcessedTransactionField, BiConsumer<KafkaProcessedTransactionDTO, Object>> FIELD_SETTERS = Map.ofEntries(
            Map.entry(
                    DESCRIPTION,
                    (pt, value) -> pt.setDescription((String) value)
            ),
            Map.entry(
                    AMOUNT,
                    (pt, value) -> pt.setAmount((Double) value)
            ),
            Map.entry(
                    IS_INFLOW,
                    (pt, value) -> pt.setInflow((Boolean) value)
            ),
            Map.entry(
                    TIMESTAMP,
                    (pt, value) -> pt.setTimestamp((LocalDateTime) value)
            )
    );

    private KafkaProcessedTransactionDTO mapValuesToTransaction(List<RawTransactionValue> values, final Collection<TransactionMapping> mappings) {
        final var processedTransaction = new KafkaProcessedTransactionDTO();

        values.forEach(value -> {
            final var mapping = mappings.stream()
                    .filter(m -> Objects.equals(m.getRawTransactionValueGroupId(), value.getGroupId()))
                    .findFirst()
                    .orElseThrow(() -> new CommonFinancesException("Unable to map values to transaction: no mapping found for group"));

            final var conversionType = mapping.getConversionType();

            // Not converting
            if (conversionType == null) {

                // Map the field from the uploaded statement to the final processed transaction
                // 1. Fetch the class field using the mapping FIELD_NAMES
                // 2. Determine the type of field from the enum ( avoid using more reflection than necessary )
                // 3. Set the new value of the field
                // This should work fine for new fields as well, so long as the ProcessedTransactionField enum and FIELD_SETTERS is maintained.
                // If only Java had a better way of doing this.

                try {

                    Object val = switch (mapping.getProcessedTransactionField().type()) {
                        case STRING -> value.getStringValue();
                        case NUMERIC -> value.getNumericValue();
                        case TIMESTAMP -> value.getTimestampValue();
                        case BOOLEAN -> value.getBooleanValue();
                    };

                    FIELD_SETTERS.get(mapping.getProcessedTransactionField()).accept(processedTransaction, val);

                } catch (Exception e) {
                    logger.error("Error while mapping statement.", e);
                }
            } else {
                // If converting, first pass the value through conversion, then set it to the field as described above

                Object result = convertValue(conversionType, mapping, value);

                try {
                    FIELD_SETTERS.get(mapping.getProcessedTransactionField()).accept(processedTransaction, result);
                } catch (Exception e) {
                    logger.error("Error while mapping statement.", e);
                }
            }
        });

        return processedTransaction;
    }

    private Object convertValue(MappingConversionType conversionType, TransactionMapping mapping, RawTransactionValue value) {
//        TODO: Fetch value group from db to double-check from type
//        if (group.getType() != conversionType.getFrom()) {
//            throw new CommonFinancesException("Invalid conversion type (from): is %s, but expected %s", conversionType.getFrom(), group.getType());
//        }

        if (mapping.getProcessedTransactionField().type() != conversionType.getTo()) {
            throw new CommonFinancesException("Invalid conversion type (to): is %s, but expected %s", conversionType.getTo(), mapping.getProcessedTransactionField().type());
        }

        return switch (conversionType) {
            case STRING_TO_BOOLEAN -> value.getStringValue().equals(mapping.getTrueBranchStringValue());

            case STRING_TO_TIMESTAMP -> LocalDateTime.parse(
                    value.getStringValue(),
                    new DateTimeFormatterBuilder()
                        .appendPattern(mapping.getTimestampPattern())
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .parseDefaulting(ChronoField.YEAR, 1970)
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.LENIENT)
            );

            case STRING_TO_NUMERIC -> Double.parseDouble(value.getStringValue());
        };
    }
}
