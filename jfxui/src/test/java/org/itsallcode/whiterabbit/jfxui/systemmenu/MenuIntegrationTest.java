package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MenuIntegrationTest
{
    @Test
    void getInstanceReturnsSameInstance()
    {
        assertThat(MenuIntegration.getInstance()).isSameAs(MenuIntegration.getInstance());
    }

    @Test
    void getInstanceReturnsSameInstanceAsStaticInstanceHolder()
    {
        assertThat(MenuIntegration.getInstance()).isSameAs(StaticInstanceHolder.getInstance());
    }
}
