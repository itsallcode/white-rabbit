package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import java.util.ArrayList;

import org.itsallcode.whiterabbit.jfxui.property.DelayedPropertyListener;
import org.itsallcode.whiterabbit.jfxui.uistate.model.ColumnStateModel;
import org.itsallcode.whiterabbit.jfxui.uistate.model.TableStateModel;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

class TableStateManager implements WidgetStateManager<TableView<?>, TableStateModel>
{
    private final DelayedPropertyListener propertyListener;

    TableStateManager(DelayedPropertyListener propertyListener)
    {
        this.propertyListener = propertyListener;
    }

    @Override
    public void restore(TableView<?> widget, TableStateModel model)
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
    public void watch(TableView<?> widget, TableStateModel model)
    {
        model.columns = new ArrayList<>();
        for (final TableColumn<?, ?> column : widget.getColumns())
        {
            final ColumnStateModel columnState = new ColumnStateModel();
            columnState.id = column.getId();
            model.columns.add(columnState);

            propertyListener.register(column.widthProperty(), columnState::setWidth);
        }
    }

    @Override
    public TableStateModel createEmptyModel(String id)
    {
        return new TableStateModel(id);
    }
}
