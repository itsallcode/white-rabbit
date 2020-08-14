package org.itsallcode.whiterabbit.jfxui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.itsallcode.whiterabbit.jfxui.table.days.DayRecordPropertyAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class JavaFxAppUiTest
{
    private JavaFxApp javaFxApp;

    @TempDir
    Path workingDir;

    Path dataDir;
    Clock clockMock;
    ScheduledExecutorService executorServiceMock;

    private ScheduledExecutorService realExecutor;

    @Start
    void start(Stage stage) throws IOException
    {
        clockMock = mock(Clock.class);
        executorServiceMock = mock(ScheduledExecutorService.class);
        final ScheduledFuture scheduledFutureMock = mock(ScheduledFuture.class);

        javaFxApp = new JavaFxApp(() -> workingDir, clockMock, executorServiceMock);

        when(clockMock.getZone()).thenReturn(ZoneId.of("Europe/Berlin"));
        // when(clockMock.instant()).thenReturn(Instant.parse("2007-12-03T10:15:30.00Z"));
        when(clockMock.instant()).thenAnswer(invocation -> Clock.systemDefaultZone().instant());

        realExecutor = new ScheduledThreadPoolExecutor(1);
        when(executorServiceMock.schedule(any(Runnable.class), any(Long.class), eq(TimeUnit.MILLISECONDS)))
                .thenAnswer(invocation -> {
                    final Runnable runnable = invocation.getArgument(0,
                            Runnable.class);
                    final Long delay = invocation.getArgument(1, Long.class);
                    final TimeUnit timeUnit = invocation.getArgument(2,
                            TimeUnit.class);
                    System.out.println("Schedule " + runnable + " with delay " + delay + " " + timeUnit);
                    return realExecutor.schedule(runnable, delay, timeUnit);
                });
        prepareConfiguration();

        javaFxApp.init();
        javaFxApp.start(stage);
    }

    @Stop
    void stop()
    {
        javaFxApp.stop();
        realExecutor.shutdown();
    }

    private void prepareConfiguration() throws IOException
    {
        dataDir = workingDir.resolve("data");
        Files.createDirectories(dataDir);
        String configFileContent = "data = " + dataDir.toString().replace('\\', '/') + "\n";
        configFileContent += "locale = de\n";
        Files.write(workingDir.resolve("time.properties"), configFileContent.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void startup(FxRobot robot) throws InterruptedException
    {
        final TableView<DayRecordPropertyAdapter> dayTable = robot.lookup("#day-table").queryTableView();
        Assertions.assertThat(dayTable).hasExactlyNumRows(31);
        Thread.sleep(5000);
    }
}
