package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.TreeTableView;

@JsonbVisibility(FieldAccessStrategy.class)
class TreeTableState implements WidgetState<TreeTableView<?>>
{
    @Override
    public void store(TreeTableView<?> widget)
    {
        System.out.println("Store " + widget);
    }

    @Override
    public void restore(TreeTableView<?> widget)
    {
        System.out.println("Re-Store " + widget);
    }
}
