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
import java.util.Optional;

import org.itsallcode.whiterabbit.api.features.Holidays;
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
    private AppPluginImpl plugin1;
    @Mock
    private AppPluginImpl plugin2;

    @Mock
    Holidays holidays1;
    @Mock
    Holidays holidays2;

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
        assertThat(pluginManager.findPluginsSupporting(ProjectReportExporter.class)).isEmpty();
    }

    @Test
    void getProjectReportExporterPlugins_unsupportedPluginAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(false);
        assertThat(pluginManager.findPluginsSupporting(ProjectReportExporter.class)).isEmpty();
    }

    @Test
    void getProjectReportExporterPlugins_supportedPluginAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(true);
        assertThat(pluginManager.findPluginsSupporting(ProjectReportExporter.class)).containsExactly(plugin1);
    }

    @Test
    void getAllFeatures()
    {
        when(plugin1.getFeature(Holidays.class)).thenReturn(Optional.of(holidays1));
        when(plugin2.getFeature(Holidays.class)).thenReturn(Optional.of(holidays2));
        when(plugin1.supports(Holidays.class)).thenReturn(true);
        when(plugin2.supports(Holidays.class)).thenReturn(true);
        simulatePlugins(plugin1, plugin2);
        assertThat(pluginManager.getAllFeatures(Holidays.class)).containsExactly(holidays1, holidays2);
    }

    @Test
    void pluginReturnsEmptyOptional()
    {
        when(plugin1.getFeature(Holidays.class)).thenReturn(Optional.of(holidays1));
        when(plugin2.getFeature(Holidays.class)).thenReturn(Optional.empty());
        when(plugin1.supports(Holidays.class)).thenReturn(true);
        when(plugin2.supports(Holidays.class)).thenReturn(true);
        simulatePlugins(plugin1, plugin2);
        assertThat(pluginManager.getAllFeatures(Holidays.class)).containsExactly(holidays1);
    }

    private void simulatePlugins(AppPluginImpl... plugins)
    {
        lenient().when(pluginRegistryMock.getAllPlugins()).thenReturn(asList(plugins));
        for (final AppPluginImpl plugin : plugins)
        {
            lenient().when(pluginRegistryMock.getPlugin(plugin.getId())).thenReturn(plugin);
        }
    }

    @Test
    void getProjectReportExporter_noPluginAvailable_throwsException()
    {
        simulatePlugins();

        assertThat(pluginManager.getAllFeatures(ProjectReportExporter.class)).isEmpty();
    }

    @Test
    void getProjectReportExporter_availableButNotSupported()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(false);

        assertThat(pluginManager.getAllFeatures(ProjectReportExporter.class)).isEmpty();
    }

    @Test
    void getProjectReportExporter_availableAndSupported()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(ProjectReportExporter.class)).thenReturn(true);
        final ProjectReportExporter featureMock = mock(ProjectReportExporter.class);
        when(plugin1.getFeature(ProjectReportExporter.class)).thenReturn(Optional.of(featureMock));

        assertThat(pluginManager.getAllFeatures(ProjectReportExporter.class)).hasSize(1).contains(featureMock);
    }

    @Test
    void getMonthDataStorage_singleInstanceAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(true);
        final MonthDataStorage featureMock = mock(MonthDataStorage.class);
        when(plugin1.getFeature(MonthDataStorage.class)).thenReturn(Optional.of(featureMock));

        assertThat(pluginManager.getUniqueFeature(MonthDataStorage.class)).isPresent().containsSame(featureMock);
    }

    @Test
    void getMonthDataStorage_noInstanceAvailable()
    {
        simulatePlugins(plugin1);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(false);

        assertThat(pluginManager.getUniqueFeature(MonthDataStorage.class)).isEmpty();
    }

    @Test
    void getMonthDataStorage_multipleInstancesAvailable()
    {
        simulatePlugins(plugin1, plugin2);
        when(plugin1.supports(MonthDataStorage.class)).thenReturn(true);
        when(plugin2.supports(MonthDataStorage.class)).thenReturn(true);

        assertThatThrownBy(() -> pluginManager.getUniqueFeature(MonthDataStorage.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Found multiple plugins supporting " + MonthDataStorage.class.getName()
                                + ": [plugin1, plugin2]. Please add only one storage plugin to the classpath.");
    }

    @Test
    void getAllPlugins()
    {
        final Collection<AppPluginImpl> plugins = new ArrayList<>();
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
