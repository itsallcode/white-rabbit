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

public class DayRecordPropertyAdapter extends RecordPropertyAdapter<DayRecord>
{
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
        });
    }

    void update(DayRecord record)
    {
        runUpdate(() -> {
            setRecord(record);
            updateFields();
        });
    }

    void clear()
    {
        update(null);
    }
}
