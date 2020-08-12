package org.itsallcode.whiterabbit.jfxui.table;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DelegatingChangeListener<T> implements ChangeListener<T>
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