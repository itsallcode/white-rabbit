package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayCalculatorPluginTest
{
    @Mock
    private PluginConfiguration configMock;

    @TempDir
    Path dataDir;

    private HolidayCalculatorPlugin plugin;

    @BeforeEach
    void setUp()
    {
        plugin = new HolidayCalculatorPlugin();
    }

    @Test
    void pluginSupportsHolidaysFeature()
    {
        assertThat(plugin.supports(Holidays.class)).isTrue();
    }

    @Test
    void pluginDoesNotSupportProjectReportExporterFeature()
    {
        assertThat(plugin.supports(ProjectReportExporter.class)).isFalse();
    }

    @Test
    void getFeature()
    {
        when(configMock.getDataDir()).thenReturn(dataDir);
        plugin.init(configMock);
        assertThat(plugin.getFeature(Holidays.class)).isNotNull();
    }
}
