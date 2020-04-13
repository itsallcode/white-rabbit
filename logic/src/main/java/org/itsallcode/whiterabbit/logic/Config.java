package org.itsallcode.whiterabbit.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
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
        return Paths.get(getMandatoryValue("data")).toAbsolutePath();
    }

    public Locale getLocale()
    {
        return getOptionalValue("locale").map(Locale::forLanguageTag).orElseGet(Locale::getDefault);
    }

    private String getMandatoryValue(String param)
    {
        return getOptionalValue(param).orElseThrow(
                () -> new IllegalStateException("Property '" + param + "' not found in config file " + configFile));
    }

    private Optional<String> getOptionalValue(String param)
    {
        return Optional.ofNullable(this.properties.getProperty(param));
    }
}
