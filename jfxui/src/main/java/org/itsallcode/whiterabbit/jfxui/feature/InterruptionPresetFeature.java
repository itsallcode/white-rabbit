package org.itsallcode.whiterabbit.jfxui.feature;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.itsallcode.whiterabbit.logic.service.AppService;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.converter.IntegerStringConverter;

public class InterruptionPresetFeature
{
    private static final List<Integer> PRESETS_IN_MINUTES = List.of(5, 10, 15, 30, 45, 60, 90, 120);
    private final AppService appService;

    public InterruptionPresetFeature(final AppService appService)
    {
        this.appService = appService;
    }

    public Node createButton()
    {
        final SplitMenuButton button = new SplitMenuButton();
        button.setId("add-interruption-button");
        button.setText("Add interruption");
        button.getItems().addAll(createPresetMenuItems());
        button.getItems().add(createMenuitem(appService.getContractTerms().getMandatoryBreak().negated()));
        button.setOnAction(event -> showDialog());
        return button;
    }

    private void showDialog()
    {
        new DurationInputDialog().showAndWait().ifPresent(this::addInterruptionForToday);
    }

    private List<MenuItem> createPresetMenuItems()
    {
        return PRESETS_IN_MINUTES.stream()
                .mapToLong(Integer::longValue)
                .mapToObj(Duration::ofMinutes)
                .map(this::createMenuitem)
                .toList();
    }

    private MenuItem createMenuitem(final Duration interruption)
    {
        final String verb = interruption.isNegative() ? "Subtract" : "Add";
        final MenuItem menuItem = new MenuItem(
                verb + " interruption of " + appService.formatter().format(interruption.abs()));
        menuItem.setId(verb.toLowerCase(Locale.ENGLISH) + "-interruption-preset-" + interruption.toString());
        menuItem.setOnAction(event -> addInterruptionForToday(interruption));
        return menuItem;
    }

    private void addInterruptionForToday(final Duration interruption)
    {
        appService.addInterruption(appService.getClock().getCurrentDate(), interruption);
    }

    private static final class DurationInputDialog extends Dialog<Duration>
    {
        private final GridPane grid;
        private final Label label;
        private final Spinner<Integer> spinner;

        private DurationInputDialog()
        {
            final int maxValue = (int) Duration.ofHours(8).toMinutes();

            spinner = new Spinner<>(0, maxValue, 0, 5);
            spinner.setId("interruption-duration-spinner");
            spinner.setMaxWidth(Double.MAX_VALUE);
            spinner.setEditable(true);

            final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
            final TextFormatter<Integer> intFormatter = new TextFormatter<>(
                    new IntegerStringConverter(), 0, change -> removeInvalidNumber(numberFormat, change));
            spinner.getEditor().setTextFormatter(intFormatter);

            GridPane.setHgrow(spinner, Priority.ALWAYS);
            GridPane.setFillWidth(spinner, true);

            final DialogPane dialogPane = getDialogPane();
            label = createContentLabel(dialogPane.getContentText());
            label.setPrefWidth(Region.USE_COMPUTED_SIZE);
            label.textProperty().bind(dialogPane.contentTextProperty());

            grid = new GridPane();
            grid.setHgap(10);
            grid.setMaxWidth(Double.MAX_VALUE);
            grid.setAlignment(Pos.CENTER_LEFT);

            dialogPane.contentTextProperty().addListener(o -> updateGrid());

            setTitle("Add interruption for today");
            dialogPane.setHeaderText("Please enter duration in minutes:");
            dialogPane.getStyleClass().add("text-input-dialog");
            final ButtonType buttonTypeAddInterruption = new ButtonType("Add interruption", ButtonData.OK_DONE);
            final ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            dialogPane.getButtonTypes().addAll(buttonTypeAddInterruption, buttonTypeCancel);

            updateGrid();

            setResultConverter(dialogButton -> {
                final ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
                final Integer value = spinner.getValue();
                if (data != ButtonData.OK_DONE || value == null)
                {
                    return null;
                }
                return Duration.ofMinutes(value);
            });
        }

        private static Change removeInvalidNumber(final NumberFormat numberFormat, final Change change)
        {
            if (change.isContentChange())
            {
                final String newText = change.getControlNewText();
                if (newText.isEmpty())
                {
                    return change;
                }
                if (parsingFails(numberFormat, newText))
                {
                    return null;
                }
            }
            return change;
        }

        private static boolean parsingFails(final NumberFormat numberFormat, final String text)
        {
            final ParsePosition parsePosition = new ParsePosition(0);
            numberFormat.parse(text, parsePosition);
            return parsePosition.getIndex() == 0 ||
                    parsePosition.getIndex() < text.length();
        }

        private static Label createContentLabel(final String text)
        {
            final Label label = new Label(text);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.getStyleClass().add("content");
            label.setWrapText(true);
            label.setPrefWidth(360);
            return label;
        }

        private void updateGrid()
        {
            grid.getChildren().clear();

            grid.add(label, 0, 0);
            grid.add(spinner, 1, 0);
            getDialogPane().setContent(grid);

            Platform.runLater(spinner::requestFocus);
        }
    }
}
