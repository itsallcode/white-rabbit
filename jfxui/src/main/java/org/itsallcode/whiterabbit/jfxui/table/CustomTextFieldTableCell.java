package org.itsallcode.whiterabbit.jfxui.table;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class CustomTextFieldTableCell<S, T> extends TableCell<S, T>
{
    private final StringConverter<T> converter;
    private TextField textField;

    public CustomTextFieldTableCell(StringConverter<T> converter)
    {
        this.converter = converter;
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
            if (textField == null)
            {
                textField = createTextField(this, converter);
            }

            startEdit(this, converter, null, null, textField);
        }
    }

    @Override
    public void cancelEdit()
    {
        super.cancelEdit();
        cancelEdit(this, converter, null);
    }

    @Override
    public void updateItem(T item, boolean empty)
    {
        super.updateItem(item, empty);
        updateItem(this, converter, null, null, textField);
    }

    static <T> TextField createTextField(final CustomTextFieldTableCell<?, T> cell, final StringConverter<T> converter)
    {
        final TextField textField = new TextField(getItemText(cell, converter));

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            if (converter == null)
            {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                                + "StringConverter is null. Be sure to set a StringConverter "
                                + "in your cell factory.");
            }
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

    private static <T> void configureFocusLossBehavior(CustomTextFieldTableCell<?, T> cell,
            final StringConverter<T> converter,
            TextField newTextField)
    {
        final ChangeListener<Boolean> focusListener = (observable, oldSelection, newSelection) -> {
            if (!newSelection)
            {
                cell.commitEdit(converter.fromString(newTextField.getText()));
            }
        };
        newTextField.focusedProperty().addListener(focusListener);

        newTextField.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            {
                newTextField.focusedProperty().removeListener(focusListener);
            }
        });
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter)
    {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem().toString()
                : converter.toString(cell.getItem());
    }

    static <T> void startEdit(final Cell<T> cell,
            final StringConverter<T> converter,
            final HBox hbox,
            final Node graphic,
            final TextField textField)
    {
        if (textField != null)
        {
            textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null)
        {
            hbox.getChildren().setAll(graphic, textField);
            cell.setGraphic(hbox);
        }
        else
        {
            cell.setGraphic(textField);
        }

        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }

    static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic)
    {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
    }

    static <T> void updateItem(final Cell<T> cell,
            final StringConverter<T> converter,
            final TextField textField)
    {
        updateItem(cell, converter, null, null, textField);
    }

    static <T> void updateItem(final Cell<T> cell,
            final StringConverter<T> converter,
            final HBox hbox,
            final Node graphic,
            final TextField textField)
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

                if (graphic != null)
                {
                    hbox.getChildren().setAll(graphic, textField);
                    cell.setGraphic(hbox);
                }
                else
                {
                    cell.setGraphic(textField);
                }
            }
            else
            {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(graphic);
            }
        }
    }
}
