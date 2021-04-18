package org.itsallcode.whiterabbit.jfxui.ui;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class UiWidget
{
    private UiWidget()
    {
        // Not innstantiable
    }

    public static Button button(String id, String label, EventHandler<ActionEvent> action)
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

    public static <S, T> TableColumn<T, S> readOnlyColumn(String id, String label, StringConverter<S> stringConverter,
            Function<T, S> valueExtractor)
    {
        return readOnlyColumn(id, label, tableCellFactory(stringConverter), tableCellValueFactory(valueExtractor));
    }

    public static <S, T> TableColumn<T, S> readOnlyColumn(String id, String label,
            Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory,
            Callback<CellDataFeatures<T, S>, ObservableValue<S>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, false);
    }

    public static <S, T> TableColumn<T, S> column(String id, String label,
            Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory,
            Callback<CellDataFeatures<T, S>, ObservableValue<S>> cellValueFactory)
    {
        return column(id, label, cellFactory, cellValueFactory, true);
    }

    private static <S, T> TableColumn<T, S> column(String id, String label,
            Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory,
            Callback<CellDataFeatures<T, S>, ObservableValue<S>> cellValueFactory,
            boolean editable)
    {
        final TableColumn<T, S> column = new TableColumn<>();
        column.setSortable(false);
        column.setId(id);
        column.setCellFactory(cellFactory);
        column.setCellValueFactory(cellValueFactory);
        column.setEditable(editable);
        column.setResizable(true);
        column.setGraphic(createLabelWithTooltip(label));
        return column;
    }

    public static <S, T> TreeTableColumn<S, T> treeTableColumn(String id, String label,
            Function<S, T> valueExtractor,
            StringConverter<T> stringConverter)
    {
        return treeTableColumn(id, label, treeTableCellValueFactory(valueExtractor),
                treeTableCellFactory(stringConverter));
    }

    private static <S, T> TreeTableColumn<S, T> treeTableColumn(String id, String label,
            Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory,
            Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> cellFactory)
    {
        final TreeTableColumn<S, T> column = new TreeTableColumn<>();
        column.setId(id);
        column.setCellValueFactory(cellValueFactory);
        column.setCellFactory(cellFactory);
        column.setEditable(false);
        column.setResizable(true);
        column.setSortable(false);
        column.setGraphic(createLabelWithTooltip(label));
        return column;
    }

    private static Label createLabelWithTooltip(String label)
    {
        final Label columnHeaderLabel = new Label(label);
        final Tooltip tooltip = new Tooltip(label);
        tooltip.getStyleClass().add("mytooltip");
        columnHeaderLabel.setTooltip(tooltip);
        columnHeaderLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return columnHeaderLabel;
    }

    private static <S, T> Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> treeTableCellFactory(
            StringConverter<T> stringConverter)
    {
        return param -> new TextFieldTreeTableCell<>(stringConverter);
    }

    private static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> tableCellFactory(
            StringConverter<T> stringConverter)
    {
        return param -> new TextFieldTableCell<>(stringConverter);
    }

    private static <S, T> Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> treeTableCellValueFactory(
            Function<S, T> valueExtractor)
    {
        return param -> {
            if (param.getValue() == null || param.getValue().getValue() == null)
            {
                return new ReadOnlyObjectWrapper<>(null);
            }
            return new ReadOnlyObjectWrapper<>(valueExtractor.apply(param.getValue().getValue()));
        };
    }

    private static <S, T> Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> tableCellValueFactory(
            Function<S, T> valueExtractor)
    {
        return param -> {
            if (param.getValue() == null)
            {
                return new ReadOnlyObjectWrapper<>(null);
            }
            return new ReadOnlyObjectWrapper<>(valueExtractor.apply(param.getValue()));
        };
    }
}
