package org.itsallcode.whiterabbit.jfxui.table.activities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.DelegatingChangeListener;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.PropertyField;
import org.itsallcode.whiterabbit.jfxui.table.RecordChangeListener;
import org.itsallcode.whiterabbit.logic.model.Activity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

class ActivityPropertyAdapter
{
    private static final Logger LOG = LogManager.getLogger(ActivityPropertyAdapter.class);

    final ObjectProperty<Activity> property = new ReadOnlyObjectWrapper<>();
    private final EditListener<Activity> editListener;

    private final List<PropertyField<Activity, ?>> fields = new ArrayList<>();

    private final BooleanProperty currentlyUpdating = new SimpleBooleanProperty(false);

    final ObjectProperty<String> projectId;
    final ObjectProperty<Duration> duration;
    final ObjectProperty<Boolean> remainder;
    final ObjectProperty<String> comment;

    ActivityPropertyAdapter(EditListener<Activity> editListener)
    {
        this.editListener = editListener;

        projectId = propertyField("projectId", Activity::getProjectId, Activity::setProjectId);
        duration = propertyField("duration", Activity::getDuration, Activity::setDuration);
        comment = propertyField("comment", Activity::getComment, Activity::setComment);
        remainder = propertyField("remainder", Activity::isRemainderActivity, (activity, remainderValue) -> {
            activity.setRemainderActivity(remainderValue);
            if (Objects.equals(duration.get(), activity.getDuration()))
            {
                duration.set(activity.getDuration());
            }
        });
    }

    private <T> ObjectProperty<T> propertyField(String fieldName, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter)
    {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        final ChangeListener<T> updatingChangeListener = new RecordChangeListener<>(this.property, fieldName,
                this.editListener, getter, setter);
        property.addListener(new DelegatingChangeListener<>(updatingChangeListener, this.currentlyUpdating));
        final PropertyField<Activity, T> field = new PropertyField<>(this.property, fieldName,
                property, getter);
        this.fields.add(field);
        return property;
    }
}
