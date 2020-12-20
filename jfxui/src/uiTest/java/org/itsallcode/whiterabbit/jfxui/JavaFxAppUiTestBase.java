package org.itsallcode.whiterabbit.jfxui;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.testutil.TableRowExpectedContent;
import org.itsallcode.whiterabbit.jfxui.testutil.TimeUtil;
import org.itsallcode.whiterabbit.jfxui.testutil.model.ApplicationHelper;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.project.Project;
import org.itsallcode.whiterabbit.logic.service.project.ProjectConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;

import com.sun.javafx.application.ParametersImpl;

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
    private Locale locale = Locale.GERMANY;

    private Instant initialTime = Instant.parse("2007-12-03T10:15:30.20Z");
    private List<String> commandLineArgs = emptyList();

    private ApplicationHelper applicationHelper;
    private TimeUtil timeUtil;

    protected void setRobot(FxRobot robot)
    {
        applicationHelper = new ApplicationHelper(Objects.requireNonNull(robot, "robot"));
    }

    protected ApplicationHelper app()
    {
        return Objects.requireNonNull(applicationHelper, "applicationHelper");
    }

    protected TimeUtil time()
    {
        return Objects.requireNonNull(timeUtil, "timeUtil");
    }

    protected void doStart(final Stage stage)
    {
        doStart(stage, null);
    }

    protected void setInitialTime(Instant initialTime)
    {
        this.initialTime = initialTime;
    }

    public void setCommandLineArgs(List<String> commandLineArgs)
    {
        this.commandLineArgs = commandLineArgs;
    }

    protected void doStart(final Stage stage, final ProjectConfig projectConfig)
    {
        LOG.info("Starting application using stage {}", stage);

        timeUtil = TimeUtil.start(initialTime);

        prepareConfiguration(projectConfig);

        this.javaFxApp = new JavaFxApp(() -> this.workingDir, timeUtil.clock(), timeUtil.executorService());

        ParametersImpl.registerParameters(javaFxApp, new ParametersImpl(commandLineArgs));

        this.javaFxApp.init();
        this.javaFxApp.start(stage);

        timeUtil.captureScheduledRunnables();
        LOG.info("Application startup finished");
    }

    protected void doStop()
    {
        LOG.info("Preparing application shutdown");
        this.javaFxApp.prepareShutdown();
        LOG.info("Application shutdown done");
    }

    private void prepareConfiguration(final ProjectConfig projectConfig)
    {
        this.dataDir = this.workingDir.resolve("data");
        String configFileContent = "data = " + this.dataDir.toString().replace('\\', '/') + "\n";
        configFileContent += "locale = " + this.locale.getLanguage() + "\n";
        configFileContent += "allow_multiple_instances = true\n";
        configFileContent += "write_log_file = false\n";
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
