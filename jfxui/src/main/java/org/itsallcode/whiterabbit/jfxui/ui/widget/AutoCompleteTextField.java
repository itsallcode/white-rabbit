package org.itsallcode.whiterabbit.jfxui.ui.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Based on https://gist.github.com/floralvikings/10290131
 */
public class AutoCompleteTextField extends TextField
{
    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;
    /** The popup used to select an entry. */
    private final ContextMenu entriesPopup;

    /** Construct a new AutoCompleteTextField. */
    public AutoCompleteTextField()
    {
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
        textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2)
            {
                if (getText().length() == 0)
                {
                    entriesPopup.hide();
                }
                else
                {
                    final LinkedList<String> searchResult = new LinkedList<>();
                    searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
                    if (entries.size() > 0)
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
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2)
            {
                entriesPopup.hide();
            }
        });

    }

    /**
     * Get the existing set of autocomplete entries.
     * 
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries()
    {
        return entries;
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
        // If you'd like more entries, modify this line.
        final int maxEntries = 10;
        final int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++)
        {
            final String result = searchResult.get(i);
            final Label entryLabel = new Label(result);
            final CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
                    setText(result);
                    entriesPopup.hide();
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}