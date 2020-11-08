package org.itsallcode.whiterabbit.jfxui.table;

import java.util.Objects;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;

public class PropertyField<R, T>
{
    private static final Logger LOG = LogManager.getLogger(PropertyField.class);

    private final ReadOnlyObjectProperty<R> recordProperty;
    private final Property<T> property;
    private final Function<R, T> getter;
    private final String fieldName;

    public PropertyField(ReadOnlyObjectProperty<R> recordProperty, String fieldName, Property<T> property,
            Function<R, T> getter)
    {
        this.recordProperty = recordProperty;
        this.fieldName = fieldName;
        this.property = property;
        this.getter = getter;
    }

    public void update()
    {
        final T newValue = recordProperty.get() != null ? getter.apply(recordProperty.get()) : null;
        if (!Objects.equals(newValue, property.getValue()))
        {
            LOG.trace("Field {} updated, new value: {}", fieldName, newValue);
            property.setValue(newValue);
        }
    }
}