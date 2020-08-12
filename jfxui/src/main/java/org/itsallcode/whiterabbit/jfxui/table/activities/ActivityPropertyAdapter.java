package org.itsallcode.whiterabbit.jfxui.table.activities;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.itsallcode.whiterabbit.jfxui.table.EditListener;
import org.itsallcode.whiterabbit.jfxui.table.RecordPropertyAdapter;
import org.itsallcode.whiterabbit.logic.model.Activity;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.project.Project;

import javafx.beans.property.ObjectProperty;

class ActivityPropertyAdapter extends RecordPropertyAdapter<Activity>
{
    private static final Logger LOG = LogManager.getLogger(ActivityPropertyAdapter.class);

    final ObjectProperty<Project> projectId;
    final ObjectProperty<Duration> duration;
    final ObjectProperty<Boolean> remainder;
    final ObjectProperty<String> comment;

    private ActivityPropertyAdapter(EditListener<DayRecord> editListener,
            Activity act)
    {
        super(record -> editListener.recordUpdated(record.getDay()));
        setRecord(act);
        projectId = propertyField("projectId", Activity::getProject, Activity::setProject);
        duration = propertyField("duration", Activity::getDuration, Activity::setDuration);
        comment = propertyField("comment", Activity::getComment, Activity::setComment);
        remainder = propertyField("remainder", Activity::isRemainderActivity, Activity::setRemainderActivity);
        update();
    }

    public void update()
    {
        runUpdate(() -> {
            LOG.trace("Updating fields from {}", getRecord());
            updateFields();
        });
    }

    static List<@NonNull ActivityPropertyAdapter> wrap(EditListener<DayRecord> editListener, List<Activity> activities)
    {
        return activities.stream()
                .map(a -> new ActivityPropertyAdapter(editListener, a))
                .collect(toList());
    }
}
