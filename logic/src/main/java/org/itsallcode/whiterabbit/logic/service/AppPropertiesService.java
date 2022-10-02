package org.itsallcode.whiterabbit.logic.service;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

public class AppPropertiesService
{
    private static final String RESOURCE_NAME = "/white-rabbit.properties";

    public AppProperties load()
    {
        return load(RESOURCE_NAME);
    }

    AppProperties load(final String resourceName)
    {
        final URL resourceUrl = getResourceUrl(resourceName);
        final Properties properties = loadProperties(resourceUrl);
        return new AppProperties(properties);
    }

    private Properties loadProperties(final URL resourceUrl)
    {
        final Properties properties = new Properties();
        try (InputStream inputStream = resourceUrl.openStream())
        {
            properties.load(inputStream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error loading properties from " + resourceUrl, e);
        }
        return properties;
    }

    private URL getResourceUrl(final String resourceName)
    {
        final URL resourceUrl = getClass().getResource(resourceName);
        if (resourceUrl == null)
        {
            throw new IllegalStateException("Resource '" + resourceName + "' not found in classpath");
        }
        return resourceUrl;
    }

    public class AppProperties
    {
        private final Properties properties;

        private AppProperties(final Properties properties)
        {
            this.properties = properties;
        }

        public String getVersion()
        {
            return getRequiredProperty("version");
        }

        public Instant getBuildDate()
        {
            return Instant.parse(getRequiredProperty("buildDate"));
        }

        private String getRequiredProperty(final String key)
        {
            final Optional<String> value = Optional.ofNullable(properties.getProperty(key));
            return value.orElseThrow(() -> new IllegalStateException("Key '" + key + "' not found in " + properties));
        }
    }
}
