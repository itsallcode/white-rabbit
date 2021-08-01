package org.itsallcode.whiterabbit.jfxui.ui;

import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.table.converter.DurationStringConverter;
import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.service.AppService;

public class ChangeOvertimeDialog
{
    private static final Logger LOG = LogManager.getLogger(ChangeOvertimeDialog.class);

    private final Window owner;
    private final MonthIndex monthIndex;
    private final DurationStringConverter durationStringConverter;
    private final AppService appService;

    public ChangeOvertimeDialog(Window owner, final AppService appService, final MonthIndex monthIndex)
    {
        this.owner = owner;
        this.monthIndex = monthIndex;
        this.durationStringConverter = new DurationStringConverter(appService.formatter());
        this.appService = appService;
    }

    public void show()
    {
        final Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.NONE);
        dialog.setTitle("Change overtime");

        final DialogPane dialogPane = dialog.getDialogPane();
        TextField overtime = new TextField();
        overtime.textProperty().addListener((observable, oldValue, newValue) -> {
            if (durationStringConverter.fromString(newValue) != null) {
                overtime.setText(newValue);
            }
        });
        overtime.setText(durationStringConverter.toString(monthIndex.getOvertimePreviousMonth()));
        HBox hb = new HBox();
        hb.getChildren().addAll(new Label("Overtime: "), overtime);

        final ButtonType changeOvertimeButton = new ButtonType("Chang overtime", ButtonData.OK_DONE);
        final ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(changeOvertimeButton, cancelButton);
        dialogPane.setContent(hb);

        dialog.showAndWait() //
                .filter(response -> response == changeOvertimeButton)
                .ifPresent(response -> changeOvertime(overtime.getText()));
    }

    private void changeOvertime(final String overTime)
    {
        LOG.info("Change overtime ");

        monthIndex.setOvertimePreviousMonth(durationStringConverter.fromString(overTime));
        appService.updatePreviousMonthOvertimeFieldManually(monthIndex);

    }
}
