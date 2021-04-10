package org.itsallcode.whiterabbit.jfxui.testutil.model;

import org.itsallcode.whiterabbit.jfxui.ui.PluginManagerViewer.PluginTableEntry;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class PluginManagerWindow
{
    private final FxRobot robot;
    private final Window window;
    private final JavaFxTable<PluginTableEntry> pluginTable;

    public PluginManagerWindow(FxRobot robot, Window window)
    {
        this.robot = robot;
        this.window = window;
        this.pluginTable = JavaFxTable.find(robot, "#plugin-table", PluginTableEntry.class);
    }

    public void close()
    {
        robot.type(KeyCode.ESCAPE);
        Assertions.assertThat(window).isNotShowing();
    }

    public JavaFxTable<PluginTableEntry> getTable()
    {
        return pluginTable;
    }
}
