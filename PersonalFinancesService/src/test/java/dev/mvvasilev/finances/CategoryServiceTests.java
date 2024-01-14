package dev.mvvasilev.finances;

import dev.mvvasilev.finances.persistence.CategorizationRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionCategoryRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import dev.mvvasilev.finances.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class CategoryServiceTests {

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

    public CategoryServiceTests() {
    }

}
