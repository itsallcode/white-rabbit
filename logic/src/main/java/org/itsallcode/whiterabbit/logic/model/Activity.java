package org.itsallcode.whiterabbit.logic.model;

import java.time.Duration;
import java.util.Objects;

import org.itsallcode.whiterabbit.api.model.Project;
import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.service.project.ProjectImpl;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;

public class Activity implements RowRecord
{
    final JsonActivity jsonActivity;
    private final DayActivities day;
    private final int index;
    private final ProjectService projectService;

    public Activity(int index, JsonActivity jsonActivity, DayActivities day, ProjectService projectService)
    {
        this.index = index;
        this.jsonActivity = jsonActivity;
        this.day = day;
        this.projectService = Objects.requireNonNull(projectService);
    }

    public DayRecord getDay()
    {
        return day.dayRecord;
    }

    public void updateValuesFrom(Activity activity)
    {
        this.jsonActivity.setComment(activity.jsonActivity.getComment());
        this.jsonActivity.setProjectId(activity.jsonActivity.getProjectId());
        this.jsonActivity.setDuration(activity.jsonActivity.getDuration());
    }

    public ProjectImpl getProject()
    {
        final String projectId = jsonActivity.getProjectId();
        return projectService.getProjectById(projectId).orElse(null);
    }

    public void setProject(Project project)
    {
        this.jsonActivity.setProjectId(project.getProjectId());
    }

    public Duration getDuration()
    {
        return day.getDuration(this);
    }

    public void setDuration(Duration duration)
    {
        jsonActivity.setDuration(duration);
    }

    public String getComment()
    {
        return jsonActivity.getComment();
    }

    public void setComment(String comment)
    {
        jsonActivity.setComment(comment);
    }

    public boolean isRemainderActivity()
    {
        return jsonActivity.getDuration() == null;
    }

    public void setRemainderActivity(boolean remainder)
    {
        day.setRemainderActivity(jsonActivity, remainder);
    }

    @Override
    public int getRow()
    {
        return index;
    }

    @Override
    public String toString()
    {
        return "Activity [activity=" + jsonActivity + ", index=" + index + "]";
    }
}
