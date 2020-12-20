package org.itsallcode.whiterabbit.jfxui.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

class UiWidget
{
    private UiWidget()
    {
        // Not innstantiable
    }

    static Button button(String id, String label, EventHandler<ActionEvent> action)
    {
        return button(id, label, null, action);
    }

    static Button button(String id, String label, String tooltip, EventHandler<ActionEvent> action)
    {
        final Button button = new Button(label);
        button.setId(id);
        button.setOnAction(action);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        if (tooltip != null)
        {
            button.setTooltip(new Tooltip(tooltip));
        }
        return button;
    }
}
