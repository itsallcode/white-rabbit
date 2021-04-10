package org.itsallcode.whiterabbit.jfxui.testutil;

import static org.mockito.Mockito.mock;

import org.itsallcode.whiterabbit.api.Plugin;
import org.itsallcode.whiterabbit.api.PluginConfiguration;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;

public class UiTestDummyPlugin implements Plugin
{
    public static final String ID = "uitest-dummy";

    @Override
    public void init(PluginConfiguration config)
    {
    }

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return featureType.isAssignableFrom(ProjectReportExporter.class);
    }

    @Override
    public <T extends PluginFeature> T getFeature(Class<T> featureType)
    {
        return mock(featureType);
    }

    @Override
    public void close()
    {

    }
}
