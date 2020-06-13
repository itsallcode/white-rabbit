package org.itsallcode.whiterabbit.jfxui.activities;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ActivitiesTable
{
    private static final Logger LOG = LogManager.getLogger(ActivitiesTable.class);

    private final ObservableList<Activity> activities = FXCollections.observableArrayList();
    private final ActivityEditListener editListener;

    public ActivitiesTable(ReadOnlyProperty<DayRecord> selectedDay, ActivityEditListener editListener)
    {
        this.editListener = editListener;
        selectedDay.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    public void updateTableValues(DayRecord day)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            activities.clear();
            if (day != null)
            {
                final List<Activity> selectedDayActivities = day.getActivities();
                LOG.debug("Day {} selected with {} activities", day.getDate(), selectedDayActivities.size());
                activities.addAll(selectedDayActivities);
            }
        });
    }

    public Node initTable()
    {
        final TableView<Activity> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("activities-table");
        return table;
    }

    private List<TableColumn<Activity, ?>> createColumns()
    {
        return asList(createReadonlyColumn("projectId", "Project", Activity::getProjectId));
    }

    private <T> TableColumn<Activity, T> createEditableColumn(String id, String label, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter, StringConverter<T> stringConverter)
    {
        return createEditableColumn(id, label, getter, setter, param -> new TextFieldTableCell<>(stringConverter));
    }

    private <T> TableColumn<Activity, T> createEditableColumn(String id, String label, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter, Callback<TableColumn<Activity, T>, TableCell<Activity, T>> cellFactory)
    {
        final TableColumn<Activity, T> column = createColumn(id, label);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(
                data -> new ReadOnlyObjectWrapper<>(data.getValue() != null ? getter.apply(data.getValue()) : null));
        column.setOnEditCommit(editCommitHandler(setter));
        column.setEditable(true);
        return column;
    }

    private <T> TableColumn<Activity, String> createReadonlyColumn(String id, String label,
            Function<Activity, T> getter)
    {
        return createReadonlyColumn(id, label, getter, T::toString);
    }

    private <T> TableColumn<Activity, String> createReadonlyColumn(String id, String label,
            Function<Activity, T> getter, Function<T, String> formatter)
    {
        final TableColumn<Activity, String> column = createColumn(id, label);
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(formatValue(data, getter, formatter)));
        column.setEditable(false);
        return column;
    }

    private <T> String formatValue(CellDataFeatures<Activity, String> data, Function<Activity, T> getter,
            Function<T, String> formatter)
    {
        final Activity value = data.getValue();
        if (value == null)
        {
            return null;
        }
        final T rawValue = getter.apply(value);
        return rawValue != null ? formatter.apply(rawValue) : null;
    }

    private <T> TableColumn<Activity, T> createColumn(String id, String label)
    {
        final TableColumn<Activity, T> column = new TableColumn<>(label);
        column.setSortable(false);
        column.setId(id);
        return column;
    }

    private <T> EventHandler<CellEditEvent<Activity, T>> editCommitHandler(BiConsumer<Activity, T> setter)
    {
        return event -> {
            final Activity rowValue = event.getRowValue();
            if (rowValue == null)
            {
                return;
            }
            setter.accept(rowValue, event.getNewValue());
            editListener.recordUpdated(rowValue);
        };
    }
}
