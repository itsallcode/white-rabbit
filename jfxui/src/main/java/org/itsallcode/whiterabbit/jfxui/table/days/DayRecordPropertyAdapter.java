package org.itsallcode.whiterabbit.jfxui.table.days;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

class DayRecordPropertyAdapter
{
    private static final Logger LOG = LogManager.getLogger(DayRecordPropertyAdapter.class);

    final ObjectProperty<DayRecord> recordProperty = new ReadOnlyObjectWrapper<>();
    private final EditListener<DayRecord> editListener;

    private final List<PropertyField<DayRecord, ?>> fields = new ArrayList<>();

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

    DayRecordPropertyAdapter(EditListener<DayRecord> editListener)
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
        final PropertyField<DayRecord, T> field = new PropertyField<>(this.recordProperty, fieldName,
                property, getter);
        this.fields.add(field);
        return property;
    }
}
