package org.itsallcode.whiterabbit.plugin.csv;

import org.itsallcode.whiterabbit.api.PluginConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVConfig {
    private final boolean filterForWeekDays;
    private final Path outPath;
    private final String separator;

    public boolean getFilterForWeekDays() {
        return filterForWeekDays;
    }

    public Path getOutPath() {
        return outPath;
    }

    public String getSeparator() {
        return separator;
    }

    public CSVConfig(PluginConfiguration config) {
        final String defaultPath = System.getProperty("user.home");
        this.outPath = Paths.get(config.getOptionalValue("destination").orElse(defaultPath));
        this.separator = config.getOptionalValue("separator").orElse(",");
        this.filterForWeekDays =
                config.getOptionalValue("filter_for_weekdays")
                        .orElse("false").equalsIgnoreCase("true");
    }
}
