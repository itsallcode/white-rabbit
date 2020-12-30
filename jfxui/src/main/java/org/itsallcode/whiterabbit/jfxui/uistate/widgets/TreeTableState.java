package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import javafx.scene.control.TreeTableView;

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
