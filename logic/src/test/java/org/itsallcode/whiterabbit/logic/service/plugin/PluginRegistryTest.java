package org.itsallcode.whiterabbit.logic.service.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin.AppPluginFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PluginRegistryTest
{
    private static final int EXPECTED_PLUGIN_COUNT = 2;

    @Mock
    private Config configMock;
    private PluginRegistry pluginRegistry;

    @BeforeEach
    void setUp()
    {
        pluginRegistry = new PluginRegistry(configMock);
    }

    @Test
    void loadFromEmptyPluginDir(@TempDir Path tempDir)
    {
        when(configMock.getPluginDir()).thenReturn(tempDir);
        pluginRegistry.load();
        assertThat(pluginRegistry.getAllPlugins()).hasSize(EXPECTED_PLUGIN_COUNT);
    }

    @Test
    void loadFromNonExistingPluginDir()
    {
        when(configMock.getPluginDir()).thenReturn(Paths.get("no-such-directory"));
        pluginRegistry.load();
        assertThat(pluginRegistry.getAllPlugins()).hasSize(EXPECTED_PLUGIN_COUNT);
    }

    @Test
    void loadInvalidJar(@TempDir Path tempDir) throws IOException
    {
        when(configMock.getPluginDir()).thenReturn(tempDir);
        Files.writeString(tempDir.resolve("invalid-plugin.jar"), "");
        pluginRegistry.load();
        assertThat(pluginRegistry.getAllPlugins()).hasSize(EXPECTED_PLUGIN_COUNT);
    }

    @Test
    void getAllPlugins_returnsEmptyList_WhenNotLoadedYet()
    {
        assertThat(pluginRegistry.getAllPlugins()).isEmpty();
    }

    @Test
    void getAllPlugins_returnsPlugins_WhenLoaded()
    {
        pluginRegistry.load();
        final Collection<AppPluginImpl> plugins = pluginRegistry.getAllPlugins();
        assertThat(plugins).hasSize(EXPECTED_PLUGIN_COUNT);
    }

    @Test
    void getPlugin_failsWhenNotLoadedAndPluginNotAvailable()
    {
        assertThatThrownBy(() -> pluginRegistry.getPlugin("unknown"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Plugin 'unknown' not found. Available plugins: []");
    }

    @Test
    void getPlugin_failsWhenLoadedAndPluginNotAvailable()
    {
        pluginRegistry.load();
        assertThatThrownBy(() -> pluginRegistry.getPlugin("unknown"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Plugin 'unknown' not found. Available plugins: [" + TestingReportPlugin.PLUGIN_ID + ", "
                        + TestingPlugin.PLUGIN_ID + "]");
    }

    @Test
    void getPlugin_returnsPluginWhenAvailable()
    {
        pluginRegistry.load();
        final AppPluginImpl plugin = pluginRegistry.getPlugin(TestingPlugin.PLUGIN_ID);
        assertThat(plugin.getId()).isEqualTo(TestingPlugin.PLUGIN_ID);
        assertThat(plugin.getOrigin().getDescription()).isEqualTo("included");
        assertThat(plugin.getFeatures()).isEmpty();
        final Optional<TestingPlugin> optionalInstance = plugin.getFeature(TestingPlugin.class);
        assertThat(optionalInstance).isPresent();
        final TestingPlugin pluginInstance = optionalInstance.get();
        assertThat(pluginInstance).isInstanceOf(TestingPlugin.class);
        assertThat(pluginInstance.getId()).isEqualTo(TestingPlugin.PLUGIN_ID);
        assertThat(pluginInstance.isClosed()).isFalse();
    }

    @Test
    void getPlugin_featureNotAvailable()
    {
        pluginRegistry.load();
        final AppPluginImpl plugin = pluginRegistry.getPlugin(TestingPlugin.PLUGIN_ID);
        assertThat(plugin.getId()).isEqualTo(TestingPlugin.PLUGIN_ID);
        assertThat(plugin.getOrigin().getDescription()).isEqualTo("included");
        assertThat(plugin.getFeatures()).isEmpty();
        assertThat(plugin.getFeature(ProjectReportExporter.class)).isEmpty();
    }

    @Test
    void getPlugin_withFeature()
    {
        pluginRegistry.load();
        final AppPluginImpl plugin = pluginRegistry.getPlugin(TestingReportPlugin.PLUGIN_ID);
        assertThat(plugin.getId()).isEqualTo(TestingReportPlugin.PLUGIN_ID);
        assertThat(plugin.getFeatures()).hasSize(1).containsExactly(AppPluginFeature.PROJECT_REPORT);
    }

    @Test
    void pluginConfig_getMandatoryValue_ReturnsValue()
    {
        pluginRegistry.load();
        final TestingPlugin pluginInstance = pluginRegistry.getPlugin(TestingPlugin.PLUGIN_ID)
                .getFeature(TestingPlugin.class).get();

        when(configMock.getMandatoryValue(TestingPlugin.PLUGIN_ID + ".property")).thenReturn("propertyValue");
        assertThat(pluginInstance.getConfig().getMandatoryValue("property")).isEqualTo("propertyValue");
    }

    @Test
    void close_whenNoPluginLoaded()
    {
        assertDoesNotThrow(() -> pluginRegistry.close());
    }

    @Test
    void close_closesPlugins()
    {
        pluginRegistry.load();
        final TestingPlugin plugin = pluginRegistry.getPlugin(TestingPlugin.PLUGIN_ID).getFeature(TestingPlugin.class)
                .get();
        assertThat(plugin.isClosed()).isFalse();

        pluginRegistry.close();

        assertThat(plugin.isClosed()).isTrue();
    }
}
