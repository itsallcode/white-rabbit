package org.itsallcode.whiterabbit.jfxui.uistate;

import javafx.stage.Stage;

class StageState implements WidgetState<Stage>
{

    @Override
    public void store(Stage stage)
    {
        System.out.println("Store " + stage);
    }

    @Override
    public void restore(Stage stage)
    {
        System.out.println("Re-Store " + stage);
    }

}
