package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.awt.Desktop;
import java.awt.HeadlessException;

import org.itsallcode.whiterabbit.jfxui.systemmenu.StaticInstanceHolder.InstanceFactory;
import org.itsallcode.whiterabbit.jfxui.tray.OsCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticInstanceHolderTest
{
    @Mock
    private OsCheck osCheckMock;
    private InstanceFactory instanceFactory;

    @BeforeEach
    void setup()
    {
        instanceFactory = new InstanceFactory(osCheckMock);
    }

    @Test
    void getInstanceReturnsSameInstance()
    {
        assertThat(StaticInstanceHolder.getInstance()).isSameAs(StaticInstanceHolder.getInstance());
    }

    @Test
    void getInstanceReturnsSameTypeAsFactory()
    {
        assertThat(StaticInstanceHolder.getInstance())
                .hasSameClassAs(new InstanceFactory(new OsCheck()).createInstance());
    }

    @ParameterizedTest
    @CsvSource(
    {
            "false, false, org.itsallcode.whiterabbit.jfxui.systemmenu.UnsupportedMenuIntegration",
            "false, true, org.itsallcode.whiterabbit.jfxui.systemmenu.UnsupportedMenuIntegration",
            "true, false, org.itsallcode.whiterabbit.jfxui.systemmenu.UnsupportedMenuIntegration",
            "true, true, org.itsallcode.whiterabbit.jfxui.systemmenu.MacMenuIntegration"
    })
    void instanceFactoryCreatesCorrectType(boolean systemMenuBarSupported, boolean desktopSupported,
            Class<? extends MenuIntegration> expectedType)
    {
        when(osCheckMock.supportsSystemMenuBar()).thenReturn(systemMenuBarSupported);
        lenient().when(osCheckMock.isDesktopSupported()).thenReturn(desktopSupported);

        if (!Desktop.isDesktopSupported() && desktopSupported && systemMenuBarSupported)
        {
            assertThatThrownBy(instanceFactory::createInstance).isInstanceOf(HeadlessException.class);
        }
        else
        {
            assertThat(instanceFactory.createInstance()).isInstanceOf(expectedType);
        }
    }
}
