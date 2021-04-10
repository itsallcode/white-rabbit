package org.itsallcode.whiterabbit.logic.service.plugin;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.logic.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PluginManagerTest
{
    @Mock
    private PluginRegistry pluginRegistryMock;

    @Mock
    private PluginWrapper plugin1;
    @Mock
    private PluginWrapper plugin2;

    private PluginManager pluginManager;

    @BeforeEach
    void setUp()
    {
        pluginManager = new PluginManager(pluginRegistryMock);
        lenient().when(plugin1.getId()).thenReturn("plugin1");
        lenient().when(plugin2.getId()).thenReturn("plugin2");
    }

    @Test
    void create()
    {
        final Config config = mock(Config.class);
        assertThat(PluginManager.create(config)).isNotNull();
    }

    @Test
    void getProjectReportExporterPlugins_noPluginAvailable()
    {
        simulatePlugins();
        assertThat(pluginManager.getProjectReportExporterPlugins()).isEmpty();
    }

    @Test
    void getProjectReportExporterPlugins_unsupportedPluginAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(false);
        assertThat(pluginManager.getProjectReportExporterPlugins()).isEmpty();
    }

    @Test
    void getProjectReportExporterPlugins_supportedPluginAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(true);
        assertThat(pluginManager.getProjectReportExporterPlugins()).containsExactly("plugin1");
    }

    private void simulatePlugins(PluginWrapper... plugins)
    {
        lenient().when(pluginRegistryMock.getAllPlugins()).thenReturn(asList(plugins));
        for (final PluginWrapper plugin : plugins)
        {
            lenient().when(pluginRegistryMock.getPlugin(plugin.getId())).thenReturn(plugin);
        }
    }

    @Test
    void getProjectReportExporter_noPluginAvailable_throwsException()
    {
        simulatePlugins();
        assertThatThrownBy(() -> pluginManager.getProjectReportExporter("unknown"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Plugin 'unknown' not found");
    }

    @Test
    void getProjectReportExporter_availableButNotSupported()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(false);
        assertThatThrownBy(() -> pluginManager.getProjectReportExporter("plugin1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Plugin 'plugin1' does not support feature " + ProjectReportExporter.class.getName());
    }

    @Test
    void getProjectReportExporter_availableAndSupported()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(true);
        final ProjectReportExporter featureMock = mock(ProjectReportExporter.class);
        when(plugin1.getFeature(ProjectReportExporter.class)).thenReturn(featureMock);

        assertThat(pluginManager.getProjectReportExporter("plugin1")).isNotNull().isSameAs(featureMock);
    }

    @Test
    void getMonthDataStorage_singleInstanceAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(true);
        final MonthDataStorage featureMock = mock(MonthDataStorage.class);
        when(plugin1.getFeature(MonthDataStorage.class)).thenReturn(featureMock);

        assertThat(pluginManager.getMonthDataStorage()).isPresent().containsSame(featureMock);
    }

    @Test
    void getMonthDataStorage_noInstanceAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(false);

        assertThat(pluginManager.getMonthDataStorage()).isEmpty();
    }

    @Test
    void getMonthDataStorage_multipleInstancesAvailable()
    {
        simulatePlugins(plugin1, plugin2);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(true);
        when(plugin2.supports(MonthDataStorage.class)).thenReturn(true);

        assertThatThrownBy(() -> pluginManager.getMonthDataStorage())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Found multiple plugins supporting " + MonthDataStorage.class.getName()
                                + ": [plugin1, plugin2]. Please add only one storage plugin to the classpath.");
    }

    @Test
    void getAllPlugins()
    {
        final Collection<PluginWrapper> plugins = new ArrayList<>();
        when(pluginRegistryMock.getAllPlugins()).thenReturn(plugins);
        assertThat(pluginManager.getAllPlugins()).isSameAs(plugins);
    }

    @Test
    void close()
    {
        pluginManager.close();
        verify(pluginRegistryMock).close();
    }

}
