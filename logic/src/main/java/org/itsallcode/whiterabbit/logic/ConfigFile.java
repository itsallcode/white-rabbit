package org.itsallcode.whiterabbit.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ConfigFile implements Config
{
    private static final Logger LOG = LogManager.getLogger(ConfigFile.class);

    private final Properties properties;
    private final Path file;

    private ConfigFile(Properties properties, Path configFile)
    {
        this.properties = properties;
        this.file = configFile;
    }

    static ConfigFile read(Path configFile)
    {
        return new ConfigFile(loadProperties(configFile), configFile);
    }

    private static Properties loadProperties(Path configFile)
    {
        if (!Files.exists(configFile))
        {
            throw new IllegalStateException("Config file not found at '" + configFile + "'");
        }
        try (InputStream stream = Files.newInputStream(configFile))
        {
            final Properties props = new Properties();
            props.load(stream);
            return props;
        }
        catch (final IOException e)
        {
            LOG.error("Error reading config file {}", configFile, e);
            throw new UncheckedIOException("Error reading " + configFile, e);
        }
    }

    @Override
    public Path getDataDir()
    {
        return Paths.get(getMandatoryValue("data")).toAbsolutePath();
    }

    @Override
    public Locale getLocale()
    {
        return getOptionalValue("locale").map(Locale::forLanguageTag).orElseGet(Locale::getDefault);
    }

    @Override
    public Optional<Duration> getCurrentHoursPerDay()
    {
        return getOptionalValue("current_working_time_per_day").map(Duration::parse);
    }

    private String getMandatoryValue(String param)
    {
        return getOptionalValue(param).orElseThrow(
                () -> new IllegalStateException("Property '" + param + "' not found in config file " + file));
    }

    private Optional<String> getOptionalValue(String param)
    {
        return Optional.ofNullable(this.properties.getProperty(param));
    }
}