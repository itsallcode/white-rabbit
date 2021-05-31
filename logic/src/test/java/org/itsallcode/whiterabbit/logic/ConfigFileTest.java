package org.itsallcode.whiterabbit.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigFileTest
{
    private static final Path CONFIG_FILE = Paths.get("dummy-config-file");

    @Mock
    private Properties propertiesMock;
    @Mock
    private WorkingDirProvider dirProvider;
    private ConfigFile configFile;

    @BeforeEach
    void setUp()
    {
        configFile = new ConfigFile(dirProvider, propertiesMock, CONFIG_FILE);
    }

    @Test
    void getFile()
    {
        assertThat(configFile.getConfigFile()).isSameAs(CONFIG_FILE);
    }

    @Test
    void allowMultipleInstances_returnsFalse_whenNoPropertyAvailable()
    {
        assertThat(configFile.allowMultipleInstances()).isFalse();
    }

    @Test
    void allowMultipleInstances_returnsTrue_whenPropertyAvailable()
    {
        when(propertiesMock.getProperty("allow_multiple_instances")).thenReturn("true");
        assertThat(configFile.allowMultipleInstances()).isTrue();
    }

    @Test
    void getDataDir_whenPropertyAvailable()
    {
        when(propertiesMock.getProperty("data")).thenReturn("data-dir");
        assertThat(configFile.getDataDir()).isEqualTo(Paths.get("data-dir").toAbsolutePath());
    }

    @Test
    void getDataDir_fails_whenNoPropertyAvailable()
    {
        when(propertiesMock.getProperty("data")).thenReturn(null);
        assertThatThrownBy(() -> configFile.getDataDir())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Property 'data' not found in config file dummy-config-file");
    }

    @Test
    void getCurrentHoursPerDay_failsForInvalidValue()
    {
        when(propertiesMock.getProperty("current_working_time_per_day")).thenReturn("invalid");
        assertThatThrownBy(() -> configFile.getCurrentHoursPerDay())
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void getCurrentHoursPerDay_returnsDefault()
    {
        when(propertiesMock.getProperty("current_working_time_per_day")).thenReturn(null);
        assertThat(configFile.getCurrentHoursPerDay()).isEmpty();
    }

    @Test
    void getCurrentHoursPerDay_returnsCustomValue()
    {
        when(propertiesMock.getProperty("current_working_time_per_day")).thenReturn("PT7H30M");
        assertThat(configFile.getCurrentHoursPerDay()).isPresent().hasValue(Duration.ofHours(7).plusMinutes(30));
    }

    @Test
    void getLocale_returnsDefault()
    {
        when(propertiesMock.getProperty("locale")).thenReturn(null);
        assertThat(configFile.getLocale()).isEqualTo(Locale.getDefault());
    }

    @Test
    void getLocale_returnsCustomValue()
    {
        when(propertiesMock.getProperty("locale")).thenReturn("zh");
        assertThat(configFile.getLocale()).isEqualTo(Locale.forLanguageTag("zh"));
    }

    @Test
    void mandatoryValueFailsForMissingValue()
    {
        assertThrows(IllegalStateException.class, () -> {
            configFile.getMandatoryValue("missing.mandatory.property");
        });
    }

    @Test
    void optionalValueReturnsEmptyOptionalForMissingValue()
    {
        assertThat(configFile.getOptionalValue("missing.optional.property")).isEmpty();
    }
}
