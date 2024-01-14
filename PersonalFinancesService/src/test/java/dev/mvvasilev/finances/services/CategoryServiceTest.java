package dev.mvvasilev.finances.services;

import dev.mvvasilev.finances.CategorizationBuilder;
import dev.mvvasilev.finances.persistence.CategorizationRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionCategoryRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CategoryServiceTest {

    @MockBean
    private TransactionCategoryRepository transactionCategoryRepository;

    @MockBean
    private CategorizationRepository categorizationRepository;

    @MockBean
    private ProcessedTransactionRepository processedTransactionRepository;

    @MockBean
    private ProcessedTransactionCategoryRepository processedTransactionCategoryRepository;

    @Autowired
    private CategoryService service;

    @Test
    void matchesRule() {

    }
}