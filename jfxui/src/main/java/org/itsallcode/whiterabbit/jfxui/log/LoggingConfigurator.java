package org.itsallcode.whiterabbit.jfxui.log;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.itsallcode.whiterabbit.logic.Config;

public class LoggingConfigurator
{
    private static final Logger LOG = LogManager.getLogger(LoggingConfigurator.class);

    private LoggingConfigurator()
    {
        // Not instantiable
    }

    public static void configure(Config config)
    {
        final Path logPath = config.getLogPath();
        LOG.info("Configuring log4j2 with log directory {}", logPath);
        final Appender appender = createAppender(logPath);
        appender.start();
        configureAppender(appender, Level.DEBUG);
        LOG.info("Log4j2 configured with log directory {}", logPath);
    }

    private static void configureAppender(Appender appender, Level logLevel)
    {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.getConfiguration().addAppender(appender);
        ctx.getRootLogger().addAppender(appender);
        ctx.updateLoggers();
    }

    private static Appender createAppender(Path logPath)
    {
        return RollingFileAppender.newBuilder()
                .setName("File")
                .withFileName(logPath.resolve("white-rabbit.log").toString())
                .withFilePattern(logPath.resolve("white-rabbit-%d{yyyy-MM}-%i.log.gz").toString())
                .setLayout(createLayout())
                .withPolicy(SizeBasedTriggeringPolicy.createPolicy("50 MB"))
                .build();
    }

    private static Layout<?> createLayout()
    {
        return PatternLayout.newBuilder()
                .withPattern("%d %p %c{1.} [%t] %-5level %logger{36} - %msg%n")
                .withHeader(
                        "Runtime: ${java:runtime}\nJVM: ${java:vm}\nOS: ${java:os}\nHardware: ${java:hw}\nLocale: ${java:locale}\n")
                .withCharset(StandardCharsets.UTF_8)
                .withAlwaysWriteExceptions(true)
                .build();
    }
}
