package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ConfigCommandLineArgUiTest extends JavaFxAppUiTestBase
{
    @TempDir
    Path tempDir;
    FxRobot robot;

    @Test
    void writeToCustomDataDir()
    {
        time().tickMinute();
        time().tickMinute();

        final Path jsonMonthPath = getDataDir().resolve("2007").resolve("2007-12.json");
        assertThat(jsonMonthPath).exists();
    }

    @Override
    @Start
    void start(Stage stage)
    {
        setLocale(Locale.GERMANY);
        setInitialTime(Instant.parse("2007-12-03T10:15:30.20Z"));
        final Path configPath = tempDir.resolve("test-config.prop");
        prepareConfigFile(configPath);
        setCommandLineArgs(List.of("--config=" + configPath));
        doStart(stage);
        setRobot(robot);
    }

    private void prepareConfigFile(Path configPath)
    {
        final Path dataDir = getDataDir();
        try
        {
            Files.writeString(configPath, "data = " + dataDir.toString().replace('\\', '/'));
            Files.createDirectory(dataDir);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error preparing config", e);
        }
    }

    private Path getDataDir()
    {
        return tempDir.resolve("data-dir");
    }

    @Override
    @Stop
    void stop()
    {
        doStop();
    }
}
