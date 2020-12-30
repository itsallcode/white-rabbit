package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.TableView;

@JsonbVisibility(FieldAccessStrategy.class)
class TableState implements WidgetState<TableView<?>>
{

    @Override
    public void store(TableView<?> widget)
    {
        System.out.println("Store " + widget);
    }

    @Override
    public void restore(TableView<?> widget)
    {
        System.out.println("Re-Store " + widget);
    }

}
