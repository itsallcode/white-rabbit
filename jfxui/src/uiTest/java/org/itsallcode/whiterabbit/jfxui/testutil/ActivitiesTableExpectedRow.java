package org.itsallcode.whiterabbit.jfxui.testutil;

import java.time.Duration;

import org.itsallcode.whiterabbit.logic.service.project.Project;

public class ActivitiesTableExpectedRow implements TableRowExpectedContent
{
    private final Project project;
    private final Duration duration;
    private final boolean remainder;
    private final String comment;

    private ActivitiesTableExpectedRow(Builder builder)
    {
        this.project = builder.project;
        this.duration = builder.duration;
        this.remainder = builder.remainder;
        this.comment = builder.comment;
    }

    @Override
    public Object[] expectedCellContent()
    {
        return new Object[] { project, duration, remainder, comment };
    }

    public static Builder defaultRow()
    {
        return builder().withProject(null).withRemainder(false).withDuration(Duration.ZERO).withComment(null);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builderFrom(ActivitiesTableExpectedRow activitiesTableExpectedRow)
    {
        return new Builder(activitiesTableExpectedRow);
    }

    public static final class Builder
    {
        private Project project;
        private Duration duration;
        private boolean remainder;
        private String comment;

        private Builder()
        {
        }

        private Builder(ActivitiesTableExpectedRow activitiesTableExpectedRow)
        {
            this.project = activitiesTableExpectedRow.project;
            this.duration = activitiesTableExpectedRow.duration;
            this.remainder = activitiesTableExpectedRow.remainder;
            this.comment = activitiesTableExpectedRow.comment;
        }

        public Builder withProject(Project project)
        {
            this.project = project;
            return this;
        }

        public Builder withDuration(Duration duration)
        {
            this.duration = duration;
            return this;
        }

        public Builder withRemainder(boolean remainder)
        {
            this.remainder = remainder;
            return this;
        }

        public Builder withComment(String comment)
        {
            this.comment = comment;
            return this;
        }

        public ActivitiesTableExpectedRow build()
        {
            return new ActivitiesTableExpectedRow(this);
        }
    }
}
