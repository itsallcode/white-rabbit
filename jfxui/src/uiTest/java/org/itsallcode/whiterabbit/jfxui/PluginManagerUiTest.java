package org.itsallcode.whiterabbit.jfxui;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.itsallcode.whiterabbit.jfxui.testutil.UiTestDummyPlugin;
import org.itsallcode.whiterabbit.jfxui.testutil.model.JavaFxTable;
import org.itsallcode.whiterabbit.jfxui.testutil.model.PluginManagerWindow;
import org.itsallcode.whiterabbit.jfxui.ui.PluginManagerViewer.PluginTableEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class PluginManagerUiTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void openPluginManager()
    {
        final PluginManagerWindow dialog = app().openPluginManager();
        final JavaFxTable<PluginTableEntry> pluginTable = dialog.getTable();
        pluginTable.assertRowCount(1)
                .assertContent(
                        TableRowExpectedContent.forValues(UiTestDummyPlugin.ID, "ProjectReportExporter", "included"));
        dialog.close();
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        doStart(stage);
        setRobot(robot);
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
