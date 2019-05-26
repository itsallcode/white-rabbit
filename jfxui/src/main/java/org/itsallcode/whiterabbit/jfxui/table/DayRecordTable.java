package org.itsallcode.whiterabbit.jfxui.table;

import static java.util.Arrays.asList;

import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.beans.property.ObjectProperty;
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

    public DayRecordTable(ObjectProperty<MonthIndex> currentMonth, RecordEditListener editListener,
            FormatterService formatterService)
    {
        this.editListener = editListener;
        this.formatterService = formatterService;

        currentMonth.addListener((observable, oldValue, newValue) -> newValue.getSortedDays()
                .forEach(this::recordUpdated));
    }

    public Node initTable()
    {
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("day-table");

        return table;
    }

    private List<TableColumn<DayRecord, ?>> createColumns()
    {
        return asList(createReadonlyColumn("date", "Date", DayRecord::getDate),
                createEditableColumn("day-type", "Type", DayRecord::getType,
                        (record, type) -> record.setType(type),
                        (TableColumn<DayRecord, DayType> data) -> new ChoiceBoxTableCell<>(
                                new DayTypeStringConverter(), DayType.values())),
                createEditableColumn("begin", "Begin", DayRecord::getBegin,
                        (record, begin) -> record.setBegin(begin),
                        new LocalTimeStringConverter(FormatStyle.SHORT)),
                createEditableColumn("end", "End", DayRecord::getEnd,
                        (record, end) -> record.setEnd(end),
                        new LocalTimeStringConverter(FormatStyle.SHORT)),
                createReadonlyColumn("break", "Break", DayRecord::getMandatoryBreak,
                        formatterService::format),
                createEditableColumn("interruption", "Interruption", DayRecord::getInterruption,
                        (record, interruption) -> record.setInterruption(interruption),
                        new DurationStringConverter(formatterService)),
                createReadonlyColumn("working-time", "Working time", DayRecord::getWorkingTime,
                        formatterService::format),
                createReadonlyColumn("overtime", "Overtime", DayRecord::getOvertime,
                        formatterService::format),
                createReadonlyColumn("total-overtime", "Total Overtime",
                        DayRecord::getTotalOvertime, formatterService::format),
                createEditableColumn("comment", "Comment", DayRecord::getComment,
                        (record, comment) -> record.setComment(comment),
                        new DefaultStringConverter()));
    }

    private <T> TableColumn<DayRecord, T> createEditableColumn(String id, String label,
            Function<DayRecord, T> getter, BiConsumer<DayRecord, T> setter,
            StringConverter<T> stringConverter)
    {
        return createEditableColumn(id, label, getter, setter,
                param -> new TextFieldTableCell<>(stringConverter));
    }

    private <T> TableColumn<DayRecord, T> createEditableColumn(String id, String label,
            Function<DayRecord, T> getter, BiConsumer<DayRecord, T> setter,
            Callback<TableColumn<DayRecord, T>, TableCell<DayRecord, T>> cellFactory)
    {
        final TableColumn<DayRecord, T> column = createColumn(id, label);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue() != null ? getter.apply(data.getValue()) : null));
        column.setOnEditCommit(editCommitHandler(setter));
        column.setEditable(true);
        return column;
    }

    private <T> TableColumn<DayRecord, String> createReadonlyColumn(String id, String label,
            Function<DayRecord, T> getter)
    {
        return createReadonlyColumn(id, label, getter, T::toString);
    }

    private <T> TableColumn<DayRecord, String> createReadonlyColumn(String id, String label,
            Function<DayRecord, T> getter, Function<T, String> formatter)
    {
        final TableColumn<DayRecord, String> column = createColumn(id, label);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue() != null ? formatter.apply(getter.apply(data.getValue())) : null));
        column.setEditable(false);
        return column;
    }

    private <T> TableColumn<DayRecord, T> createColumn(String id, String label)
    {
        final TableColumn<DayRecord, T> column = new TableColumn<>(label);
        column.setSortable(false);
        column.setId(id);
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
        JavaFxUtil.runOnFxApplicationThread(() -> {
            final int recordIndex = record.getDate().getDayOfMonth() - 1;
            while (dayRecords.size() <= recordIndex)
            {
                dayRecords.add(null);
            }
            dayRecords.set(recordIndex, record);
        });
    }
}
