package org.itsallcode.whiterabbit.jfxui.testutil;

import java.time.Duration;

public class TestUtil
{
    private TestUtil()
    {
        // Not instantiable
    }

    public static void sleepShort()
    {
        sleep(Duration.ofMillis(100));
    }

    private static void sleep(Duration duration)
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
}
