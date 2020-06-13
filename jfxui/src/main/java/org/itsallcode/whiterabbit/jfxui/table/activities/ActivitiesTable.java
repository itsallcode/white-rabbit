package org.itsallcode.whiterabbit.jfxui.table.activities;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.DurationStringConverter;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.FormatterService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ActivitiesTable
{
    private static final Logger LOG = LogManager.getLogger(ActivitiesTable.class);

    private final ObservableList<Activity> activities = FXCollections.observableArrayList();
    private final ActivityEditListener editListener;
    private final FormatterService formatterService;

    public ActivitiesTable(ReadOnlyProperty<DayRecord> selectedDay, ActivityEditListener editListener,
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
                activities.addAll(selectedDayActivities);
            }
        });
    }

    public TableView<Activity> initTable()
    {
        final TableView<Activity> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("activities-table");
        return table;
    }

    private List<TableColumn<Activity, ?>> createColumns()
    {
        final Callback<TableColumn<Activity, Boolean>, TableCell<Activity, Boolean>> cellFactory = new Callback<TableColumn<Activity, Boolean>, TableCell<Activity, Boolean>>()
        {
            @Override
            public TableCell<Activity, Boolean> call(TableColumn<Activity, Boolean> tableColumn)
            {
                final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty = new Callback<Integer, ObservableValue<Boolean>>()
                {
                    @Override
                    public ObservableValue<Boolean> call(Integer index)
                    {
                        final Activity activity = tableColumn.getTableView().getItems().get(index);
                        final SimpleBooleanProperty property = new SimpleBooleanProperty(
                                activity.isRemainderActivity());
                        property.addListener(new ChangeListener<Boolean>()
                        {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                    Boolean newValue)
                            {
                                activity.setRemainderActivity(newValue);
                            }
                        });
                        return property;
                    }
                };
                return new CheckBoxTableCell<>(getSelectedProperty);
            }
        };
        return asList(createEditableColumn("projectId", "Project", Activity::getProjectId,
                (activity, projectId) -> activity.setProjectId(projectId), new DefaultStringConverter()),

                createEditableColumn("duration", "Duration", Activity::getDuration,
                        (activity, duration) -> activity.setDuration(duration),
                        param -> new TextFieldTableCell<>(new DurationStringConverter(formatterService))),

                createEditableColumn("remainder", "Remainder",
                        Activity::isRemainderActivity,
                        (activity, remainder) -> activity.setRemainderActivity(remainder),
                        cellFactory),

                createEditableColumn("comment", "Comment", Activity::getComment,
                        (activity, comment) -> activity.setComment(comment), new DefaultStringConverter()));
    }

    private <T> TableColumn<Activity, T> createEditableColumn(String id, String label, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter, StringConverter<T> stringConverter)
    {
        return createEditableColumn(id, label, getter, setter, param -> new TextFieldTableCell<>(stringConverter));
    }

    private <T> TableColumn<Activity, T> createEditableColumn(String id, String label, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter, Callback<TableColumn<Activity, T>, TableCell<Activity, T>> cellFactory)
    {
        final TableColumn<Activity, T> column = new TableColumn<>(label);
        column.setId(id);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(
                data -> new ReadOnlyObjectWrapper<>(data.getValue() != null ? getter.apply(data.getValue()) : null));
        column.setOnEditCommit(editCommitHandler(setter));
        column.setEditable(true);
        column.setSortable(false);
        return column;
    }

    private <T> EventHandler<CellEditEvent<Activity, T>> editCommitHandler(BiConsumer<Activity, T> setter)
    {
        return event -> {
            final int row = event.getTablePosition().getRow();
            final Activity rowValue = event.getRowValue();
            if (rowValue == null)
            {
                return;
            }
            setter.accept(rowValue, event.getNewValue());
            editListener.recordUpdated(row, rowValue);
        };
    }
}
