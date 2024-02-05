package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.dto.KafkaProcessedTransactionDTO;
import dev.mvvasilev.finances.dtos.ProcessedTransactionDTO;
import dev.mvvasilev.finances.dtos.TransactionCategoryDTO;
import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessedTransactionService {

    final private ProcessedTransactionRepository processedTransactionRepository;

    final private TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    public ProcessedTransactionService(ProcessedTransactionRepository processedTransactionRepository, TransactionCategoryRepository transactionCategoryRepository) {
        this.processedTransactionRepository = processedTransactionRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
    }

    public Page<ProcessedTransactionDTO> fetchPagedProcessedTransactionsForUser(int userId, final Pageable pageable) {
        return processedTransactionRepository.findAllByUserId(userId, pageable)
                .map(t -> new ProcessedTransactionDTO(
                        t.getId(),
                        t.getAmount(),
                        t.isInflow(),
                        t.getTimestamp(),
                        t.getDescription(),
                        transactionCategoryRepository.fetchCategoriesForTransaction(t.getId())
                                .stream().map(ptc -> new TransactionCategoryDTO(ptc.getId(), ptc.getName()))
                                .toList()
                ));
    }

    public void createOrReplaceProcessedTransactions(Long statementId, Integer userId, List<KafkaProcessedTransactionDTO> transactions) {
        processedTransactionRepository.deleteProcessedTransactionsForStatement(statementId, userId);

        var entities = transactions.stream()
                .map(t -> {
                    var entity = new ProcessedTransaction();

                    entity.setUserId(userId);
                    entity.setStatementId(statementId);
                    entity.setInflow(t.isInflow());
                    entity.setTimestamp(t.getTimestamp());
                    entity.setAmount(t.getAmount());
                    entity.setDescription(t.getDescription());

                    return entity;
                })
                .toList();

        processedTransactionRepository.saveAllAndFlush(entities);
    }
}
