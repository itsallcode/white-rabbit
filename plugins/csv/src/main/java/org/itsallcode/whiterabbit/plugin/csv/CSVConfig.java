package org.itsallcode.whiterabbit.plugin.csv;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.whiterabbit.api.PluginConfiguration;

class CSVConfig
{
    private final boolean filterForWeekDays;
    private final Path outPath;
    private final String separator;

    boolean getFilterForWeekDays()
    {
        return filterForWeekDays;
    }

    Path getOutPath()
    {
        return outPath;
    }

    String getSeparator()
    {
        return separator;
    }

    CSVConfig(PluginConfiguration config)
    {
        final String defaultPath = System.getProperty("user.home");
        this.outPath = Paths.get(config.getOptionalValue("destination").orElse(defaultPath));
        this.separator = config.getOptionalValue("separator").orElse(",");
        this.filterForWeekDays = config.getOptionalValue("filter_for_weekdays")
                .orElse("false").equalsIgnoreCase("true");
    }
}
