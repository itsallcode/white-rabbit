package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HolidayCalculatorPluginTest
{
    private HolidayCalculatorPlugin holidayCalculatorPlugin;

    @BeforeEach
    void setUp()
    {
        holidayCalculatorPlugin = new HolidayCalculatorPlugin();
    }

    @Test
    void pluginSupportsHolidaysFeature()
    {
        assertThat(holidayCalculatorPlugin.supports(Holidays.class)).isTrue();
    }

    @Test
    void pluginDoesNotSupportProjectReportExporterFeature()
    {
        assertThat(holidayCalculatorPlugin.supports(ProjectReportExporter.class)).isFalse();
    }
}
