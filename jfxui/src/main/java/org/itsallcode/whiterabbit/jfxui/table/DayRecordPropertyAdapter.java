package org.itsallcode.whiterabbit.jfxui.table;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

class DayRecordPropertyAdapter
{
    private static final Logger LOG = LogManager.getLogger(DayRecordPropertyAdapter.class);

    private final ObjectProperty<DayRecord> recordProperty = new ReadOnlyObjectWrapper<>();
    private final RecordEditListener editListener;

    private final List<PropertyField<?>> fields = new ArrayList<>();

    private final BooleanProperty currentlyUpdating = new SimpleBooleanProperty(false);

    final ObjectProperty<LocalDate> date;
    final ObjectProperty<DayType> dayType;
    final ObjectProperty<LocalTime> begin;
    final ObjectProperty<LocalTime> end;
    final ObjectProperty<Duration> mandatoryBreak;
    final ObjectProperty<Duration> interruption;
    final ObjectProperty<Duration> workingTime;
    final ObjectProperty<Duration> overtime;
    final ObjectProperty<Duration> totalOvertime;
    final ObjectProperty<String> comment;

    DayRecordPropertyAdapter(RecordEditListener editListener)
    {
        this.editListener = editListener;

        date = readOnlyPropertyField("date", DayRecord::getDate);
        dayType = propertyField("dayType", DayRecord::getType, DayRecord::setType);
        begin = propertyField("begin", DayRecord::getBegin, DayRecord::setBegin);
        end = propertyField("end", DayRecord::getEnd, DayRecord::setEnd);
        mandatoryBreak = readOnlyPropertyField("mandatoryBreak", DayRecord::getMandatoryBreak);
        interruption = propertyField("interruption", DayRecord::getInterruption, DayRecord::setInterruption);
        workingTime = readOnlyPropertyField("workingTime", DayRecord::getWorkingTime);
        overtime = readOnlyPropertyField("overtime", DayRecord::getOvertime);
        totalOvertime = readOnlyPropertyField("totalOvertime", DayRecord::getOverallOvertime);
        comment = propertyField("comment", DayRecord::getComment, DayRecord::setComment);
    }

    void update(DayRecord record)
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

    private <T> ObjectProperty<T> readOnlyPropertyField(String fieldName, Function<DayRecord, T> getter)
    {
        return propertyField(fieldName, getter, (r, f) -> {
            // ignore
        });
    }

    private <T> ObjectProperty<T> propertyField(String fieldName, Function<DayRecord, T> getter,
            BiConsumer<DayRecord, T> setter)
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
        private final ObjectProperty<DayRecord> recordProperty;
        private final Property<T> property;
        private final Function<DayRecord, T> getter;
        private final String fieldName;

        private PropertyField(ObjectProperty<DayRecord> recordProperty, String fieldName, Property<T> property,
                Function<DayRecord, T> getter)
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
        private final ObjectProperty<DayRecord> record;
        private final RecordEditListener editListener;
        private final BiConsumer<DayRecord, T> setter;
        private final Function<DayRecord, T> getter;
        private final String fieldName;

        private RecordChangeListener(ObjectProperty<DayRecord> record, String fieldName,
                RecordEditListener editListener, Function<DayRecord, T> getter, BiConsumer<DayRecord, T> setter)
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
            LOG.debug("Value updated for {}, field {}: {} -> {}: trigger edit listener", record.getValue().getDate(),
                    fieldName, oldValue, newValue);
            setter.accept(record.get(), newValue);
            this.editListener.recordUpdated(record.get());
        }
    }
}
