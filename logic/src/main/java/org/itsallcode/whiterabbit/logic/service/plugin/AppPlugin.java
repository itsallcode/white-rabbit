package org.itsallcode.whiterabbit.logic.service.plugin;

import java.util.Collection;

import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;

public interface AppPlugin
{
    String getId();

    Collection<AppPluginFeature> getFeatures();

    PluginOrigin getOrigin();

    public enum AppPluginFeature
    {
        DATA_STORAGE(MonthDataStorage.class), PROJECT_REPORT(ProjectReportExporter.class);

        private final Class<? extends PluginFeature> featureInterface;

        private AppPluginFeature(Class<? extends PluginFeature> featureClass)
        {
            this.featureInterface = featureClass;
        }

        public String getName()
        {
            return featureInterface.getSimpleName();
        }

        public Class<? extends PluginFeature> getFeatureClass()
        {
            return featureInterface;
        }
    }

    public interface PluginOrigin
    {
        String getDescription();

        boolean isExternal();
    }
}
