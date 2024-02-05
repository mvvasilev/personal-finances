package dev.mvvasilev.finances.controllers;

import dev.mvvasilev.common.dto.KafkaReplaceProcessedTransactionsDTO;
import dev.mvvasilev.finances.configuration.KafkaConfiguration;
import dev.mvvasilev.finances.services.ProcessedTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionsKafkaListener {

    private final ProcessedTransactionService service;

    @Autowired
    public TransactionsKafkaListener(ProcessedTransactionService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = KafkaConfiguration.REPLACE_TRANSACTIONS_TOPIC,
            containerFactory = "replaceTransactionsKafkaListenerContainerFactory"
    )
    public void replaceTransactionsListener(KafkaReplaceProcessedTransactionsDTO message) {
        service.createOrReplaceProcessedTransactions(message.statementId(), message.userId(), message.transactions());
    }

}
