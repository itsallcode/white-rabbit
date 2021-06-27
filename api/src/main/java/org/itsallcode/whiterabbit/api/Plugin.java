package org.itsallcode.whiterabbit.api;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.MonthDataStorage;
import org.itsallcode.whiterabbit.api.features.PluginFeature;
import org.itsallcode.whiterabbit.api.features.ProjectReportExporter;

/**
 * Implement this interface to create a plugin for WhiteRabbit. Register your
 * plugin by adding file
 * {@code META-INF/services/org.itsallcode.whiterabbit.api.Plugin} containing
 * the full qualified class name to the jar.
 * 
 * Available features:
 * <ul>
 * <li>{@link ProjectReportExporter}</li>
 * <li>{@link MonthDataStorage}</li>
 * <li>{@link Holidays}</li>
 * </ul>
 */
public interface Plugin
{
    /**
     * Called once when loading the plugin.
     * 
     * @param config
     *            the configuration of the plugin.
     */
    void init(PluginConfiguration config);

    /**
     * The ID of this plugin. IDs must be unique for all plugins. The ID is used
     * as prefix for {@link PluginConfiguration#getMandatoryValue(String)}.
     * 
     * @return the ID of this plugin.
     */
    String getId();

    /**
     * Check if this plugin supports the given feature.
     * 
     * @param featureType
     *            the feature type.
     * @return <code>true</code> if this plugin supports the given feature, else
     *         <code>false</code>.
     */
    boolean supports(Class<? extends PluginFeature> featureType);

    /**
     * Get an instance of the given feature type.
     * 
     * @param featureType
     *            the feature type.
     * @param <T>
     *            the type of the feature.
     * @return the instance of the given feature. May return <code>null</code>
     *         or throw an exception if the feature is not supported.
     */
    <T extends PluginFeature> T getFeature(Class<T> featureType);

    /**
     * Called before closing the plugin. The plugin should cleanup any resources
     * in this method.
     */
    void close();
}
