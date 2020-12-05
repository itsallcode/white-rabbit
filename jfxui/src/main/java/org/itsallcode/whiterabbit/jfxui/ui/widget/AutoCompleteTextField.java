package org.itsallcode.whiterabbit.jfxui.ui.widget;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteEntrySupplier;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteProposal;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Based on https://gist.github.com/floralvikings/10290131
 */
@SuppressWarnings("java:S110") // Deep inheritance tree required by API
public class AutoCompleteTextField extends TextField
{
    private static final Logger LOG = LogManager.getLogger(AutoCompleteTextField.class);

    private final AutocompleteEntrySupplier autocompleteEntriesSupplier;
    private final ContextMenu entriesPopup;

    public AutoCompleteTextField(AutocompleteEntrySupplier autocompleteEntriesSupplier)
    {
        this.autocompleteEntriesSupplier = autocompleteEntriesSupplier;
        entriesPopup = new ContextMenu();
        textProperty().addListener((observableValue, oldValue, newValue) -> textUpdated(getText()));
        focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesPopup.hide());
        Platform.runLater(() -> textUpdated(""));
    }

    private void textUpdated(final String currentText)
    {
        final List<AutocompleteProposal> searchResult = autocompleteEntriesSupplier.getEntries(currentText);
        LOG.debug("Text updated: '{}', got {} results", currentText, searchResult.size());
        if (searchResult.isEmpty())
        {
            entriesPopup.hide();
            return;
        }
        populatePopup(searchResult);
        if (!entriesPopup.isShowing())
        {
            showPopup();
        }
    }

    private void showPopup()
    {
        if (getScene() == null)
        {
            LOG.warn("Scene not available for {}, can't show popup", this);
            return;
        }
        LOG.debug("Showing popup with {} entries", entriesPopup.getItems().size());
        entriesPopup.show(this, Side.BOTTOM, 0, 0);
        entriesPopup.getSkin().getNode().requestFocus();
    }

    private void populatePopup(List<AutocompleteProposal> searchResult)
    {
        final List<MenuItem> menuItems = searchResult.stream()
                .map(this::createMenuItem)
                .collect(toList());
        entriesPopup.getItems().setAll(menuItems);
    }

    private MenuItem createMenuItem(final AutocompleteProposal result)
    {
        final List<Text> textParts = highlightMatch(result);
        final TextFlow textFlow = new TextFlow(textParts.toArray(new Node[0]));

        final MenuItem item = new CustomMenuItem(textFlow);
        item.setOnAction(actionEvent -> {
            setText(result.getText());
            entriesPopup.hide();
        });
        return item;
    }

    private List<Text> highlightMatch(final AutocompleteProposal result)
    {
        final int matchPositionStart = result.getMatchPositionStart();
        final List<Text> textParts = new ArrayList<>();
        final String text = result.getText();
        if (matchPositionStart > 0)
        {
            textParts.add(new Text(text.substring(0, matchPositionStart)));
        }
        final int matchLength = result.getMatchLength();
        if (matchPositionStart >= 0)
        {
            final Text matchingText = new Text(text.substring(matchPositionStart, matchPositionStart + matchLength));
            matchingText.setStyle("-fx-font-weight: bold");
            textParts.add(matchingText);
        }
        if (matchPositionStart + matchLength < text.length())
        {
            textParts.add(new Text(text.substring(matchPositionStart + matchLength)));
        }
        return textParts;
    }
}