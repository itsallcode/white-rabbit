package org.itsallcode.whiterabbit.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractPluginTest
{
    private static final String PLUGIN_ID = "testing-plugin";

    @Mock
    SupportedFeature supportedFeatureMock;

    private TestingAbstractPlugin plugin;

    @BeforeEach
    void setUp()
    {
        plugin = new TestingAbstractPlugin();
    }

    @Test
    void supportsReturnsTrueForSupportedFeature()
    {
        assertThat(plugin.supports(SupportedFeature.class)).isTrue();
    }

    @Test
    void supportsReturnsFalseForUnsupportedFeature()
    {
        assertThat(plugin.supports(UnsupportedFeature.class)).isFalse();
    }

    @Test
    void gettingUnsupportedFeatureThrowsException()
    {
        assertThatThrownBy(() -> plugin.getFeature(UnsupportedFeature.class))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(
                        "Feature " + UnsupportedFeature.class.getName() + " not supported by plugin " + PLUGIN_ID);
    }

    @Test
    void gettingSupportedFeatureWorks()
    {
        assertThat(plugin.getFeature(SupportedFeature.class)).isSameAs(supportedFeatureMock);
    }

    @Test
    void getPluginId()
    {
        assertThat(plugin.getId()).isEqualTo(PLUGIN_ID);
    }

    private interface SupportedFeature extends PluginFeature
    {

    }

    private interface UnsupportedFeature extends PluginFeature
    {

    }

    private class TestingAbstractPlugin extends AbstractPlugin<SupportedFeature>
    {
        protected TestingAbstractPlugin()
        {
            super(PLUGIN_ID, SupportedFeature.class);
        }

        @Override
        protected SupportedFeature createInstance()
        {
            return supportedFeatureMock;
        }
    }
}
