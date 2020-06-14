package org.itsallcode.whiterabbit.jfxui.table;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.model.RowRecord;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class RecordChangeListener<R extends RowRecord, T> implements ChangeListener<T>
{
    private static final Logger LOG = LogManager.getLogger(RecordChangeListener.class);

    private final ObjectProperty<R> record;
    private final EditListener<R> editListener;
    private final BiConsumer<R, T> setter;
    private final Function<R, T> getter;
    private final String fieldName;

    public RecordChangeListener(ObjectProperty<R> record, String fieldName,
            EditListener<R> editListener, Function<R, T> getter, BiConsumer<R, T> setter)
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
        LOG.debug("Value updated for {}, field {}: {} -> {}: trigger edit listener", record.getValue(), fieldName,
                oldValue, newValue);
        setter.accept(record.get(), newValue);
        this.editListener.recordUpdated(record.get());
    }
}