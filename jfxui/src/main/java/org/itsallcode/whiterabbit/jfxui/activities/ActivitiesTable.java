package org.itsallcode.whiterabbit.jfxui.activities;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.List;

import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ActivitiesTable
{
    private final ObservableList<Activity> activities = FXCollections.observableArrayList();

    public ActivitiesTable(ReadOnlyProperty<DayRecord> readOnlyProperty)
    {
        readOnlyProperty.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    private void updateTableValues(DayRecord day)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            activities.clear();
            activities.addAll(day.getActivities());
        });
    }

    public Node initTable()
    {
        final TableView<Activity> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("day-table");

        return table;
    }

    private List<TableColumn<Activity, ?>> createColumns()
    {
        final TableColumn<Activity, String> projectId = new TableColumn<>();
        final TableColumn<Activity, Duration> duration = new TableColumn<>();
        final TableColumn<Activity, String> comment = new TableColumn<>();
        return asList(projectId, duration, comment);
    }
}
