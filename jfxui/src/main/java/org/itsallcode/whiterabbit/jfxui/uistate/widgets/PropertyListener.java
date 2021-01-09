package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

public class PropertyListener
{
    private static final Logger LOG = LogManager.getLogger(PropertyListener.class);

    private PropertyListener()
    {
        // not instantiable
    }

    public static <T> void register(ObservableValue<T> property, Consumer<T> delegate)
    {
        delegate.accept(property.getValue());
        property.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                // TODO Auto-generated method stub

            }
        });

        property.addListener((observable, oldValue, newValue) -> {
            LOG.debug("Property {} changed: {} -> {}", property, oldValue, newValue);
            delegate.accept(newValue);
        });
    }
}
