package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.time.Instant;

class TriggerContext
{
    private final Instant lastScheduledExecutionTime;
    private final Instant lastActualExecutionTime;
    private final Instant lastCompletionTime;

    TriggerContext(Instant lastScheduledExecutionTime, Instant lastActualExecutionTime,
            Instant lastCompletionTime)
    {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    Instant lastScheduledExecutionTime()
    {
        return lastScheduledExecutionTime;
    }

    Instant lastCompletionTime()
    {
        return lastCompletionTime;
    }

    Instant lastActualExecutionTime()
    {
        return lastActualExecutionTime;
    }
}
