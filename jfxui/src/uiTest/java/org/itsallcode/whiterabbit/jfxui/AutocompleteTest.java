package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Locale;

import org.itsallcode.whiterabbit.jfxui.testutil.model.DayTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

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

        dayTable.typeComment(3, "txt1");
        dayTable.typeComment(4, "txt2");

        final TableCell<?, ?> commentCell = dayTable.getCommentCell(5);

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
