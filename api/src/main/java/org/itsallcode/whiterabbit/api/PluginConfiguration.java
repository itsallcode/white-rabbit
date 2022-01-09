package org.itsallcode.whiterabbit.api;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The configuration of a {@link Plugin} that allows retrieving configuration
 * values from the WhiteRabbit properties file
 * {@code ~/.whiterabbit.properties}.
 */
public interface PluginConfiguration
{
    /**
     * Get property {@code pluginId.propertyName} from the config file. Throws
     * an exception if the property is not available.
     * 
     * @param propertyName
     *            the property name to get.
     * @return the value of the property.
     * @throws RuntimeException
     *             if the property is not available in the config file.
     */
    String getMandatoryValue(String propertyName);

    /**
     * Get property {@code pluginId.propertyName} from the config file. If the
     * property is not available then return empty Optional.
     * 
     * @param propertyName
     *            the property name to get.
     * @return the value of the property.
     */
    Optional<String> getOptionalValue(String propertyName);

    /**
     * Get the {@link Path} to the data directory.
     * 
     * @return the {@link Path} to the data directory.
     */
    Path getDataDir();
}
