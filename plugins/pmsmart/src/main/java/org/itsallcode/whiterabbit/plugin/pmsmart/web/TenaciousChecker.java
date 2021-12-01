package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import java.time.Duration;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TenaciousChecker
{
    private static final Logger LOG = LogManager.getLogger(TenaciousChecker.class);
    private static final long NUMBER_OF_CHECKS = 10;

    private final Supplier<Boolean> condition;

    public TenaciousChecker(Supplier<Boolean> condition)
    {
        this.condition = condition;
    }

    public boolean check(Duration maxDuration)
    {
        final Duration interval = maxDuration.dividedBy(NUMBER_OF_CHECKS);
        for (int i = 0; i < NUMBER_OF_CHECKS; i++)
        {
            if (condition.get())
            {
                LOG.debug("Condition was met after {} ms.", interval.multipliedBy(i).toMillis());
                return true;
            }
            try
            {
                Thread.sleep(interval.toMillis());
            }
            catch (final InterruptedException e)
            {
                return false;
            }
        }
        return false;
    }
}
