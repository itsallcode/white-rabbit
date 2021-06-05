package org.itsallcode.whiterabbit.plugin.csv;

import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CSVConfigTest {
    @Mock
    PluginConfiguration pluginConfiguration;

    @Test
    void testCsvConfig()
    {
        final Path testPath = Paths.get(".");
        when(pluginConfiguration.getOptionalValue("destination")).thenReturn(java.util.Optional.of("."));
        when(pluginConfiguration.getOptionalValue("separator")).thenReturn(java.util.Optional.of("\t"));
        when(pluginConfiguration.getOptionalValue("filter_for_weekdays")).thenReturn(java.util.Optional.of("True"));

        final CSVConfig csvConfig = new CSVConfig(pluginConfiguration);
        assertThat(csvConfig.getOutPath()).isEqualByComparingTo(testPath);
        assertThat(csvConfig.getFilterForWeekDays()).isEqualTo(true);
        assertThat(csvConfig.getSeparator()).isEqualTo("\t");
    }
}
