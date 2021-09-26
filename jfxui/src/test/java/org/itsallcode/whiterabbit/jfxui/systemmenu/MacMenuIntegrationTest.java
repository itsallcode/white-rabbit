package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.awt.Desktop;
import java.awt.desktop.AboutHandler;

import org.itsallcode.whiterabbit.jfxui.UiActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MacMenuIntegrationTest
{
    @Mock
    private Desktop desktopMock;
    @Mock
    private UiActions uiActionsMock;

    private MacMenuIntegration menuIntegration;

    @BeforeEach
    void setUp()
    {
        menuIntegration = new MacMenuIntegration(desktopMock);
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
        menuIntegration.register();
        menuIntegration.setUiActions(uiActionsMock);

        getAboutHandler().handleAbout(null);

        verify(uiActionsMock).showAboutDialog();
    }

    @Test
    void aboutHandlerFailsForMissingUiActions()
    {
        menuIntegration.register();

        final AboutHandler aboutHandler = getAboutHandler();

        assertThatThrownBy(() -> aboutHandler.handleAbout(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("UI Actions not registered");
    }

    private AboutHandler getAboutHandler()
    {
        final ArgumentCaptor<AboutHandler> arg = ArgumentCaptor.forClass(AboutHandler.class);
        verify(desktopMock).setAboutHandler(arg.capture());
        return arg.getValue();
    }
}
