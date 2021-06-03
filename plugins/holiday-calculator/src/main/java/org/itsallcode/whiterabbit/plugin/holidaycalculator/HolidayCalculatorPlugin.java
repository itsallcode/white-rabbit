package org.itsallcode.whiterabbit.plugin.holidaycalculator;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;

public class HolidayCalculatorPlugin implements Plugin
{
    private PluginConfiguration config;

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public String getId()
    {
        return "holidaycalculator";
    }

    @Override
    public void close()
    {
        // ignore
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isAssignableFrom(CalculatedHolidays.class);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        if (featureType.isAssignableFrom(CalculatedHolidays.class))
        {
            return featureType.cast(new CalculatedHolidays(config.getDataDir()));
        }
        throw new IllegalArgumentException("Feature " + featureType.getName() + " not supported by plugin " + getId());
    }
}
