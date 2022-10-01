package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.*;

import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class AutocompleteTest extends JavaFxAppUiTestBase
{
    FxRobot robot;

    @Test
    void autocompleteAllowsSelectingProposal()
    {
        time().tickMinute();

        final DayTable dayTable = app().dayTable();

        dayTable.row(3).typeComment("txt1");
        dayTable.row(4).typeComment("txt2");

        final TableCell<?, ?> commentCell = dayTable.row(5).getCommentCell();

        robot.doubleClickOn(commentCell)
                .write("t")
                .type(KeyCode.DOWN)
                .type(KeyCode.ENTER)
                .type(KeyCode.ENTER);

        assertThat(commentCell.isEditing()).isFalse();
        assertThat(commentCell.getText()).isEqualTo("txt1");
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
