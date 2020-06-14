package org.itsallcode.whiterabbit.jfxui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.itsallcode.whiterabbit.logic.model.RowRecord;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public abstract class RecordPropertyAdapter<R extends RowRecord>
{
    private final ObjectProperty<R> recordProperty = new ReadOnlyObjectWrapper<>();
    private final EditListener<R> editListener;

    private final List<PropertyField<R, ?>> fields = new ArrayList<>();

    private final BooleanProperty currentlyUpdating = new SimpleBooleanProperty(false);

    protected RecordPropertyAdapter(EditListener<R> editListener)
    {
        this.editListener = editListener;
    }

    protected void runUpdate(Runnable runnable)
    {
        currentlyUpdating.set(true);
        try
        {
            runnable.run();
        }
        finally
        {
            currentlyUpdating.set(false);
        }
    }

    protected void setRecord(R record)
    {
        recordProperty.set(record);
    }

    public R getRecord()
    {
        return recordProperty.get();
    }

    protected void updateFields()
    {
        fields.forEach(PropertyField::update);
    }

    protected <T> ObjectProperty<T> readOnlyPropertyField(String fieldName, Function<R, T> getter)
    {
        return propertyField(fieldName, getter, (r, f) -> {
            // ignore
        });
    }

    protected <T> ObjectProperty<T> propertyField(String fieldName, Function<R, T> getter,
            BiConsumer<R, T> setter)
    {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        final ChangeListener<T> updatingChangeListener = new RecordChangeListener<>(this.recordProperty, fieldName,
                this.editListener, getter, setter);
        property.addListener(new DelegatingChangeListener<>(updatingChangeListener, this.currentlyUpdating));
        final PropertyField<R, T> field = new PropertyField<>(this.recordProperty, fieldName,
                property, getter);
        this.fields.add(field);
        return property;
    }
}
