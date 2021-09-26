package org.itsallcode.whiterabbit.jfxui.systemmenu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;

import java.awt.Desktop;

import org.itsallcode.whiterabbit.jfxui.OsCheck;
import org.itsallcode.whiterabbit.jfxui.systemmenu.StaticInstanceHolder.InstanceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    void instanceFactoryCreatesHeadlessType()
    {
        when(osCheckMock.isDesktopSupported()).thenReturn(false);

        assertThat(instanceFactory.createInstance()).isInstanceOf(HeadlessDesktopIntegration.class);
    }

    @Test
    void instanceFactoryCreatesSupportedType()
    {
        assumeTrue(Desktop.isDesktopSupported(), "No headless support");
        when(osCheckMock.isDesktopSupported()).thenReturn(true);

        assertThat(instanceFactory.createInstance()).isInstanceOf(DesktopIntegrationImpl.class);
    }
}
