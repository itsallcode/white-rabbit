package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;

import org.itsallcode.whiterabbit.jfxui.uistate.model.ColumnStateModel;
import org.itsallcode.whiterabbit.jfxui.uistate.model.TableStateModel;

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

class TreeTableStateManager implements WidgetStateManager<TreeTableView<?>, TableStateModel>
{
    @Override
    public void restore(TreeTableView<?> widget, TableStateModel model)
    {
        if (model.columns == null)
        {
            return;
        }

        for (int i = 0; i < widget.getColumns().size(); i++)
        {
            final double width = model.columns.get(i).width;
            if (width == 0)
            {
                return;
            }

            widget.getColumns().get(i).setPrefWidth(width);
        }
    }

    @Override
    public void watch(TreeTableView<?> widget, TableStateModel model)
    {
        model.columns = new ArrayList<>();
        for (final TreeTableColumn<?, ?> column : widget.getColumns())
        {
            final ColumnStateModel columnState = new ColumnStateModel();
            columnState.id = column.getId();
            model.columns.add(columnState);

            PropertyListener.register(column.widthProperty(), columnState::setWidth);
        }
    }

    @Override
    public TableStateModel createEmptyModel(String id)
    {
        return new TableStateModel(id);
    }
}
