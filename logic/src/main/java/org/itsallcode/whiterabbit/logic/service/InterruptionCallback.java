package org.itsallcode.whiterabbit.logic.service;

import java.time.Duration;
import java.util.function.Consumer;

public interface InterruptionCallback
{
    static InterruptionCallback create(Consumer<Duration> addInterruptionCallback, Runnable cancelInterruptionCallback)
    {
        return new InterruptionCallback()
        {
            @Override
            public void cancelInterruption()
            {
                cancelInterruptionCallback.run();
            }

            @Override
            public void addInterruption(Duration duration)
            {
                addInterruptionCallback.accept(duration);
            }
        };
    }

    void addInterruption(Duration duration);

    void cancelInterruption();
}
