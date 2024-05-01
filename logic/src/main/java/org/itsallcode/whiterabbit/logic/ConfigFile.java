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
    private final WorkingDirProvider dirProvider;

    ConfigFile(final WorkingDirProvider dirProvider, final Properties properties, final Path configFile)
    {
        this.dirProvider = dirProvider;
        this.properties = properties;
        this.file = configFile;
    }

    static ConfigFile read(final WorkingDirProvider dirProvider, final Path configFile)
    {
        final Path file = configFile.normalize();
        return new ConfigFile(dirProvider, loadProperties(file), file);
    }

    private static Properties loadProperties(final Path configFile)
    {
        if (!Files.exists(configFile))
        {
            throw new IllegalStateException("Config file not found at '" + configFile + "'");
        }
        LOG.info("Reading config file from {}", configFile);
        try (InputStream stream = Files.newInputStream(configFile))
        {
            final Properties props = new Properties();
            props.load(stream);
            return props;
        }
        catch (final IOException e)
        {
            LOG.error("Error reading config file {}", configFile, e);
            throw new UncheckedIOException("Error reading config file " + configFile, e);
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

    @Override
    public Optional<Duration> getMandatoryBreak()
    {
        return getOptionalValue("mandatory_break").map(Duration::parse);
    }

    @Override
    public boolean reduceMandatoryBreakByInterruption()
    {
        return getOptionalValue("reduce_mandatory_break_by_interruption").map(Boolean::valueOf).orElse(false);
    }

    @Override
    public boolean allowMultipleInstances()
    {
        return getOptionalValue("allow_multiple_instances").map(Boolean::valueOf).orElse(false);
    }

    @Override
    public Path getConfigFile()
    {
        return file;
    }

    @Override
    public Path getUserDir()
    {
        return dirProvider.getUserDir().resolve(".whiterabbit");
    }

    @Override
    public String getMandatoryValue(final String param)
    {
        return getOptionalValue(param).orElseThrow(
                () -> new IllegalStateException("Property '" + param + "' not found in config file " + file));
    }

    @Override
    public Optional<String> getOptionalValue(final String param)
    {
        return Optional.ofNullable(this.properties.getProperty(param));
    }
}
