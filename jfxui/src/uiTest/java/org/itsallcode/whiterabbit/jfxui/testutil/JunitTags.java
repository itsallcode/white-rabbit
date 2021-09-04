package org.itsallcode.whiterabbit.jfxui.testutil;

public class JunitTags
{
    /**
     * Marks a test as unstable that will be skipped in the CI build.
     */
    public static final String FLAKY = "FLAKY";

    private JunitTags()
    {
        // not instantiable
    }
}
