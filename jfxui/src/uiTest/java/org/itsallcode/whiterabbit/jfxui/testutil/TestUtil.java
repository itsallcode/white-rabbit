package org.itsallcode.whiterabbit.jfxui.testutil;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestUtil
{
    private static final Logger LOG = LogManager.getLogger(TestUtil.class);

    private TestUtil()
    {
        // Not instantiable
    }

    public static void sleepLong()
    {
        sleep(Duration.ofSeconds(1));
    }

    public static void sleepShort()
    {
        sleep(Duration.ofMillis(100));
    }

    private static void sleep(final Duration duration)
    {
        try
        {
            Thread.sleep(duration.toMillis());
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    public static void retryAssertion(final Duration duration, final Runnable assertion)
    {
        final Instant start = Instant.now();
        int tries = 0;
        while (true)
        {
            try
            {
                tries++;
                assertion.run();
                return;
            }
            catch (final AssertionError e)
            {
                final Duration currentDuration = Duration.between(start, Instant.now());
                final String message = "Assertion failed after " + currentDuration + " / " + tries + " tries: "
                        + e.getMessage();
                final Duration remaining = currentDuration.minus(duration);
                if (remaining.isPositive())
                {
                    throw new AssertionError(message, e);
                }
                LOG.warn(message);
                sleepLong();
            }
        }
    }
}
