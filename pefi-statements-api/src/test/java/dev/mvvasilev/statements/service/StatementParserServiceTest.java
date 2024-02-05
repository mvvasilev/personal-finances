package dev.mvvasilev.statements.service;

import dev.mvvasilev.common.enums.RawTransactionValueType;
import dev.mvvasilev.statements.entity.RawStatement;
import dev.mvvasilev.statements.entity.RawTransactionValue;
import dev.mvvasilev.statements.entity.RawTransactionValueGroup;
import dev.mvvasilev.statements.persistence.RawStatementRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueGroupRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueRepository;
import org.apache.commons.lang3.IntegerRange;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

class StatementParserServiceTest {
    private static final String XLS_TEST_PATH = "src/test/resources/xls-test.xls";

    private static final String XLSX_TEST_PATH = "src/test/resources/xlsx-test.xlsx";

    private static final RawStatementRepository rawStatementRepository = Mockito.mock(RawStatementRepository.class);

    private static final RawTransactionValueGroupRepository rawTransactionValueGroupRepository = Mockito.mock(RawTransactionValueGroupRepository.class);

    private static final RawTransactionValueRepository rawTransactionValueRepository = Mockito.mock(RawTransactionValueRepository.class);

    private StatementParserService service;

    @BeforeAll
    static void beforeAll() {
        Mockito.when(rawStatementRepository.saveAndFlush(Mockito.any(RawStatement.class))).thenAnswer((input) -> {
            ((RawStatement) input.getArguments()[0]).setId(1L);

            return input.getArguments()[0];
        });

        Mockito.when(rawTransactionValueGroupRepository.saveAllAndFlush(Mockito.anyList())).thenAnswer((input) -> {
            var inputList = (List<RawTransactionValueGroup>) input.getArguments()[0];

            var range = LongStream.range(0, inputList.size()).iterator();

            return inputList.stream().peek(r -> r.setId(range.next())).toList();
        });

        Mockito.when(rawTransactionValueRepository.saveAllAndFlush(Mockito.anyList())).thenAnswer((input) -> {
            var inputList = (List<RawTransactionValue>) input.getArguments()[0];

            var range = LongStream.range(0, inputList.size()).iterator();

            return inputList.stream().peek(r -> r.setId(range.next())).toList();
        });
    }

    @BeforeEach
    void setUp() {
        service = new StatementParserService(
                rawStatementRepository,
                rawTransactionValueGroupRepository,
                rawTransactionValueRepository
        );
    }

