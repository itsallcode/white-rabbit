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
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.converter.DayTypeStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.UiWidget;
import org.itsallcode.whiterabbit.jfxui.ui.widget.AutoCompleteTextField;
import org.itsallcode.whiterabbit.jfxui.ui.widget.PersistOnFocusLossTextFieldTableCell;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteService;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.DayType;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;

public class DayRecordTable
{
    private static final Logger LOG = LogManager.getLogger(DayRecordTable.class);

    private final ObservableList<DayRecordPropertyAdapter> dayRecords = FXCollections.observableArrayList();
    private final EditListener<DayRecord> editListener;
    private final FormatterService formatterService;
    private final Locale locale;
    private final SimpleObjectProperty<DayRecord> selectedDay;
    private TableView<DayRecordPropertyAdapter> table;

    private final AutocompleteService autocompleteService;

    public DayRecordTable(Locale locale, SimpleObjectProperty<DayRecord> selectedDay,
            ObjectProperty<MonthIndex> currentMonth, EditListener<DayRecord> editListener,
            FormatterService formatterService, AutocompleteService autocompleteService)
    {
        this.editListener = editListener;
        this.formatterService = formatterService;
        this.locale = locale;
        this.selectedDay = selectedDay;
        this.autocompleteService = autocompleteService;
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
                .addListener((observable, oldValue, newValue) -> {
                    LOG.debug("Table row selected: {}", newValue.getRecord());
                    selectedDay.set(newValue.getRecord());
                });
        return table;
    }

    public void selectRow(LocalDate date)
    {
        Objects.requireNonNull(table, "Table not yet initialized");
        final int row = date.getDayOfMonth() - 1;
        final DayRecordPropertyAdapter rowItem = table.getItems().get(row);
        LOG.debug("Select table row {} (item {}) for {}", row, rowItem.getRecord(), date);
        table.getSelectionModel().select(rowItem);
        table.scrollTo(rowItem);
    }

    private List<TableColumn<DayRecordPropertyAdapter, ?>> createColumns()
    {
        final TableColumn<DayRecordPropertyAdapter, LocalDate> dateCol = UiWidget.readOnlyColumn("date", "Date",
                param -> new PersistOnFocusLossTextFieldTableCell<>(
                        new LocalDateStringConverter(DateTimeFormatter.ofPattern("E dd.MM.", locale), null)),
                data -> data.getValue().date);
        final DurationStringConverter durationConverter = new DurationStringConverter(formatterService);
        final LocalTimeStringConverter localTimeConverter = new LocalTimeStringConverter(FormatStyle.SHORT, locale);
        final TableColumn<DayRecordPropertyAdapter, @NonNull DayType> dayTypeCol = UiWidget.column("day-type", "Type",
                param -> new ChoiceBoxTableCell<>(new DayTypeStringConverter(), DayType.values()),
                data -> data.getValue().dayType);
        final TableColumn<DayRecordPropertyAdapter, LocalTime> beginCol = UiWidget.column("begin", "Begin",
                param -> new PersistOnFocusLossTextFieldTableCell<>(localTimeConverter),
                data -> data.getValue().begin);
        final TableColumn<DayRecordPropertyAdapter, LocalTime> endCol = UiWidget.column("end", "End",
                param -> new PersistOnFocusLossTextFieldTableCell<>(localTimeConverter),
                data -> data.getValue().end);
        final TableColumn<DayRecordPropertyAdapter, Duration> breakCol = UiWidget.readOnlyColumn("break", "Break",
                param -> new PersistOnFocusLossTextFieldTableCell<>(durationConverter),
                data -> data.getValue().mandatoryBreak);
        final TableColumn<DayRecordPropertyAdapter, Duration> interruptionCol = UiWidget.column("interruption",
                "Interruption",
                param -> new PersistOnFocusLossTextFieldTableCell<>(durationConverter),
                data -> data.getValue().interruption);
        final TableColumn<DayRecordPropertyAdapter, Duration> workingTimeCol = UiWidget.readOnlyColumn("working-time",
                "Working time",
                param -> new PersistOnFocusLossTextFieldTableCell<>(durationConverter),
                data -> data.getValue().workingTime);
        final TableColumn<DayRecordPropertyAdapter, Duration> overTimeCol = UiWidget.readOnlyColumn("overtime",
                "Overtime",
                param -> new PersistOnFocusLossTextFieldTableCell<>(durationConverter),
                data -> data.getValue().overtime);
        final TableColumn<DayRecordPropertyAdapter, Duration> totalOvertimeCol = UiWidget.readOnlyColumn(
                "total-overtime",
                "Total Overtime",
                param -> new PersistOnFocusLossTextFieldTableCell<>(durationConverter),
                data -> data.getValue().totalOvertime);
        final TableColumn<DayRecordPropertyAdapter, String> commentCol = UiWidget.column("comment", "Comment",
                param -> new PersistOnFocusLossTextFieldTableCell<>(new DefaultStringConverter(),
                        () -> new AutoCompleteTextField(autocompleteService.dayCommentAutocompleter())),
                data -> data.getValue().comment);

        return asList(dateCol, dayTypeCol, beginCol, endCol, breakCol, interruptionCol, workingTimeCol, overTimeCol,
                totalOvertimeCol, commentCol);
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
