package org.itsallcode.whiterabbit.jfxui.ui;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DayRecordTable
{
    private final ObservableList<DayRecord> dayRecords = FXCollections.observableArrayList();
    private final TableView<DayRecord> table = new TableView<>(dayRecords);

    public Node initTable()
    {
        table.setEditable(true);

        table.getColumns().add(tableColumn("Date", "date"));
        table.getColumns().add(tableColumn("Type", "type"));
        table.getColumns().add(tableColumn("Begin", "begin"));
        table.getColumns().add(tableColumn("End", "end"));
        table.getColumns().add(tableColumn("Break", "mandatoryBreak"));
        table.getColumns().add(tableColumn("Interruption", "interruption"));
        table.getColumns().add(tableColumn("Comment", "comment"));

        return table;
    }

    private <T> TableColumn<DayRecord, T> tableColumn(String label, String property)
    {
        final TableColumn<DayRecord, T> dateColumn = new TableColumn<>(label);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        return dateColumn;
    }

    public void recordUpdated(DayRecord record)
    {
        final int recordIndex = record.getDate().getDayOfMonth() - 1;
        while (dayRecords.size() <= recordIndex)
        {
            dayRecords.add(null);
        }
        dayRecords.set(recordIndex, record);
    }
}
