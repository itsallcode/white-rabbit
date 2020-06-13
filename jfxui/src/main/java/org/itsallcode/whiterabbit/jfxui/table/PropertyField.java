package org.itsallcode.whiterabbit.jfxui.table;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;

public class PropertyField<R, T>
{
    private static final Logger LOG = LogManager.getLogger(PropertyField.class);

    private final ObjectProperty<R> recordProperty;
    private final Property<T> property;
    private final Function<R, T> getter;
    private final String fieldName;

    public PropertyField(ObjectProperty<R> recordProperty, String fieldName, Property<T> property,
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
        LOG.trace("Field {} updated, new value: {}", fieldName, newValue);
        property.setValue(newValue);
    }
}