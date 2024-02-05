package dev.mvvasilev.common.dto;

import java.util.List;

public record KafkaReplaceProcessedTransactionsDTO(
        Long statementId,
        Integer userId,
        List<KafkaProcessedTransactionDTO> transactions
) {
}
