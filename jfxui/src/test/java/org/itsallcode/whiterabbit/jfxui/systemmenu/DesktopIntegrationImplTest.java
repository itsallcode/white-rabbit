package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.desktop.AboutHandler;

import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesktopIntegrationImplTest
{
    @Mock
    private Desktop desktopMock;
    @Mock
    private UiActions uiActionsMock;

    private DesktopIntegrationImpl menuIntegration;

    @BeforeEach
    void setUp()
    {
        menuIntegration = new DesktopIntegrationImpl(desktopMock);
    }

    @Test
    void settingNullUiActionsFails()
    {
        assertThatThrownBy(() -> menuIntegration.setUiActions(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void registersAboutHandler()
    {
        when(desktopMock.isSupported(Action.APP_ABOUT)).thenReturn(true);
        menuIntegration.register();
        menuIntegration.setUiActions(uiActionsMock);

        getAboutHandler().handleAbout(null);

        verify(uiActionsMock).showAboutDialog();
    }

    @Test
    void aboutHandlerFailsForMissingUiActions()
    {
        when(desktopMock.isSupported(Action.APP_ABOUT)).thenReturn(true);
        menuIntegration.register();

        final AboutHandler aboutHandler = getAboutHandler();

        assertThatThrownBy(() -> aboutHandler.handleAbout(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("UI Actions not registered");
    }

    @Test
    void aboutHandlerNotRegisteredWhenNotSupported()
    {
        when(desktopMock.isSupported(Action.APP_ABOUT)).thenReturn(false);

        menuIntegration.register();

        verify(desktopMock, never()).setAboutHandler(any());
    }

    private AboutHandler getAboutHandler()
    {
        final ArgumentCaptor<AboutHandler> arg = ArgumentCaptor.forClass(AboutHandler.class);
        verify(desktopMock).setAboutHandler(arg.capture());
        return arg.getValue();
    }
}
