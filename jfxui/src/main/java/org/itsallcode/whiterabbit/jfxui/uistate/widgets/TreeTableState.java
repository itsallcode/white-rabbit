package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

@JsonbVisibility(FieldAccessStrategy.class)
public class TreeTableState implements WidgetState<TreeTableView<?>>
{
    private final String treeTableId;
    List<TreeColumnState> columns;

    public TreeTableState()
    {
        this(null);
    }

    TreeTableState(String treeTableId)
    {
        this.treeTableId = treeTableId;
    }

    @Override
    public void restore(TreeTableView<?> widget)
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
    public void watch(TreeTableView<?> widget)
    {
        columns = new ArrayList<>();
        for (final TreeTableColumn<?, ?> column : widget.getColumns())
        {
            final TreeColumnState columnState = new TreeColumnState(column.getId());
            columns.add(columnState);
            columnState.watch(column);
        }
    }

    @Override
    public String toString()
    {
        return "TreeTableState [treeTableId=" + treeTableId + ", columns=" + columns + "]";
    }

    @JsonbVisibility(FieldAccessStrategy.class)
    public static class TreeColumnState implements WidgetState<TreeTableColumn<?, ?>>
    {
        String columnId;
        double width;

        public TreeColumnState()
        {
            this(null);
        }

        TreeColumnState(String columnId)
        {
            this.columnId = columnId;
        }

        @Override
        public void restore(TreeTableColumn<?, ?> widget)
        {
            if (width == 0)
            {
                return;
            }
            widget.setPrefWidth(width);
        }

        @Override
        public void watch(TreeTableColumn<?, ?> widget)
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
            return "TreeColumnState [columnId=" + columnId + ", width=" + width + "]";
        }
    }
}
