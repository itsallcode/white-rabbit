package org.itsallcode.whiterabbit.jfxui.activities;

import static java.util.Arrays.asList;

import java.util.List;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class ActivitiesTable
{
    private final ObservableList<ActivityPropertyAdapter> activities = FXCollections.observableArrayList();
    private ActivityEditListener editListener;

    public ActivitiesTable(ReadOnlyProperty<DayRecord> readOnlyProperty)
    {
        readOnlyProperty.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    private void updateTableValues(DayRecord day)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            activities.clear();
            for (final Activity activity : day.getActivities())
            {
                final ActivityPropertyAdapter adapter = new ActivityPropertyAdapter(editListener);
                activities.add(adapter);
                adapter.update(activity);
            }
        });
    }

    public Node initTable()
    {
        final TableView<ActivityPropertyAdapter> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("day-table");
        return table;
    }

    private List<TableColumn<ActivityPropertyAdapter, ?>> createColumns()
    {
        return asList(readOnlyColumn("project", "Project", param -> new TextFieldTableCell<>(),
                data -> data.getValue().projectId));
    }

    private <T> TableColumn<ActivityPropertyAdapter, T> readOnlyColumn(String id, String label,
            Callback<TableColumn<ActivityPropertyAdapter, T>, TableCell<ActivityPropertyAdapter, T>> cellFactory,
            Callback<CellDataFeatures<ActivityPropertyAdapter, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, false);
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
}
