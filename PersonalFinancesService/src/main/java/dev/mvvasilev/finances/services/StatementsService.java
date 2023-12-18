package dev.mvvasilev.finances.services;

import dev.mvvasilev.finances.dtos.TransactionValueGroupDTO;
import dev.mvvasilev.finances.dtos.UploadedStatementDTO;
import dev.mvvasilev.finances.entity.RawStatement;
import dev.mvvasilev.finances.entity.RawTransactionValue;
import dev.mvvasilev.finances.entity.RawTransactionValueGroup;
import dev.mvvasilev.finances.enums.RawTransactionValueType;
import dev.mvvasilev.finances.persistence.RawStatementRepository;
import dev.mvvasilev.finances.persistence.RawTransactionValueGroupRepository;
import dev.mvvasilev.finances.persistence.RawTransactionValueRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatementsService {

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
    public StatementsService(RawStatementRepository rawStatementRepository, RawTransactionValueGroupRepository rawTransactionValueGroupRepository, RawTransactionValueRepository rawTransactionValueRepository) {
        this.rawStatementRepository = rawStatementRepository;
        this.rawTransactionValueGroupRepository = rawTransactionValueGroupRepository;
        this.rawTransactionValueRepository = rawTransactionValueRepository;
    }


    public void uploadStatementFromExcelSheetForUser(String fileName, String mimeType, InputStream workbookInputStream, int userId) throws IOException {

        var workbook = WorkbookFactory.create(workbookInputStream);

        var firstWorksheet = workbook.getSheetAt(0);

        var lastRowIndex = firstWorksheet.getLastRowNum();

        var statement = new RawStatement();
        statement.setUserId(userId);
        statement.setName(fileName);

        statement = rawStatementRepository.saveAndFlush(statement);

        var firstRow = firstWorksheet.getRow(0);

        List<RawTransactionValueGroup> valueGroups = new ArrayList<>();

        // turn each column into a value group
        for (var c : firstRow) {

            if (c == null || c.getCellType() == CellType.BLANK) {
                break;
            }

            var transactionValueGroup = new RawTransactionValueGroup();

            transactionValueGroup.setStatementId(statement.getId());
            transactionValueGroup.setName(c.getStringCellValue());

            // group type is string by default, if no other type could have been determined
            var groupType = RawTransactionValueType.STRING;

            // iterate down through the rows on this column, looking for the first one to return a type
            for (int y = c.getRowIndex() + 1; y <= lastRowIndex; y++) {
                var typeResult = determineGroupType(firstWorksheet, y, c.getColumnIndex());

                // if a type has been determined, stop here
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

        // turn each cell in each row into a value, related to the value group ( column )
        for (var group : valueGroups) {
            var valueList = new ArrayList<RawTransactionValue>();

            for (int y = 1; y < lastRowIndex; y++) {
                var value = new RawTransactionValue();

                value.setGroupId(group.getId());
                value.setRowIndex(y);

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

    private boolean isValidDate(String stringDate) {
        try {
            DATE_FORMAT.parse(stringDate);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public Collection<UploadedStatementDTO> fetchStatementsForUser(int userId) {
        return rawStatementRepository.fetchAllForUser(userId)
                .stream()
                .map(dto -> new UploadedStatementDTO(dto.getId(), dto.getName(), dto.getTimeCreated()))
                .collect(Collectors.toList());
    }

    public Collection<TransactionValueGroupDTO> fetchTransactionValueGroupsForUserStatement(Long statementId, int userId) {
        return rawTransactionValueGroupRepository.fetchAllForStatementAndUser(statementId, userId)
                .stream()
                .map(dto -> new TransactionValueGroupDTO(dto.getId(), dto.getName(), RawTransactionValueType.values()[dto.getType()]))
                .collect(Collectors.toList());
    }

    public void deleteStatement(Long statementId, int userId) {
        rawStatementRepository.deleteById(statementId);
        rawStatementRepository.flush();
    }
}
