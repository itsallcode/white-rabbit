package org.itsallcode.whiterabbit.jfxui.table.activities;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class ActivitiesTable
{
    private static final Logger LOG = LogManager.getLogger(ActivitiesTable.class);

    private final ObservableList<ActivityPropertyAdapter> activities = FXCollections.observableArrayList();
    private final EditListener<DayRecord> editListener;
    private final FormatterService formatterService;

    public ActivitiesTable(ReadOnlyProperty<DayRecord> selectedDay, EditListener<DayRecord> editListener,
            FormatterService formatterService)
    {
        this.editListener = editListener;
        this.formatterService = formatterService;
        selectedDay.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    public void updateTableValues(DayRecord day)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            activities.clear();
            if (day != null)
            {
                final List<Activity> selectedDayActivities = day.activities().getAll();
                LOG.trace("Day {} selected with {} activities", day.getDate(), selectedDayActivities.size());
                activities.addAll(ActivityPropertyAdapter.wrap(editListener, selectedDayActivities));
            }
        });
    }

    public TableView<ActivityPropertyAdapter> initTable()
    {
        final TableView<ActivityPropertyAdapter> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("activities-table");
        return table;
    }

    private List<TableColumn<ActivityPropertyAdapter, ?>> createColumns()
    {
        final Callback<TableColumn<ActivityPropertyAdapter, Boolean>, TableCell<ActivityPropertyAdapter, Boolean>> cellFactory = new Callback<TableColumn<ActivityPropertyAdapter, Boolean>, TableCell<ActivityPropertyAdapter, Boolean>>()
        {
            @Override
            public TableCell<ActivityPropertyAdapter, Boolean> call(
                    TableColumn<ActivityPropertyAdapter, Boolean> tableColumn)
            {
                final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty = new Callback<Integer, ObservableValue<Boolean>>()
                {
                    @Override
                    public ObservableValue<Boolean> call(Integer index)
                    {
                        final ActivityPropertyAdapter activity = tableColumn.getTableView().getItems().get(index);
                        final SimpleBooleanProperty prop = new SimpleBooleanProperty(activity.remainder.get());
                        Bindings.bindBidirectional(activity.remainder, prop);
                        return prop;
                    }
                };
                return new CheckBoxTableCell<>(getSelectedProperty);
            }
        };
        final TableColumn<ActivityPropertyAdapter, String> projectCol = column("project", "Project",
                param -> new TextFieldTableCell<>(new DefaultStringConverter()), data -> data.getValue().projectId);
        final TableColumn<ActivityPropertyAdapter, Duration> durationCol = column("duration", "Duration",
                param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().duration);
        final TableColumn<ActivityPropertyAdapter, Boolean> remainderCol = column("remainder", "Remainder",
                cellFactory, data -> data.getValue().remainder);
        final TableColumn<ActivityPropertyAdapter, String> commentCol = column("comment", "Comment",
                param -> new TextFieldTableCell<>(new DefaultStringConverter()), data -> data.getValue().comment);

        return asList(projectCol, durationCol, remainderCol, commentCol);
    }

    private <T> TableColumn<ActivityPropertyAdapter, T> column(String id, String label,
            Callback<TableColumn<ActivityPropertyAdapter, T>, TableCell<ActivityPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<ActivityPropertyAdapter, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, true);
    }

    private <T> TableColumn<ActivityPropertyAdapter, T> column(String id, String label,
            Callback<TableColumn<ActivityPropertyAdapter, T>, TableCell<ActivityPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<ActivityPropertyAdapter, T>, ObservableValue<T>> cellValueFactory,
            boolean editable)
    {
        final TableColumn<ActivityPropertyAdapter, T> column = new TableColumn<>(label);
        column.setSortable(false);
        column.setId(id);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(cellValueFactory);
        column.setEditable(editable);
        column.setResizable(true);
        return column;
    }

    public void refresh()
    {
        activities.forEach(ActivityPropertyAdapter::update);
    }
}
