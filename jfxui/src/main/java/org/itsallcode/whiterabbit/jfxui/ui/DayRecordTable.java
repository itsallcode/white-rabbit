package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.Arrays.asList;

import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class DayRecordTable
{
    private final ObservableList<DayRecord> dayRecords = FXCollections.observableArrayList();
    private final TableView<DayRecord> table = new TableView<>(dayRecords);
    private final RecordEditListener editListener;
    private final FormatterService formatterService;

    public DayRecordTable(RecordEditListener editListener, FormatterService formatterService)
    {
        this.editListener = editListener;
        this.formatterService = formatterService;
    }

    public Node initTable()
    {
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        return table;
    }

    private List<TableColumn<DayRecord, ?>> createColumns()
    {
        return asList(createReadonlyColumn("Date", DayRecord::getDate),
                createEditableColumn("Type", DayRecord::getType,
                        (record, type) -> record.setType(type),
                        (TableColumn<DayRecord, DayType> data) -> new ChoiceBoxTableCell<>(
                                new DayTypeStringConverter(), DayType.values())),
                createEditableColumn("Begin", DayRecord::getBegin,
                        (record, begin) -> record.setBegin(begin),
                        new LocalTimeStringConverter(FormatStyle.SHORT)),
                createEditableColumn("End", DayRecord::getEnd, (record, end) -> record.setEnd(end),
                        new LocalTimeStringConverter(FormatStyle.SHORT)),
                createReadonlyColumn("Break", DayRecord::getMandatoryBreak,
                        formatterService::format),
                createEditableColumn("Interruption", DayRecord::getInterruption,
                        (record, interruption) -> record.setInterruption(interruption),
                        new DurationStringConverter(formatterService)),
                createReadonlyColumn("Overtime", DayRecord::getOvertime, formatterService::format),
                createReadonlyColumn("Total Overtime", DayRecord::getTotalOvertime,
                        formatterService::format),
                createEditableColumn("Comment", DayRecord::getComment,
                        (record, comment) -> record.setComment(comment),
                        new DefaultStringConverter()));
    }

    private <T> TableColumn<DayRecord, T> createEditableColumn(String label,
            Function<DayRecord, T> getter, BiConsumer<DayRecord, T> setter,
            StringConverter<T> stringConverter)
    {
        return createEditableColumn(label, getter, setter,
                param -> new TextFieldTableCell<>(stringConverter));
    }

    private <T> TableColumn<DayRecord, T> createEditableColumn(String label,
            Function<DayRecord, T> getter, BiConsumer<DayRecord, T> setter,
            Callback<TableColumn<DayRecord, T>, TableCell<DayRecord, T>> cellFactory)
    {
        final TableColumn<DayRecord, T> column = new TableColumn<>(label);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue() != null ? getter.apply(data.getValue()) : null));
        column.setOnEditCommit(editCommitHandler(setter));
        column.setEditable(true);
        return column;
    }

    private <T> TableColumn<DayRecord, String> createReadonlyColumn(String label,
            Function<DayRecord, T> getter)
    {
        return createReadonlyColumn(label, getter, T::toString);
    }

    private <T> TableColumn<DayRecord, String> createReadonlyColumn(String label,
            Function<DayRecord, T> getter, Function<T, String> formatter)
    {
        final TableColumn<DayRecord, String> column = new TableColumn<>(label);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue() != null ? formatter.apply(getter.apply(data.getValue())) : null));
        column.setEditable(false);
        return column;
    }

    private <T> EventHandler<CellEditEvent<DayRecord, T>> editCommitHandler(
            BiConsumer<DayRecord, T> setter)
    {
        return event -> {
            final DayRecord rowValue = event.getRowValue();
            if (rowValue == null)
            {
                return;
            }
            setter.accept(rowValue, event.getNewValue());
            editListener.recordUpdated(rowValue);
        };
    }

    public void recordUpdated(DayRecord record)
    {
        Platform.runLater(() -> {
            final int recordIndex = record.getDate().getDayOfMonth() - 1;
            while (dayRecords.size() <= recordIndex)
            {
                dayRecords.add(null);
            }
            dayRecords.set(recordIndex, record);
        });
    }
}
