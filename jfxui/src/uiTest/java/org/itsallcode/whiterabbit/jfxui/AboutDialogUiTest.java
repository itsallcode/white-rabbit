package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.model.AboutDialogWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.*;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class AboutDialogUiTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void openAboutDialog()
    {
        final AboutDialogWindow aboutDialog = app().openAboutDialog();
        assertAll(() -> assertThat(aboutDialog.getHeaderText()).startsWith("White Rabbit version "),
                () -> assertThat(aboutDialog.getContentText()).startsWith("Java Vendor: "));
        aboutDialog.close();
    }

    @Override
    @Start
    void start(final Stage stage)
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
