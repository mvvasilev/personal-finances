package dev.mvvasilev.statements.service;

import dev.mvvasilev.common.enums.RawTransactionValueType;
import dev.mvvasilev.statements.entity.RawStatement;
import dev.mvvasilev.statements.entity.RawTransactionValue;
import dev.mvvasilev.statements.entity.RawTransactionValueGroup;
import dev.mvvasilev.statements.persistence.RawStatementRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueGroupRepository;
import dev.mvvasilev.statements.persistence.RawTransactionValueRepository;
import dev.mvvasilev.statements.service.dtos.ParsedStatementDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static dev.mvvasilev.common.enums.RawTransactionValueType.*;

@Service
public class StatementParserService {

    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("dd.MM.yyyy[ [HH][:mm][:ss]]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()
            .withResolverStyle(ResolverStyle.LENIENT);

    private final RawStatementRepository rawStatementRepository;

    private final RawTransactionValueGroupRepository rawTransactionValueGroupRepository;

    private final RawTransactionValueRepository rawTransactionValueRepository;

    @Autowired
    public StatementParserService(RawStatementRepository rawStatementRepository, RawTransactionValueGroupRepository rawTransactionValueGroupRepository, RawTransactionValueRepository rawTransactionValueRepository) {
        this.rawStatementRepository = rawStatementRepository;
        this.rawTransactionValueGroupRepository = rawTransactionValueGroupRepository;
        this.rawTransactionValueRepository = rawTransactionValueRepository;
    }

    public void uploadStatementFromExcelSheetForUser(final String fileName, final String mimeType, final InputStream workbookInputStream, final int userId) throws IOException {
        var workbook = WorkbookFactory.create(workbookInputStream);

        var firstWorksheet = workbook.getSheetAt(0);

        parseSheet(firstWorksheet, userId, fileName);
    }

    protected ParsedStatementDTO parseSheet(Sheet sheet, final int userId, final String fileName) {
        var lastRowIndex = sheet.getLastRowNum();

        var statement = new RawStatement();
        statement.setUserId(userId);
        statement.setName(fileName);

        statement = rawStatementRepository.saveAndFlush(statement);

        var firstRow = sheet.getRow(0);

        List<RawTransactionValueGroup> valueGroups = new ArrayList<>();

        // turn each column into a value group
        for (var c : firstRow) {

            if (c == null || c.getCellType() == CellType.BLANK) {
                break;
            }

            var valueGroup = parseValueGroup(c.getStringCellValue(), sheet, c.getRowIndex(), lastRowIndex, c.getColumnIndex());

            valueGroup.setStatementId(statement.getId());

            valueGroups.add(valueGroup);
        }

        valueGroups = rawTransactionValueGroupRepository.saveAllAndFlush(valueGroups);

        var column = 0;
        List<RawTransactionValue> allValues = new ArrayList<>();

        // turn each cell in each row into a value, related to the value group ( column )
        for (var group : valueGroups) {
            var values = parseValuesForColumn(group, sheet, column, lastRowIndex);

            allValues.addAll(values);

            column++;
        }

        allValues = rawTransactionValueRepository.saveAllAndFlush(allValues);

        return new ParsedStatementDTO(
                statement,
                valueGroups,
                allValues
        );
    }

    protected RawTransactionValueGroup parseValueGroup(String name, Sheet worksheet, int rowIndex, int lastRowIndex, int columnIndex) {
        var transactionValueGroup = new RawTransactionValueGroup();

        transactionValueGroup.setName(name);

        // group type is string by default, if no other type could have been determined
        var groupType = STRING;

        // iterate down through the rows on this column, looking for the first one to return a type
        for (int y = rowIndex + 1; y <= lastRowIndex; y++) {
            var typeResult = determineGroupType(worksheet, y, columnIndex);

            // if a type has been determined, stop here
            if (typeResult.isPresent()) {
                groupType = typeResult.get();
                break;
            }
        }

        transactionValueGroup.setType(groupType);

        return transactionValueGroup;
    }

    protected List<RawTransactionValue> parseValuesForColumn(RawTransactionValueGroup group, Sheet worksheet, int x, int lastRowIndex) {
        return IntStream.range(1, lastRowIndex + 1).mapToObj(y -> parseValueFromCell(group, worksheet, x, y)).toList();
    }

    protected RawTransactionValue parseValueFromCell(RawTransactionValueGroup group, Sheet worksheet, int x, int y) {
        var value = new RawTransactionValue();

        value.setGroupId(group.getId());
        value.setRowIndex(y);

        var cell = worksheet.getRow(y).getCell(x);

        if (cell.getCellType() == CellType.STRING) {

            var cellValue = cell.getStringCellValue().trim();

            try {
                switch (group.getType()) {
                    case STRING -> value.setStringValue(cellValue);
                    case NUMERIC -> value.setNumericValue(Double.parseDouble(cellValue));
                    case TIMESTAMP -> value.setTimestampValue(LocalDateTime.parse(cellValue, DEFAULT_DATE_FORMAT));
                    case BOOLEAN -> value.setBooleanValue(Boolean.parseBoolean(cellValue));
                }
            } catch (Exception e) {
                switch (group.getType()) {
                    case STRING -> value.setStringValue("");
                    case NUMERIC -> value.setNumericValue(0.0);
                    case TIMESTAMP -> value.setTimestampValue(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
                    case BOOLEAN -> value.setBooleanValue(false);
                }
            }

            return value;
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            var cellValue = worksheet.getRow(y).getCell(x).getBooleanCellValue();

            switch (group.getType()) {
                case STRING -> value.setStringValue(Boolean.toString(cellValue));
                case NUMERIC -> value.setNumericValue(0.0);
                case TIMESTAMP -> value.setTimestampValue(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
                case BOOLEAN -> value.setBooleanValue(cellValue);
            }

            return value;
        }

        if (DateUtil.isCellDateFormatted(cell)) {
            var cellValue = cell.getLocalDateTimeCellValue();

            switch (group.getType()) {
                case STRING -> value.setStringValue("");
                case NUMERIC -> value.setNumericValue(0.0);
                case TIMESTAMP -> value.setTimestampValue(cellValue);
                case BOOLEAN -> value.setBooleanValue(false);
            }

            return value;
        }

        var cellValue = cell.getNumericCellValue();

        switch (group.getType()) {
            case STRING -> value.setStringValue(Double.toString(cellValue));
            case NUMERIC -> value.setNumericValue(cellValue);
            case TIMESTAMP -> value.setTimestampValue(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
            case BOOLEAN -> value.setBooleanValue(false);
        }

        return value;
    }

    protected Optional<RawTransactionValueType> determineGroupType(final Sheet worksheet, final int rowIndex, final int columnIndex) {
        var cell = worksheet.getRow(rowIndex).getCell(columnIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return Optional.empty();
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return Optional.of(BOOLEAN);
        }

        if (cell.getCellType() == CellType.STRING) {
            return Optional.of(STRING);
        }

        if (DateUtil.isCellDateFormatted(cell)) {
            return Optional.of(TIMESTAMP);
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return Optional.of(NUMERIC);
        }

        var cellValue = cell.getStringCellValue();

        if (isValidDate(cellValue, DEFAULT_DATE_FORMAT)) {
            return Optional.of(RawTransactionValueType.TIMESTAMP);
        }

        return Optional.empty();
    }

    protected boolean isValidDate(String stringDate, DateTimeFormatter formatter) {
        try {
            formatter.parse(stringDate);
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

}
