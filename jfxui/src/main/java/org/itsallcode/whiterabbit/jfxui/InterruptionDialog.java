package org.itsallcode.whiterabbit.jfxui;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.service.Interruption;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class InterruptionDialog
{
    private static final Logger LOG = LogManager.getLogger(InterruptionDialog.class);

    private final ObjectProperty<Interruption> interruption;
    private final Window owner;
    private final Property<Instant> currentTimeProperty;

    private final Clock clock;
    private final Locale locale;

    public InterruptionDialog(Window owner, Property<Instant> currentTimeProperty,
            ObjectProperty<Interruption> interruption, Clock clock, Locale locale)
    {
        this.owner = owner;
        this.currentTimeProperty = currentTimeProperty;
        this.interruption = interruption;
        this.clock = clock;
        this.locale = locale;
    }

    public void show()
    {
        final Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.NONE);
        dialog.setTitle("Add interruption");

        final DialogPane dialogPane = dialog.getDialogPane();
        final Instant interruptionStartInstant = interruption.get().getStart().truncatedTo(ChronoUnit.SECONDS);
        final LocalTime interruptionStart = LocalTime.ofInstant(interruptionStartInstant, clock.getZone());
        dialogPane.setHeaderText("Interruption started at " + interruptionStart + ". End interruption now?");
        dialogPane.contentTextProperty().bind(Bindings.createStringBinding(this::formatText, currentTimeProperty));
        final ButtonType addInterruptionButton = new ButtonType("Add interruption", ButtonData.OK_DONE);
        final ButtonType cancelInterruptionButton = new ButtonType("Cancel interruption", ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(addInterruptionButton, cancelInterruptionButton);

        dialog.showAndWait() //
                .filter(response -> response == addInterruptionButton)
                .ifPresentOrElse(response -> addInterruption(), this::cancelInterruption);
        interruption.set(null);
    }

    private void addInterruption()
    {
        LOG.info("Add interruption {}", interruption);
        interruption.get().end();
    }

    private void cancelInterruption()
    {
        LOG.info("Cancel interruption {}", interruption);
        interruption.get().cancel();
    }

    private String formatText()
    {
        if (interruption.get() == null)
        {
            return "Interruption cancelled";
        }
        final Instant now = currentTimeProperty.getValue();
        final Duration duration = interruption.get().currentDuration(now);
        final LocalTime currentTime = LocalTime.ofInstant(now, ZoneId.systemDefault());
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale);
        return "Current time: " + currentTime.format(formatter) + ". Add interruption of " + duration + "?";
    }
}
