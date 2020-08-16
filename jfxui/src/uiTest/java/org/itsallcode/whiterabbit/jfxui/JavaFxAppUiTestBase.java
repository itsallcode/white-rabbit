package org.itsallcode.whiterabbit.jfxui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
abstract class JavaFxAppUiTestBase
{
    private static final Logger LOG = LogManager.getLogger(JavaFxAppUiTestBase.class);

    private JavaFxApp javaFxApp;

    @TempDir
    Path workingDir;

    private Path dataDir;
    private Clock clockMock;
    private ScheduledExecutorService executorServiceMock;
    private Instant now = Instant.parse("2007-12-03T10:15:30.20Z");
    private Locale locale = Locale.GERMANY;

    private Runnable updateEverySecondRunnable;

    private Runnable updateEveryMinuteRunnable;

    protected void tickSecond()
    {
        addTime(Duration.ofSeconds(1));
        LOG.info("Tick second to {}", now);
        updateEverySecondRunnable.run();
    }

    protected void tickMinute()
    {
        addTime(Duration.ofMinutes(1));
        LOG.info("Tick minute to {}", now);
        updateEverySecondRunnable.run();
        updateEveryMinuteRunnable.run();
    }

    private void addTime(Duration duration)
    {
        now = now.plus(duration);
    }

    @SuppressWarnings("null")
    protected void doStart(Stage stage)
    {
        LOG.info("Starting application using stage {}", stage);

        clockMock = mock(Clock.class);
        executorServiceMock = mock(ScheduledExecutorService.class);
        final ScheduledFuture<?> scheduledFutureMock = mock(ScheduledFuture.class);

        when(executorServiceMock.schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocation -> {
                    final Long delayMillis = invocation.getArgument(1, Long.class);
                    final Runnable runnable = invocation.getArgument(0, Runnable.class);
                    LOG.debug("Mock scheduler called: {} -> {}", Duration.ofMillis(delayMillis), runnable);
                    return scheduledFutureMock;
                });

        when(clockMock.getZone()).thenReturn(ZoneId.of("Europe/Berlin"));
        when(clockMock.instant()).thenAnswer(invocation -> now);

        prepareConfiguration();

        javaFxApp = new JavaFxApp(() -> workingDir, clockMock, executorServiceMock);
        javaFxApp.init();
        javaFxApp.start(stage);

        captureScheduledRunnables();
        LOG.info("Application startup finished");
    }

    protected void doStop()
    {
        LOG.info("Preparing application shutdown");
        javaFxApp.prepareShutdown();
        LOG.info("Application shutdown done");
    }

    private void captureScheduledRunnables()
    {
        @SuppressWarnings("null")
        final ArgumentCaptor<Runnable> arg = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceMock, times(2)).schedule(arg.capture(), eq(0L), eq(TimeUnit.MILLISECONDS));
        updateEverySecondRunnable = arg.getAllValues().get(0);
        updateEveryMinuteRunnable = arg.getAllValues().get(1);
    }

    private void prepareConfiguration()
    {
        dataDir = workingDir.resolve("data");
        String configFileContent = "data = " + dataDir.toString().replace('\\', '/') + "\n";
        configFileContent += "locale = " + locale.getLanguage() + "\n";
        try
        {
            Files.createDirectories(dataDir);
            Files.write(workingDir.resolve("time.properties"), configFileContent.getBytes(StandardCharsets.UTF_8));
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    public void setCurrentTime(Instant now)
    {
        this.now = now;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    abstract void start(Stage stage);

    abstract void stop();
}
