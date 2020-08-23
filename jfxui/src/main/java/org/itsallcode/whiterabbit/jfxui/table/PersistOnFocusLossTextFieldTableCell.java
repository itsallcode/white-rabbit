package org.itsallcode.whiterabbit.jfxui.table;

import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

/**
 * A class similar to {@link javafx.scene.control.cell.TextFieldTableCell} that
 * supports persisting edit changes on focus loss.
 * <p>
 * Implementation based on proposed workaround for <a href=
 * "https://bugs.openjdk.java.net/browse/JDK-8089311?focusedCommentId=13810219&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-13810219">JDK-8089311</a>.
 * <p>
 * Note: inheriting from {@link javafx.scene.control.cell.TextFieldTableCell} is
 * not possible because it would require writing to private field
 * <code>textField</code>.
 */
@SuppressWarnings("java:S110") // Deep inheritance tree required by API
public class PersistOnFocusLossTextFieldTableCell<S, T> extends TableCell<S, T>
{
    private final StringConverter<T> converter;
    private TextField textField;

    public PersistOnFocusLossTextFieldTableCell(final StringConverter<T> converter)
    {
        this.converter = Objects.requireNonNull(converter);
    }

    @Override
    public void startEdit()
    {
        if (!isEditable()
                || !getTableView().isEditable()
                || !getTableColumn().isEditable())
        {
            return;
        }
        super.startEdit();

        if (isEditing())
        {
            if (this.textField == null)
            {
                this.textField = createTextField(this, this.converter);
            }

            startEdit(this, this.converter, this.textField);
        }
    }

    private static <T> TextField createTextField(final PersistOnFocusLossTextFieldTableCell<?, T> cell,
            final StringConverter<T> converter)
    {
        final TextField textField = new TextField(getItemText(cell, converter));

        textField.setOnAction(event -> {
            cell.commitEdit(converter.fromString(textField.getText()));
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE)
            {
                cell.cancelEdit();
                t.consume();
            }
        });

        configureFocusLossBehavior(cell, converter, textField);
        return textField;
    }

    private static <T> void configureFocusLossBehavior(final PersistOnFocusLossTextFieldTableCell<?, T> cell,
            final StringConverter<T> converter, final TextField newTextField)
    {
        final ChangeListener<Boolean> focusListener = (observable, oldSelection, newSelection) -> {
            if (!newSelection.booleanValue())
            {
                cell.commitEdit(converter.fromString(newTextField.getText()));
            }
        };
        newTextField.focusedProperty().addListener(focusListener);

        newTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            {
                newTextField.focusedProperty().removeListener(focusListener);
            }
        });
    }

    @Override
    public void cancelEdit()
    {
        super.cancelEdit();
        cancelEdit(this, this.converter, null);
    }

    @Override
    public void updateItem(final T item, final boolean empty)
    {
        super.updateItem(item, empty);
        updateItem(this, this.converter, this.textField);
    }

    private static <T> String getItemText(final Cell<T> cell, final StringConverter<T> converter)
    {
        return converter.toString(cell.getItem());
    }

    private static <T> void startEdit(final Cell<T> cell, final StringConverter<T> converter, final TextField textField)
    {
        textField.setText(getItemText(cell, converter));
        cell.setText(null);
        cell.setGraphic(textField);
        textField.selectAll();
        textField.requestFocus();
    }

    private static <T> void cancelEdit(final Cell<T> cell, final StringConverter<T> converter, final Node graphic)
    {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
    }

    private static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final TextField textField)
    {
        if (cell.isEmpty())
        {
            cell.setText(null);
            cell.setGraphic(null);
        }
        else
        {
            if (cell.isEditing())
            {
                if (textField != null)
                {
                    textField.setText(getItemText(cell, converter));
                }
                cell.setText(null);

                cell.setGraphic(textField);
            }
            else
            {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(null);
            }
        }
    }
}
