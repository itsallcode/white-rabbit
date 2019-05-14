package org.itsallcode.whiterabbit.jfxui.ui;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.model.json.DayType;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class DayRecordTable
{
    private final ObservableList<DayRecord> dayRecords = FXCollections.observableArrayList();
    private final TableView<DayRecord> table = new TableView<>(dayRecords);

    public Node initTable(RecordEditListener editListener)
    {
        table.setEditable(true);

        table.getColumns().add(tableColumn("Date", "date"));

        final TableColumn<DayRecord, DayType> dayTypeColumn = new TableColumn<>("Type");
        dayTypeColumn
                .setCellFactory((param) -> new ComboBoxTableCell<>(new StringConverter<DayType>()
                {
                    @Override
                    public String toString(DayType object)
                    {
                        return object != null ? object.name() : null;
                    }

                    @Override
                    public DayType fromString(String string)
                    {
                        return DayType.valueOf(string);
                    }
                }, DayType.values()));
        dayTypeColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue() != null ? data.getValue().getType() : null));
        dayTypeColumn.setOnEditCommit(event -> {
            final DayRecord rowValue = event.getRowValue();
            if (rowValue == null)
            {
                return;
            }
            rowValue.setType(event.getNewValue());
            editListener.recordUpdated(rowValue);
        });
        dayTypeColumn.setEditable(true);
        table.getColumns().add(dayTypeColumn);

        table.getColumns().add(tableColumn("Begin", "begin"));
        table.getColumns().add(tableColumn("End", "end"));
        table.getColumns().add(tableColumn("Break", "mandatoryBreak"));
        table.getColumns().add(tableColumn("Interruption", "interruption"));
        table.getColumns().add(tableColumn("Comment", "comment"));

        return table;
    }

    private <T> TableColumn<DayRecord, T> tableColumn(String label, String property)
    {
        return tableColumn(label, property, null);
    }

    private <T> TableColumn<DayRecord, T> tableColumn(String label, String property,
            EventHandler<CellEditEvent<DayRecord, T>> editCommitHandler)
    {
        final TableColumn<DayRecord, T> column = new TableColumn<>(label);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        if (editCommitHandler != null)
        {
            column.setEditable(true);
            column.setOnEditCommit(editCommitHandler);
        }
        return column;
    }

    public void recordUpdated(DayRecord record)
    {
        Platform.runLater(() -> {
            final int recordIndex = record.getDate().getDayOfMonth() - 1;
            while (dayRecords.size() <= recordIndex)
            {
                dayRecords.add(null);
            }
            dayRecords.set(recordIndex, record);
        });
    }
}
