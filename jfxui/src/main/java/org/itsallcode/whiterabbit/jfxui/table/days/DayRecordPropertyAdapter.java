package org.itsallcode.whiterabbit.jfxui.table.days;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.itsallcode.whiterabbit.api.model.DayType;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.RecordPropertyAdapter;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;

public class DayRecordPropertyAdapter extends RecordPropertyAdapter<DayRecord>
{
    private static final PseudoClass WEEKEND_PSEUDO_CLASS = PseudoClass.getPseudoClass("weekend");
    private static final PseudoClass NOT_WORKING_PSEUDO_CLASS = PseudoClass.getPseudoClass("not-working");

    final ObjectProperty<LocalDate> date;
    final ObjectProperty<DayType> dayType;
    final ObjectProperty<LocalTime> begin;
    final ObjectProperty<LocalTime> end;
    final ObjectProperty<Duration> mandatoryBreak;
    final ObjectProperty<Duration> interruption;
    final ObjectProperty<Duration> workingTime;
    final ObjectProperty<Duration> overtime;
    final ObjectProperty<Duration> totalOvertime;
    final ObjectProperty<String> comment;
    private TableRow<DayRecordPropertyAdapter> tableRow;

    DayRecordPropertyAdapter(EditListener<DayRecord> editListener)
    {
        super(editListener);
        date = readOnlyPropertyField("date", DayRecord::getDate);
        dayType = propertyField("dayType", DayRecord::getType, DayRecord::setType);
        begin = propertyField("begin", DayRecord::getBegin, DayRecord::setBegin);
        end = propertyField("end", DayRecord::getEnd, DayRecord::setEnd);
        mandatoryBreak = readOnlyPropertyField("mandatoryBreak", DayRecord::getMandatoryBreak);
        interruption = propertyField("interruption", DayRecord::getInterruption, DayRecord::setInterruption);
        workingTime = readOnlyPropertyField("workingTime", DayRecord::getWorkingTime);
        overtime = readOnlyPropertyField("overtime", DayRecord::getOvertime);
        totalOvertime = readOnlyPropertyField("totalOvertime", DayRecord::getOverallOvertime);
        comment = propertyField("comment", DayRecord::getComment, DayRecord::setComment);

        dayType.addListener((ChangeListener<DayType>) (observable, oldValue, newValue) -> {
            if (newValue != null && newValue != DayType.WORK)
            {
                begin.set(null);
                end.set(null);
                interruption.set(Duration.ZERO);
            }
            updateRowPseudoClasses();
        });
    }

    void update(DayRecord record)
    {
        runUpdate(() -> {
            setRecord(record);
            updateFields();
            updateRowPseudoClasses();
        });
    }

    void clear()
    {
        update(null);
    }

    @Override
    public String toString()
    {
        return "DayRecordPropertyAdapter [date=" + date + ", dayType=" + dayType + ", begin=" + begin + ", end=" + end
                + ", mandatoryBreak=" + mandatoryBreak + ", interruption=" + interruption + ", workingTime="
                + workingTime + ", overtime=" + overtime + ", totalOvertime=" + totalOvertime + ", comment=" + comment
                + "]";
    }

    public void setTableRow(TableRow<DayRecordPropertyAdapter> newTableRow)
    {
        this.tableRow = newTableRow;
        updateRowPseudoClasses();
    }

    private void updateRowPseudoClasses()
    {
        if (tableRow == null)
        {
            return;
        }
        final boolean weekend = dayType.get() == DayType.WEEKEND;
        final boolean notWorking = dayType.get() != null && !dayType.get().isWorkDay();
        tableRow.pseudoClassStateChanged(WEEKEND_PSEUDO_CLASS, weekend);
        tableRow.pseudoClassStateChanged(NOT_WORKING_PSEUDO_CLASS, notWorking);
        tableRow.requestLayout();
        tableRow.layout();
    }
}
