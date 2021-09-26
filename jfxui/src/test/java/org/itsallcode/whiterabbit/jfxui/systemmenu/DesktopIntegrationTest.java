package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DesktopIntegrationTest
{
    @Test
    void getInstanceReturnsSameInstance()
    {
        assertThat(DesktopIntegration.getInstance()).isSameAs(DesktopIntegration.getInstance());
    }

    @Test
    void getInstanceReturnsSameInstanceAsStaticInstanceHolder()
    {
        assertThat(DesktopIntegration.getInstance()).isSameAs(StaticInstanceHolder.getInstance());
    }
}
