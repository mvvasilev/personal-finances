package dev.mvvasilev.finances.services;

import dev.mvvasilev.finances.dtos.ProcessedTransactionDTO;
import dev.mvvasilev.finances.entity.ProcessedTransaction;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcessedTransactionService {

    final private ProcessedTransactionRepository processedTransactionRepository;

    @Autowired
    public ProcessedTransactionService(ProcessedTransactionRepository processedTransactionRepository) {
        this.processedTransactionRepository = processedTransactionRepository;
    }

    public Page<ProcessedTransactionDTO> fetchPagedProcessedTransactionsForUser(int userId, final Pageable pageable) {
        return processedTransactionRepository.findAllByUserId(userId, pageable)
                .map(t -> new ProcessedTransactionDTO(
                        t.getId(),
                        t.getAmount(),
                        t.isInflow(),
                        t.getTimestamp(),
                        t.getDescription(),
                        Lists.newArrayList() // TODO: Fetch categories. Do it all in SQL for better performance.
                ));
    }
}
