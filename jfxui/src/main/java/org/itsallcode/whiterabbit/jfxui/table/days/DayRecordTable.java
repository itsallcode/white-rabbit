package org.itsallcode.whiterabbit.jfxui.table.days;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.DurationStringConverter;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class DayRecordTable
{
    private final ObservableList<DayRecordPropertyAdapter> dayRecords = FXCollections.observableArrayList();
    private final DayRecordEditListener editListener;
    private final FormatterService formatterService;
    private final Locale locale;
    private final SimpleObjectProperty<DayRecord> selectedDay = new SimpleObjectProperty<>(null);
    private TableView<DayRecordPropertyAdapter> table;

    public DayRecordTable(Locale locale, ObjectProperty<MonthIndex> currentMonth, DayRecordEditListener editListener,
            FormatterService formatterService)
    {
        this.editListener = editListener;
        this.formatterService = formatterService;
        this.locale = locale;
        fillTableWith31EmptyRows();
        currentMonth.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    public TableView<DayRecordPropertyAdapter> initTable()
    {
        table = new TableView<>(dayRecords);
        table.getStylesheets().add("org/itsallcode/whiterabbit/jfxui/table/style.css");
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("day-table");
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty()
                .addListener((ChangeListener<DayRecordPropertyAdapter>) (observable, oldValue, newValue) -> {
                    selectedDay.set(newValue.recordProperty.get());
                });
        return table;
    }

    public ReadOnlyProperty<DayRecord> selectedDay()
    {
        return selectedDay;
    }

    private List<TableColumn<DayRecordPropertyAdapter, ?>> createColumns()
    {
        final TableColumn<DayRecordPropertyAdapter, LocalDate> dateCol = readOnlyColumn("date", "Date",
                param -> new TextFieldTableCell<>(
                        new LocalDateStringConverter(DateTimeFormatter.ofPattern("E dd.MM.", locale), null)),
                data -> data.getValue().date);
        final TableColumn<DayRecordPropertyAdapter, DayType> dayTypeCol = column("day-type", "Type",
                param -> new ChoiceBoxTableCell<>(new DayTypeStringConverter(), DayType.values()),
                data -> data.getValue().dayType);
        final TableColumn<DayRecordPropertyAdapter, LocalTime> beginCol = column("begin", "Begin",
                param -> new TextFieldTableCell<>(new LocalTimeStringConverter(FormatStyle.SHORT)),
                data -> data.getValue().begin);
        final TableColumn<DayRecordPropertyAdapter, LocalTime> endCol = column("end", "End",
                param -> new TextFieldTableCell<>(new LocalTimeStringConverter(FormatStyle.SHORT)),
                data -> data.getValue().end);
        final TableColumn<DayRecordPropertyAdapter, Duration> breakCol = readOnlyColumn("break", "Break",
                param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().mandatoryBreak);
        final TableColumn<DayRecordPropertyAdapter, Duration> interruptionCol = column("interruption", "Interruption",
                param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().interruption);
        final TableColumn<DayRecordPropertyAdapter, Duration> workingTimeCol = readOnlyColumn("working-time",
                "Working time", param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().workingTime);
        final TableColumn<DayRecordPropertyAdapter, Duration> overTimeCol = readOnlyColumn("overtime", "Overtime",
                param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().overtime);
        final TableColumn<DayRecordPropertyAdapter, Duration> totalOvertimeCol = readOnlyColumn("total-overtime",
                "Total Overtime", param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().totalOvertime);
        final TableColumn<DayRecordPropertyAdapter, String> commentCol = column("comment", "Comment",
                param -> new TextFieldTableCell<>(new DefaultStringConverter()), data -> data.getValue().comment);

        return asList(dateCol, dayTypeCol, beginCol, endCol, breakCol, interruptionCol, workingTimeCol, overTimeCol,
                totalOvertimeCol, commentCol);
    }

    private <T> TableColumn<DayRecordPropertyAdapter, T> readOnlyColumn(String id, String label,
            Callback<TableColumn<DayRecordPropertyAdapter, T>, TableCell<DayRecordPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<DayRecordPropertyAdapter, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, false);
    }

    private <T> TableColumn<DayRecordPropertyAdapter, T> column(String id, String label,
            Callback<TableColumn<DayRecordPropertyAdapter, T>, TableCell<DayRecordPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<DayRecordPropertyAdapter, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, true);
    }

    private <T> TableColumn<DayRecordPropertyAdapter, T> column(String id, String label,
            Callback<TableColumn<DayRecordPropertyAdapter, T>, TableCell<DayRecordPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<DayRecordPropertyAdapter, T>, ObservableValue<T>> cellValueFactory,
            boolean editable)
    {
        final TableColumn<DayRecordPropertyAdapter, T> column = new TableColumn<>(label);
        column.setSortable(false);
        column.setId(id);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(cellValueFactory);
        column.setEditable(editable);
        column.setResizable(true);
        return column;
    }

    private void updateTableValues(MonthIndex newValue)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            final List<DayRecord> sortedDays = newValue.getSortedDays().collect(toList());
            int index = 0;
            for (final DayRecordPropertyAdapter row : dayRecords)
            {
                if (sortedDays.size() <= index)
                {
                    row.clear();
                }
                else
                {
                    row.update(sortedDays.get(index));
                }
                index++;
            }
        });
    }

    private void fillTableWith31EmptyRows()
    {
        while (dayRecords.size() < 31)
        {
            dayRecords.add(new DayRecordPropertyAdapter(editListener));
        }
    }
}
