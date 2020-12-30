package org.itsallcode.whiterabbit.jfxui.uistate;

import javafx.scene.control.TableView;

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
