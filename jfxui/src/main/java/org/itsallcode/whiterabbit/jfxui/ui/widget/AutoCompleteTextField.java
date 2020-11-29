package org.itsallcode.whiterabbit.jfxui.ui.widget;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.autocomplete.AutocompleteEntrySupplier;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Based on https://gist.github.com/floralvikings/10290131
 */
@SuppressWarnings("java:S110") // Deep inheritance tree required by API
public class AutoCompleteTextField extends TextField
{
    private static final Logger LOG = LogManager.getLogger(AutoCompleteTextField.class);

    private static final int MAX_ENTRY_COUNT = 10;

    private final AutocompleteEntrySupplier autocompleteEntriesSupplier;
    private final ContextMenu entriesPopup;

    public AutoCompleteTextField(AutocompleteEntrySupplier autocompleteEntriesSupplier)
    {
        this.autocompleteEntriesSupplier = autocompleteEntriesSupplier;
        entriesPopup = new ContextMenu();
        textProperty().addListener((observableValue, oldValue, newValue) -> textUpdated(getText()));

        focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesPopup.hide());
    }

    private void textUpdated(final String currentText)
    {
        if (currentText.isBlank())
        {
            entriesPopup.hide();
            return;
        }
        final List<String> searchResult = autocompleteEntriesSupplier.getEntries(currentText);
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
            LOG.warn("Scene not available for {}", this);
            return;
        }
        entriesPopup.show(this, Side.BOTTOM, 0, 0);
    }

    private void populatePopup(List<String> searchResult)
    {
        final int count = Math.min(searchResult.size(), MAX_ENTRY_COUNT);
        entriesPopup.getItems().clear();
        for (int i = 0; i < count; i++)
        {
            final String result = searchResult.get(i);
            entriesPopup.getItems().add(createMenuItem(result));
        }
    }

    private CustomMenuItem createMenuItem(final String result)
    {
        final Label entryLabel = new Label(result);
        final CustomMenuItem item = new CustomMenuItem(entryLabel, true);
        item.setOnAction(actionEvent -> {
            setText(result);
            entriesPopup.hide();
        });
        return item;
    }
}