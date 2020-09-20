package org.itsallcode.whiterabbit.jfxui;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.service.project.ProjectConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

@ExtendWith(MockitoExtension.class)
abstract class JavaFxAppUiTestBase
{
    private static final Logger LOG = LogManager.getLogger(JavaFxAppUiTestBase.class);
    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

    private JavaFxApp javaFxApp;

    @TempDir
    Path workingDir;

    private Path dataDir;
    private Clock clockMock;
    private ScheduledExecutorService executorServiceMock;
    private final ZoneId timeZone = ZoneId.of("Europe/Berlin");
    private Instant now = Instant.parse("2007-12-03T10:15:30.20Z");
    private Locale locale = Locale.GERMANY;

    private Runnable updateEverySecondRunnable;

    private Runnable updateEveryMinuteRunnable;

    protected void tickSecond()
    {
        addTime(Duration.ofSeconds(1));
        LOG.info("Tick second to {}", this.now);
        this.updateEverySecondRunnable.run();
    }

    protected void tickMinute()
    {
        addTime(Duration.ofMinutes(1));
        LOG.info("Tick minute to {}", this.now);
        this.updateEverySecondRunnable.run();
        this.updateEveryMinuteRunnable.run();
    }

    private void addTime(final Duration duration)
    {
        this.now = this.now.plus(duration);
    }

    protected void doStart(final Stage stage)
    {
        doStart(stage, null);
    }

    protected void doStart(final Stage stage, final ProjectConfig projectConfig)
    {
        LOG.info("Starting application using stage {}", stage);

        this.clockMock = mock(Clock.class);
        this.executorServiceMock = mock(ScheduledExecutorService.class);
        final ScheduledFuture<?> scheduledFutureMock = mock(ScheduledFuture.class);

        when(this.executorServiceMock.schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocation -> {
                    final Long delayMillis = invocation.getArgument(1, Long.class);
                    final Runnable runnable = invocation.getArgument(0, Runnable.class);
                    LOG.debug("Mock scheduler called: {} -> {}", Duration.ofMillis(delayMillis), runnable);
                    return scheduledFutureMock;
                });

        when(this.clockMock.getZone()).thenReturn(this.timeZone);
        when(this.clockMock.instant()).thenAnswer(invocation -> this.now);

        prepareConfiguration(projectConfig);

        this.javaFxApp = new JavaFxApp(() -> this.workingDir, this.clockMock, this.executorServiceMock);
        this.javaFxApp.init();
        this.javaFxApp.start(stage);

        captureScheduledRunnables();
        LOG.info("Application startup finished");
    }

    protected void doStop()
    {
        LOG.info("Preparing application shutdown");
        this.javaFxApp.prepareShutdown();
        LOG.info("Application shutdown done");
    }

    private void captureScheduledRunnables()
    {
        @SuppressWarnings("null")
        final ArgumentCaptor<Runnable> arg = ArgumentCaptor.forClass(Runnable.class);
        verify(this.executorServiceMock, times(2)).schedule(arg.capture(), eq(0L), eq(TimeUnit.MILLISECONDS));
        this.updateEverySecondRunnable = arg.getAllValues().get(0);
        this.updateEveryMinuteRunnable = arg.getAllValues().get(1);
    }

    private void prepareConfiguration(final ProjectConfig projectConfig)
    {
        this.dataDir = this.workingDir.resolve("data");
        String configFileContent = "data = " + this.dataDir.toString().replace('\\', '/') + "\n";
        configFileContent += "locale = " + this.locale.getLanguage() + "\n";
        try
        {
            Files.createDirectories(this.dataDir);
            Files.write(this.workingDir.resolve("time.properties"), configFileContent.getBytes(StandardCharsets.UTF_8));
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException(e);
        }
        if (projectConfig != null)
        {
            writeProjectsFile(projectConfig);
        }
    }

    protected LocalDate getCurrentDate()
    {
        return LocalDate.ofInstant(this.now, this.timeZone);
    }

    protected LocalTime getCurrentTimeMinutes()
    {
        return LocalTime.ofInstant(this.now, this.timeZone).truncatedTo(ChronoUnit.MINUTES);
    }

    protected int getCurrentDayRowIndex()
    {
        return getCurrentDate().getDayOfMonth() - 1;
    }

    public void setCurrentTime(final Instant now)
    {
        this.now = now;
    }

    public void setLocale(final Locale locale)
    {
        this.locale = locale;
    }

    abstract void start(Stage stage);

    abstract void stop();

    protected void assertRowContent(final TableView<?> table, final int rowIndex,
            final TableRowExpectedContent expectedRowContent)
    {
        Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
    }

    private void writeProjectsFile(final ProjectConfig projectConfig)
    {
        final Path projectsFile = this.dataDir.resolve("projects.json");
        try (OutputStream stream = Files.newOutputStream(projectsFile))
        {
            JSONB.toJson(projectConfig, stream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing file " + projectsFile, e);
        }
    }

    protected ProjectConfig projectConfig(final Project... projects)
    {
        final ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setProjects(asList(projects));
        return projectConfig;
    }

    protected static Project project(final String id, final String label)
    {
        final Project project = new Project();
        project.setProjectId(id);
        project.setLabel(label);
        return project;
    }

    protected JsonMonth loadMonth(final LocalDate date)
    {
        final Path file = this.dataDir.resolve(String.valueOf(date.getYear()))
                .resolve(YearMonth.from(date).toString() + ".json");
        try (InputStream stream = Files.newInputStream(file))
        {
            return JSONB.fromJson(stream, JsonMonth.class);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading file " + file, e);
        }
    }
}
