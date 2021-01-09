package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@JsonbVisibility(FieldAccessStrategy.class)
public class TableState implements WidgetState<TableView<?>>
{
    private final String tableId;
    List<ColumnState> columns;

    public TableState()
    {
        this(null);
    }

    TableState(String tableId)
    {
        this.tableId = tableId;
    }

    @Override
    public void restore(TableView<?> widget)
    {
        if (columns == null)
        {
            return;
        }

        for (int i = 0; i < widget.getColumns().size(); i++)
        {
            columns.get(i).restore(widget.getColumns().get(i));
        }
    }

    @Override
    public void watch(TableView<?> widget)
    {
        columns = new ArrayList<>();
        for (final TableColumn<?, ?> column : widget.getColumns())
        {
            final ColumnState columnState = new ColumnState(column.getId());
            columns.add(columnState);
            columnState.watch(column);
        }
    }

    @Override
    public String toString()
    {
        return "TableState [tableId=" + tableId + ", columns=" + columns + "]";
    }

    @JsonbVisibility(FieldAccessStrategy.class)
    public static class ColumnState implements WidgetState<TableColumn<?, ?>>
    {
        String columnId;
        double width;

        public ColumnState()
        {
            this(null);
        }

        ColumnState(String columnId)
        {
            this.columnId = columnId;
        }

        @Override
        public void restore(TableColumn<?, ?> widget)
        {
            if (width == 0)
            {
                return;
            }
            widget.setPrefWidth(width);
        }

        @Override
        public void watch(TableColumn<?, ?> widget)
        {
            PropertyListener.register(widget.widthProperty(), this::setWidth);
        }

        private void setWidth(Number width)
        {
            this.width = width.doubleValue();
        }

        @Override
        public String toString()
        {
            return "ColumnState [columnId=" + columnId + ", width=" + width + "]";
        }
    }

}
