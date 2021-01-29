package org.itsallcode.whiterabbit.jfxui.table.activities;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.jfxui.table.converter.ProjectStringConverter;
import org.itsallcode.whiterabbit.jfxui.ui.UiWidget;
import org.itsallcode.whiterabbit.jfxui.ui.widget.AutoCompleteTextField;
import org.itsallcode.whiterabbit.jfxui.ui.widget.PersistOnFocusLossTextFieldTableCell;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteService;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class ActivitiesTable
{
    private static final Logger LOG = LogManager.getLogger(ActivitiesTable.class);

    private final ObservableList<ActivityPropertyAdapter> activities = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Activity> selectedActivity;

    private final EditListener<DayRecord> editListener;
    private final FormatterService formatterService;
    private final ProjectService projectService;
    private final AutocompleteService autocompleteService;

    public ActivitiesTable(ReadOnlyProperty<DayRecord> selectedDay, SimpleObjectProperty<Activity> selectedActivity,
            EditListener<DayRecord> editListener, FormatterService formatterService, ProjectService projectService,
            AutocompleteService autocompleteService)
    {
        this.selectedActivity = selectedActivity;
        this.editListener = editListener;
        this.formatterService = formatterService;
        this.projectService = projectService;
        this.autocompleteService = autocompleteService;
        selectedDay.addListener((observable, oldValue, newValue) -> updateTableValues(newValue));
    }

    public void updateTableValues(DayRecord day)
    {
        JavaFxUtil.runOnFxApplicationThread(() -> {
            if (day == null || day.activities().isEmpty())
            {
                LOG.trace("No day selected or no activities: clear list of activities");
                activities.clear();
                return;
            }
            final List<Activity> selectedDayActivities = day.activities().getAll();
            LOG.trace("Day {} selected with {} activities", day.getDate(), selectedDayActivities.size());

            removeSurplusRows(selectedDayActivities);
            updateRows(selectedDayActivities);
        });
    }

    private void removeSurplusRows(final List<Activity> selectedDayActivities)
    {
        final int activitiesToRemove = Math.max(0, activities.size() - selectedDayActivities.size());
        LOG.trace("Removing {} surplus rows of {}", activitiesToRemove, activities.size());
        for (int i = 0; i < activitiesToRemove; i++)
        {
            activities.remove(activities.size() - 1);
        }
    }

    private void updateRows(final List<Activity> selectedDayActivities)
    {
        for (int i = 0; i < selectedDayActivities.size(); i++)
        {
            final Activity activity = selectedDayActivities.get(i);
            if (activities.size() <= i)
            {
                LOG.trace("Add activity #{}: {}", i, activity);
                activities.add(ActivityPropertyAdapter.wrap(editListener, activity));
            }
            else
            {
                LOG.trace("Update activity #{}: {}", i, activity);
                activities.get(i).setActivity(activity);
            }
        }
    }

    public TableView<ActivityPropertyAdapter> initTable()
    {
        final TableView<ActivityPropertyAdapter> table = new TableView<>(activities);
        table.setEditable(true);
        table.getColumns().addAll(createColumns());
        table.setId("activities-table");
        table.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> selectedActivity
                        .set(newValue != null ? newValue.getRecord() : null));
        return table;
    }

    private List<TableColumn<ActivityPropertyAdapter, ?>> createColumns()
    {
        final Callback<TableColumn<ActivityPropertyAdapter, Boolean>, TableCell<ActivityPropertyAdapter, Boolean>> cellFactory = tableColumn -> new CheckBoxTableCell<>(
                index -> {
                    final ActivityPropertyAdapter activity = tableColumn.getTableView().getItems().get(index);
                    final SimpleBooleanProperty prop = new SimpleBooleanProperty(activity.remainder.get());
                    Bindings.bindBidirectional(activity.remainder, prop);
                    return prop;
                });

        final TableColumn<ActivityPropertyAdapter, ProjectImpl> projectCol = UiWidget.column("project", "Project",
                param -> new ChoiceBoxTableCell<>(new ProjectStringConverter(projectService),
                        projectService.getAvailableProjects().toArray(new ProjectImpl[0])),
                data -> data.getValue().projectId);
        final TableColumn<ActivityPropertyAdapter, Duration> durationCol = UiWidget.column("duration", "Duration",
                param -> new PersistOnFocusLossTextFieldTableCell<>(new DurationStringConverter(formatterService)),
                data -> data.getValue().duration);
        final TableColumn<ActivityPropertyAdapter, Boolean> remainderCol = UiWidget.column("remainder", "Remainder",
                cellFactory, data -> data.getValue().remainder);
        final TableColumn<ActivityPropertyAdapter, String> commentCol = UiWidget.column("comment", "Comment",
                param -> new PersistOnFocusLossTextFieldTableCell<>(new DefaultStringConverter(),
                        () -> new AutoCompleteTextField(autocompleteService.activityCommentAutocompleter())),
                data -> data.getValue().comment);

        return asList(projectCol, durationCol, remainderCol, commentCol);
    }

    public void refresh()
    {
        activities.forEach(ActivityPropertyAdapter::update);
    }
}
