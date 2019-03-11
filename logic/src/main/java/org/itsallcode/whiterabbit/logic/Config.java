package org.itsallcode.whiterabbit.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config
{

    private final Properties properties;
    private final Path configFile;

    private Config(Properties properties, Path configFile)
    {
        this.properties = properties;
        this.configFile = configFile;
    }

    public static Config read(Path configFile)
    {
        return new Config(loadProperties(configFile), configFile);
    }

    private static Properties loadProperties(Path configFile)
    {
        try (InputStream stream = Files.newInputStream(configFile))
        {
            final Properties props = new Properties();
            props.load(stream);
            return props;
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading " + configFile, e);
        }
    }

    public Path getDataDir()
    {
        return Paths.get(getMandatoryValue("data"));
    }

    private String getMandatoryValue(String param)
    {
        final String value = this.properties.getProperty(param);
        if (value == null)
        {
            throw new IllegalStateException(
                    "Property '" + param + "' not found in config file " + configFile);
        }
        return value;
    }
}
