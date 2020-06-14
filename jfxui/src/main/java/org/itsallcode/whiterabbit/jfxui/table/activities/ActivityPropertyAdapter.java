package org.itsallcode.whiterabbit.jfxui.table.activities;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.DelegatingChangeListener;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.PropertyField;
import org.itsallcode.whiterabbit.jfxui.table.RecordChangeListener;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

class ActivityPropertyAdapter
{
    private static final Logger LOG = LogManager.getLogger(ActivityPropertyAdapter.class);

    private final ReadOnlyObjectProperty<Activity> activityProperty;
    private final EditListener<DayRecord> editListener;

    private final List<PropertyField<Activity, ?>> fields = new ArrayList<>();

    private final BooleanProperty currentlyUpdating = new SimpleBooleanProperty(false);

    final ObjectProperty<String> projectId;
    final ObjectProperty<Duration> duration;
    final ObjectProperty<Boolean> remainder;
    final ObjectProperty<String> comment;

    private ActivityPropertyAdapter(EditListener<DayRecord> editListener,
            Activity act)
    {
        this.editListener = editListener;
        this.activityProperty = new ReadOnlyObjectWrapper<>(act);

        projectId = propertyField("projectId", Activity::getProjectId, Activity::setProjectId);
        duration = propertyField("duration", Activity::getDuration, Activity::setDuration);
        comment = propertyField("comment", Activity::getComment, Activity::setComment);
        remainder = propertyField("remainder", Activity::isRemainderActivity, Activity::setRemainderActivity);
        update();
    }

    public void update()
    {
        currentlyUpdating.set(true);
        LOG.debug("Updating fields from {}", activityProperty.get());
        try
        {
            fields.forEach(PropertyField::update);
        }
        finally
        {
            currentlyUpdating.set(false);
        }
    }

    private <T> ObjectProperty<T> propertyField(String fieldName, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter)
    {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();

        final EditListener<Activity> editListenerAdapter = record -> editListener.recordUpdated(record.getDay());
        final ChangeListener<T> updatingChangeListener = new RecordChangeListener<>(this.activityProperty, fieldName,
                editListenerAdapter, getter, setter);
        property.addListener(new DelegatingChangeListener<>(updatingChangeListener, this.currentlyUpdating));
        final PropertyField<Activity, T> field = new PropertyField<>(this.activityProperty, fieldName,
                property, getter);
        this.fields.add(field);
        return property;
    }

    public static List<ActivityPropertyAdapter> wrap(EditListener<DayRecord> editListener, List<Activity> activities)
    {
        return activities.stream()
                .map(a -> new ActivityPropertyAdapter(editListener, a))
                .collect(toList());
    }
}
