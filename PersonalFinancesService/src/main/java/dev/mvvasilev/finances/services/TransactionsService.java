package dev.mvvasilev.finances.services;

import dev.mvvasilev.finances.entity.RawStatement;
import dev.mvvasilev.finances.entity.RawTransactionValue;
import dev.mvvasilev.finances.entity.RawTransactionValueGroup;
import dev.mvvasilev.finances.enums.RawTransactionValueType;
import dev.mvvasilev.finances.persistence.RawStatementRepository;
import dev.mvvasilev.finances.persistence.RawTransactionValueGroupRepository;
import dev.mvvasilev.finances.persistence.RawTransactionValueRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional
public class TransactionsService {

    private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("dd.MM.yyyy[ [HH][:mm][:ss]]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()
            .withResolverStyle(ResolverStyle.LENIENT);

    private RawStatementRepository rawStatementRepository;

    private RawTransactionValueGroupRepository rawTransactionValueGroupRepository;

    private RawTransactionValueRepository rawTransactionValueRepository;

    @Autowired
    public TransactionsService(RawStatementRepository rawStatementRepository, RawTransactionValueGroupRepository rawTransactionValueGroupRepository, RawTransactionValueRepository rawTransactionValueRepository) {
        this.rawStatementRepository = rawStatementRepository;
        this.rawTransactionValueGroupRepository = rawTransactionValueGroupRepository;
        this.rawTransactionValueRepository = rawTransactionValueRepository;
    }


    public void uploadMultipleTransactionsFromExcelSheetForUser(InputStream workbookInputStream, String userId) throws IOException {
        var workbook = WorkbookFactory.create(workbookInputStream);

        var firstWorksheet = workbook.getSheetAt(0);

        var lastRowIndex = firstWorksheet.getLastRowNum();

        var statement = new RawStatement();
        statement.setUserId(Integer.parseInt(userId));

        statement = rawStatementRepository.saveAndFlush(statement);

        var firstRow = firstWorksheet.getRow(0);

        List<RawTransactionValueGroup> valueGroups = new ArrayList<>();

        for (var c : firstRow) {

            if (c == null || c.getCellType() == CellType.BLANK) {
                break;
            }

            var transactionValueGroup = new RawTransactionValueGroup();

            transactionValueGroup.setStatementId(statement.getId());
            transactionValueGroup.setName(c.getStringCellValue());

            // group type is string by default, if no other type could have been determined
            var groupType = RawTransactionValueType.STRING;

            for (int y = c.getRowIndex() + 1; y <= lastRowIndex; y++) {
                var typeResult = determineGroupType(firstWorksheet, y, c.getColumnIndex());

                if (typeResult.isPresent()) {
                    groupType = typeResult.get();
                    break;
                }
            }

            transactionValueGroup.setType(groupType);

            valueGroups.add(transactionValueGroup);
        }

        valueGroups = rawTransactionValueGroupRepository.saveAllAndFlush(valueGroups);

        var column = 0;
        for (var group : valueGroups) {
            var valueList = new ArrayList<RawTransactionValue>();

            for (int y = 1; y < lastRowIndex; y++) {
                var value = new RawTransactionValue();

                value.setGroupId(group.getId());

                switch (group.getType()) {
                    case STRING -> value.setStringValue(firstWorksheet.getRow(y).getCell(column).getStringCellValue());
                    case NUMERIC -> value.setNumericValue(firstWorksheet.getRow(y).getCell(column).getNumericCellValue());
                    case TIMESTAMP -> value.setTimestampValue(LocalDateTime.parse(firstWorksheet.getRow(y).getCell(column).getStringCellValue().trim(), DATE_FORMAT));
                    case BOOLEAN -> value.setBooleanValue(firstWorksheet.getRow(y).getCell(column).getBooleanCellValue());
                }

                valueList.add(value);
            }

            rawTransactionValueRepository.saveAllAndFlush(valueList);

            column++;
        }
    }

    private Optional<RawTransactionValueType> determineGroupType(Sheet worksheet, int rowIndex, int columnIndex) {
        var cell = worksheet.getRow(rowIndex).getCell(columnIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return Optional.empty();
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return Optional.of(RawTransactionValueType.BOOLEAN);
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return Optional.of(RawTransactionValueType.NUMERIC);
        }

        var cellValue = cell.getStringCellValue();

        if (isValidDate(cellValue)) {
            return Optional.of(RawTransactionValueType.TIMESTAMP);
        }

        return Optional.empty();
    }

    private boolean isValidDate(String inDate) {
        try {
            DATE_FORMAT.parse(inDate.trim());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

}
