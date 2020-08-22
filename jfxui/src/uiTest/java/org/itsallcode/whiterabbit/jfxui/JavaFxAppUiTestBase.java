package org.itsallcode.whiterabbit.jfxui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;

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
    private final ZoneId timeZone = ZoneId.of("Europe/Berlin");
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

        when(clockMock.getZone()).thenReturn(timeZone);
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

    protected LocalDate getCurrentDate()
    {
        return LocalDate.ofInstant(now, timeZone);
    }

    protected LocalTime getCurrentTimeMinutes()
    {
        return LocalTime.ofInstant(now, timeZone).truncatedTo(ChronoUnit.MINUTES);
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

    protected TableCell<?, ?> getTableCell(final TableView<?> table, int rowIndex, String columnId)
    {
        final VirtualFlow<?> virtualFlow = table.getChildrenUnmodifiable().stream()
                .filter(VirtualFlow.class::isInstance)
                .map(VirtualFlow.class::cast)
                .findFirst().orElseThrow();
        assertThat(virtualFlow.getCellCount()).isGreaterThan(rowIndex);
        final TableRow<?> row = (TableRow<?>) virtualFlow.getCell(rowIndex);
        return row.getChildrenUnmodifiable().stream()
                .filter(cell -> cell.getId().equals(columnId))
                .map(TableCell.class::cast)
                .findFirst().orElseThrow();
    }

    protected void assertRowContent(final TableView<?> table, int rowIndex, TableRowExpectedContent expectedRowContent)
    {
        Assertions.assertThat(table).containsRowAtIndex(rowIndex, expectedRowContent.expectedCellContent());
    }

    protected JsonMonth loadMonth(LocalDate date)
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        final Path file = dataDir.resolve(String.valueOf(date.getYear()))
                .resolve(YearMonth.from(date).toString() + ".json");
        try (InputStream stream = Files.newInputStream(file))
        {
            return jsonb.fromJson(stream, JsonMonth.class);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading file " + file, e);
        }
    }
}
