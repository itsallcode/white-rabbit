package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeadlessDesktopIntegrationTest
{
    private HeadlessDesktopIntegration menuIntegration;

    @BeforeEach
    void setUp()
    {
        menuIntegration = new HeadlessDesktopIntegration();
    }

    @Test
    void register()
    {
        assertDoesNotThrow(menuIntegration::register);
    }

    @Test
    void testSetUiActions()
    {
        assertDoesNotThrow(() -> menuIntegration.setUiActions(null));
    }
}
