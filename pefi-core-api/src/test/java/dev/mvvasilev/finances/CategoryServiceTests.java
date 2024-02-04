package dev.mvvasilev.finances;

import dev.mvvasilev.finances.persistence.CategorizationRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionCategoryRepository;
import dev.mvvasilev.finances.persistence.ProcessedTransactionRepository;
import dev.mvvasilev.finances.persistence.TransactionCategoryRepository;
import dev.mvvasilev.finances.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public class CategoryServiceTests {

    @Mock
    private TransactionCategoryRepository transactionCategoryRepository;

    @Mock
    private CategorizationRepository categorizationRepository;

    @Mock
    private ProcessedTransactionRepository processedTransactionRepository;

    @Mock
    private ProcessedTransactionCategoryRepository processedTransactionCategoryRepository;

    private CategoryService service;

    @BeforeEach
    public void setup() {
        service = new CategoryService(
                transactionCategoryRepository,
                categorizationRepository,
                processedTransactionRepository,
                processedTransactionCategoryRepository
        );
    }

}
