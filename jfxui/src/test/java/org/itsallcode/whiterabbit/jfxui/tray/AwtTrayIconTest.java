package org.itsallcode.whiterabbit.jfxui.tray;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AwtTrayIconTest
{
    @Mock
    TrayCallback callbackMock;

    @Test
    void test()
    {
        assumeTrue(AwtTrayIcon.isAwtTraySupported());

        final Tray tray = AwtTrayIcon.createAwtTray(callbackMock);
        try
        {
            assertThat(tray.isSupported()).isTrue();
            tray.setTooltip("tooltip");
        }
        finally
        {
            tray.removeTrayIcon();
        }
    }
}
