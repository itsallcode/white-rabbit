package org.itsallcode.whiterabbit.jfxui.ui;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class UiWidget
{
    private UiWidget()
    {
        // Not innstantiable
    }

    static Button button(String id, String label, EventHandler<ActionEvent> action)
    {
        return button(id, label, null, action);
    }

    static Button button(String id, String label, String tooltip, EventHandler<ActionEvent> action)
    {
        final Button button = new Button(label);
        button.setId(id);
        button.setOnAction(action);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        if (tooltip != null)
        {
            button.setTooltip(new Tooltip(tooltip));
        }
        return button;
    }

    public static <T, A> TableColumn<A, T> readOnlyColumn(String id, String label,
            Callback<TableColumn<A, T>, TableCell<A, T>> cellFactory,
            Callback<CellDataFeatures<A, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, false);
    }

    public static <T, A> TableColumn<A, T> column(String id, String label,
            Callback<TableColumn<A, T>, TableCell<A, T>> cellFactory,
            Callback<CellDataFeatures<A, T>, ObservableValue<T>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, true);
    }

    private static <T, A> TableColumn<A, T> column(String id, String label,
            Callback<TableColumn<A, T>, TableCell<A, T>> cellFactory,
            Callback<CellDataFeatures<A, T>, ObservableValue<T>> cellValueFactory,
            boolean editable)
    {
        final TableColumn<A, T> column = new TableColumn<>(label);
        column.setSortable(false);
        column.setId(id);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(cellValueFactory);
        column.setEditable(editable);
        column.setResizable(true);
        return column;
    }

    public static <R, T> TreeTableColumn<R, T> treeTableColumn(String id, String label,
            Function<R, T> valueExtractor,
            StringConverter<T> stringConverter)
    {
        return treeTableColumn(id, label, cellValueFactory(valueExtractor), cellFactory(stringConverter));
    }

    private static <R, T> TreeTableColumn<R, T> treeTableColumn(String id, String label,
            Callback<TreeTableColumn.CellDataFeatures<R, T>, ObservableValue<T>> cellValueFactory,
            Callback<TreeTableColumn<R, T>, TreeTableCell<R, T>> cellFactory)
    {
        final TreeTableColumn<R, T> column = new TreeTableColumn<>(label);
        column.setId(id);
        column.setCellValueFactory(cellValueFactory);
        column.setCellFactory(cellFactory);
        column.setEditable(false);
        column.setResizable(true);
        column.setSortable(false);
        return column;
    }

    private static <R, T> Callback<TreeTableColumn<R, T>, TreeTableCell<R, T>> cellFactory(
            StringConverter<T> stringConverter)
    {
        return param -> new TextFieldTreeTableCell<>(stringConverter);
    }

    private static <R, T> Callback<TreeTableColumn.CellDataFeatures<R, T>, ObservableValue<T>> cellValueFactory(
            Function<R, T> valueExtractor)
    {
        return param -> {
            if (param.getValue() == null || param.getValue().getValue() == null)
            {
                return new ReadOnlyObjectWrapper<>(null);
            }
            return new ReadOnlyObjectWrapper<>(valueExtractor.apply(param.getValue().getValue()));
        };
    }
}
