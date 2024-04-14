package org.itsallcode.whiterabbit.jfxui.testutil;

import java.time.Duration;

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
        final long start = System.currentTimeMillis();
        while (true)
        {
            try
            {
                assertion.run();
                return;
            }
            catch (final AssertionError e)
            {
                LOG.warn("Assertion failed: {}", e.getMessage(), e);
                if (System.currentTimeMillis() - start > duration.toMillis())
                {
                    throw e;
                }
                sleepShort();
            }
        }
    }
}
