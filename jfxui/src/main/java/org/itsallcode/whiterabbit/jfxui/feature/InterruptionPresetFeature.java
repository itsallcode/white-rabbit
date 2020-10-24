package org.itsallcode.whiterabbit.jfxui.feature;

import static java.util.stream.Collectors.toList;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.Duration;
import java.util.List;
import java.util.function.UnaryOperator;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.converter.IntegerStringConverter;

public class InterruptionPresetFeature
{
    private static final List<Integer> PRESETS_IN_MINUTES = List.of(5, 10, 15, 30, 45, 60, 90, 120);
    private final AppService appService;

    public InterruptionPresetFeature(AppService appService)
    {
        this.appService = appService;
    }

    public Node createButton()
    {
        final SplitMenuButton button = new SplitMenuButton();
        button.setId("add-interruption-button");
        button.setText("Add interruption");
        button.getItems().addAll(createPresetMenuItems());
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
                .map(Duration::ofMinutes)
                .map(this::createMenuitem)
                .collect(toList());
    }

    private MenuItem createMenuitem(Duration interruption)
    {
        final MenuItem menuItem = new MenuItem("Add interruption of " + appService.formatter().format(interruption));
        menuItem.setId("add-interruption-preset-" + interruption.toString());
        menuItem.setOnAction(event -> addInterruptionForToday(interruption));
        return menuItem;
    }

    private void addInterruptionForToday(Duration interruption)
    {
        appService.addInterruption(appService.getClock().getCurrentDate(), interruption);
    }

    private static class DurationInputDialog extends Dialog<Duration>
    {
        private final GridPane grid;
        private final Label label;
        private final Spinner<Integer> spinner;

        private DurationInputDialog()
        {
            final DialogPane dialogPane = getDialogPane();
            final int maxValue = (int) Duration.ofHours(8).toMinutes();

            spinner = new Spinner<>(0, maxValue, 0, 5);
            spinner.setId("interruption-duration-spinner");
            spinner.setMaxWidth(Double.MAX_VALUE);
            spinner.setEditable(true);

            final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
            final UnaryOperator<TextFormatter.Change> filter = change -> {
                if (change.isContentChange())
                {
                    final String newText = change.getControlNewText();
                    if (newText.isEmpty())
                    {
                        return change;
                    }
                    final ParsePosition parsePosition = new ParsePosition(0);
                    numberFormat.parse(newText, parsePosition);
                    if (parsePosition.getIndex() == 0 ||
                            parsePosition.getIndex() < newText.length())
                    {
                        return null;
                    }
                }
                return change;
            };
            final TextFormatter<Integer> intFormatter = new TextFormatter<>(
                    new IntegerStringConverter(), 0, filter);
            spinner.getEditor().setTextFormatter(intFormatter);

            GridPane.setHgrow(spinner, Priority.ALWAYS);
            GridPane.setFillWidth(spinner, true);

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

        private static Label createContentLabel(String text)
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

            Platform.runLater(() -> spinner.requestFocus());
        }
    }
}
