package org.itsallcode.whiterabbit.plugin.csv;

import static org.assertj.core.api.Assertions.assertThat;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CSVExporterPluginTest
{
    @Mock
    private PluginConfiguration configMock;

    private CSVExporterPlugin plugin;

    @BeforeEach
    void setUp()
    {
        plugin = new CSVExporterPlugin();
    }

    @Test
    void pluginSupportsProjectReportExporterFeature()
    {
        assertThat(plugin.supports(ProjectReportExporter.class)).isTrue();
    }

    @Test
    void pluginDoesNotSupportHolidaysFeature()
    {
        assertThat(plugin.supports(Holidays.class)).isFalse();
    }

    @Test
    void getFeature()
    {
        plugin.init(configMock);
        assertThat(plugin.getFeature(ProjectReportExporter.class)).isNotNull();
    }
}
