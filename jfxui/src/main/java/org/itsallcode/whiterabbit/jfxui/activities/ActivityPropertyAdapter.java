package org.itsallcode.whiterabbit.jfxui.activities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.Activity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

class ActivityPropertyAdapter
{
    private static final Logger LOG = LogManager.getLogger(ActivityPropertyAdapter.class);

    final ObjectProperty<Activity> recordProperty = new ReadOnlyObjectWrapper<>();
    private final ActivityEditListener editListener;

    private final List<PropertyField<?>> fields = new ArrayList<>();

    private final BooleanProperty currentlyUpdating = new SimpleBooleanProperty(false);

    final ObjectProperty<String> projectId;
    final ObjectProperty<Duration> duration;
    final ObjectProperty<String> comment;

    ActivityPropertyAdapter(ActivityEditListener editListener)
    {
        this.editListener = editListener;

        projectId = propertyField("projectId", Activity::getProjectId, Activity::setProjectId);
        duration = propertyField("duration", Activity::getDuration, Activity::setDuration);
        comment = propertyField("comment", Activity::getComment, Activity::setComment);
    }

    void update(Activity record)
    {
        currentlyUpdating.set(true);
        try
        {
            recordProperty.setValue(record);
            fields.forEach(PropertyField::update);
        }
        finally
        {
            currentlyUpdating.set(false);
        }
    }

    void clear()
    {
        update(null);
    }

    private <T> ObjectProperty<T> propertyField(String fieldName, Function<Activity, T> getter,
            BiConsumer<Activity, T> setter)
    {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        final ChangeListener<T> updatingChangeListener = new RecordChangeListener<>(this.recordProperty, fieldName,
                this.editListener, getter, setter);
        property.addListener(new DelegatingChangeListener<>(updatingChangeListener, this.currentlyUpdating));
        final PropertyField<T> field = new PropertyField<>(this.recordProperty, fieldName, property, getter);
        this.fields.add(field);
        return property;
    }

    private static class PropertyField<T>
    {
        private final ObjectProperty<Activity> recordProperty;
        private final Property<T> property;
        private final Function<Activity, T> getter;
        private final String fieldName;

        private PropertyField(ObjectProperty<Activity> recordProperty, String fieldName, Property<T> property,
                Function<Activity, T> getter)
        {
            this.recordProperty = recordProperty;
            this.fieldName = fieldName;
            this.property = property;
            this.getter = getter;
        }

        void update()
        {
            final T newValue = recordProperty.get() != null ? getter.apply(recordProperty.get()) : null;
            LOG.trace("Field {} updated, new value: {}", fieldName, newValue);
            property.setValue(newValue);
        }
    }

    private static class DelegatingChangeListener<T> implements ChangeListener<T>
    {
        private final ChangeListener<T> delegate;
        private final BooleanProperty currentlyUpdating;

        public DelegatingChangeListener(ChangeListener<T> delegate, BooleanProperty currentlyUpdating)
        {
            this.delegate = delegate;
            this.currentlyUpdating = currentlyUpdating;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
        {
            if (!currentlyUpdating.get())
            {
                delegate.changed(observable, oldValue, newValue);
            }
        }
    }

    private static class RecordChangeListener<T> implements ChangeListener<T>
    {
        private final ObjectProperty<Activity> record;
        private final ActivityEditListener editListener;
        private final BiConsumer<Activity, T> setter;
        private final Function<Activity, T> getter;
        private final String fieldName;

        private RecordChangeListener(ObjectProperty<Activity> record, String fieldName,
                ActivityEditListener editListener, Function<Activity, T> getter, BiConsumer<Activity, T> setter)
        {
            this.record = record;
            this.fieldName = fieldName;
            this.getter = getter;
            this.setter = setter;
            this.editListener = editListener;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
        {
            if (record.get() == null)
            {
                return;
            }
            final T currentRecordValue = getter.apply(record.get());
            if (Objects.equals(currentRecordValue, newValue))
            {
                LOG.debug("Value {} was not changed: ignore update", currentRecordValue);
                return;
            }
            LOG.debug("Value updated for {}, field {}: {} -> {}: trigger edit listener",
                    record.getValue().getProjectId(), fieldName, oldValue, newValue);
            setter.accept(record.get(), newValue);
            this.editListener.recordUpdated(record.get());
        }
    }
}
