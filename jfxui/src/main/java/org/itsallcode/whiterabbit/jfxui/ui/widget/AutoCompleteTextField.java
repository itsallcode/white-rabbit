package org.itsallcode.whiterabbit.jfxui.ui.widget;

import java.util.LinkedList;
import java.util.List;

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
    private static final int MAX_ENTRY_COUNT = 10;

    /** The popup used to select an entry. */
    private final ContextMenu entriesPopup;

    /** Construct a new AutoCompleteTextField. */
    public AutoCompleteTextField(AutocompleteEntrySupplier autocompleteEntriesSupplier)
    {
        entriesPopup = new ContextMenu();
        textProperty().addListener((observableValue, oldValue, newValue) -> {
            final String currentText = getText();
            if (currentText.length() == 0)
            {
                entriesPopup.hide();
            }
            else
            {
                final List<String> searchResult = autocompleteEntriesSupplier.getEntries(currentText);
                if (!searchResult.isEmpty())
                {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing())
                    {
                        entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                }
                else
                {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesPopup.hide());
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     * 
     * @param searchResult
     *            The set of matching strings.
     */
    private void populatePopup(List<String> searchResult)
    {
        final List<CustomMenuItem> menuItems = new LinkedList<>();

        final int count = Math.min(searchResult.size(), MAX_ENTRY_COUNT);
        for (int i = 0; i < count; i++)
        {
            final String result = searchResult.get(i);
            final Label entryLabel = new Label(result);
            final CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}