    @Test
    void parseSheet() throws IOException {
        var xlsWorkbook = WorkbookFactory.create(Files.newInputStream(Path.of(XLS_TEST_PATH)));
        var xlsxWorkbook = WorkbookFactory.create(Files.newInputStream(Path.of(XLSX_TEST_PATH)));

        var xlsResult = service.parseSheet(
                xlsWorkbook.getSheetAt(0),
                0,
                "xls-test.xls"
        );

        var xlsxResult = service.parseSheet(
                xlsxWorkbook.getSheetAt(0),
                0,
                "xlsx-test.xlsx"
        );

        Assertions.assertEquals("xls-test.xls", xlsResult.statement().getName());
        Assertions.assertEquals(4, xlsResult.groups().size());

        Assertions.assertEquals("xlsx-test.xlsx", xlsxResult.statement().getName());
        Assertions.assertEquals(4, xlsxResult.groups().size());

        // === XLS Value Groups ===

        Assertions.assertEquals(RawTransactionValueType.STRING, xlsResult.groups().getFirst().getType());
        Assertions.assertEquals("Column A", xlsResult.groups().getFirst().getName());
        Assertions.assertEquals(xlsResult.statement().getId(), xlsResult.groups().getFirst().getStatementId());

        Assertions.assertEquals(RawTransactionValueType.NUMERIC, xlsResult.groups().get(1).getType());
        Assertions.assertEquals("Column B", xlsResult.groups().get(1).getName());
        Assertions.assertEquals(xlsResult.statement().getId(), xlsResult.groups().get(1).getStatementId());

        Assertions.assertEquals(RawTransactionValueType.TIMESTAMP, xlsResult.groups().get(2).getType());
        Assertions.assertEquals("Column C", xlsResult.groups().get(2).getName());
        Assertions.assertEquals(xlsResult.statement().getId(), xlsResult.groups().get(2).getStatementId());

        Assertions.assertEquals(RawTransactionValueType.BOOLEAN, xlsResult.groups().get(3).getType());
        Assertions.assertEquals("Column D", xlsResult.groups().get(3).getName());
        Assertions.assertEquals(xlsResult.statement().getId(), xlsResult.groups().get(3).getStatementId());

        // === XLSX Value Groups ===

        Assertions.assertEquals(RawTransactionValueType.STRING, xlsxResult.groups().getFirst().getType());
        Assertions.assertEquals("Column A", xlsxResult.groups().getFirst().getName());
        Assertions.assertEquals(xlsResult.statement().getId(), xlsxResult.groups().getFirst().getStatementId());

        Assertions.assertEquals(RawTransactionValueType.NUMERIC, xlsxResult.groups().get(1).getType());
        Assertions.assertEquals("Column B", xlsxResult.groups().get(1).getName());
        Assertions.assertEquals(xlsxResult.statement().getId(), xlsxResult.groups().get(1).getStatementId());

        Assertions.assertEquals(RawTransactionValueType.TIMESTAMP, xlsxResult.groups().get(2).getType());
        Assertions.assertEquals("Column C", xlsxResult.groups().get(2).getName());
        Assertions.assertEquals(xlsxResult.statement().getId(), xlsxResult.groups().get(2).getStatementId());

        Assertions.assertEquals(RawTransactionValueType.BOOLEAN, xlsxResult.groups().get(3).getType());
        Assertions.assertEquals("Column D", xlsxResult.groups().get(3).getName());
        Assertions.assertEquals(xlsxResult.statement().getId(), xlsxResult.groups().get(3).getStatementId());

        // === XLS Values ===

        Assertions.assertEquals(xlsResult.groups().getFirst().getId(), xlsResult.values().getFirst().getGroupId());
        Assertions.assertEquals(xlsResult.values().getFirst().getRowIndex(), 1);
        Assertions.assertEquals("Text a", xlsResult.values().getFirst().getStringValue());

        Assertions.assertEquals(xlsResult.groups().get(1).getId(), xlsResult.values().get(6).getGroupId());
        Assertions.assertEquals(1, xlsResult.values().get(6).getRowIndex());
        Assertions.assertEquals(0.12, xlsResult.values().get(6).getNumericValue());

        Assertions.assertEquals(xlsResult.groups().get(2).getId(), xlsResult.values().get(12).getGroupId());
        Assertions.assertEquals(1, xlsResult.values().get(12).getRowIndex());
        Assertions.assertEquals(LocalDateTime.of(LocalDate.of(1990, 1, 1), LocalTime.of(0, 0, 0)), xlsResult.values().get(12).getTimestampValue());

        Assertions.assertEquals(xlsResult.groups().get(3).getId(), xlsResult.values().get(18).getGroupId());
        Assertions.assertEquals(1, xlsResult.values().get(18).getRowIndex());
        Assertions.assertEquals(true, xlsResult.values().get(18).getBooleanValue());

        // === XLSX Values ===

        Assertions.assertEquals(xlsxResult.groups().getFirst().getId(), xlsxResult.values().getFirst().getGroupId());
        Assertions.assertEquals(xlsxResult.values().getFirst().getRowIndex(), 1);
        Assertions.assertEquals("Text a", xlsxResult.values().getFirst().getStringValue());

        Assertions.assertEquals(xlsxResult.groups().get(1).getId(), xlsxResult.values().get(6).getGroupId());
        Assertions.assertEquals(1, xlsxResult.values().get(6).getRowIndex());
        Assertions.assertEquals(0.12, xlsxResult.values().get(6).getNumericValue());

        Assertions.assertEquals(xlsxResult.groups().get(2).getId(), xlsxResult.values().get(12).getGroupId());
        Assertions.assertEquals(1, xlsxResult.values().get(12).getRowIndex());
        Assertions.assertEquals(LocalDateTime.of(LocalDate.of(1990, 1, 1), LocalTime.of(0, 0, 0)), xlsxResult.values().get(12).getTimestampValue());

        Assertions.assertEquals(xlsxResult.groups().get(3).getId(), xlsxResult.values().get(18).getGroupId());
        Assertions.assertEquals(1, xlsxResult.values().get(18).getRowIndex());
        Assertions.assertEquals(true, xlsxResult.values().get(18).getBooleanValue());
    }
}