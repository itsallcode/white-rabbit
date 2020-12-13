package org.itsallcode.whiterabbit.jfxui.log;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.itsallcode.whiterabbit.logic.Config;

public class LoggingConfigurator
{
    private final Config config;

    public LoggingConfigurator(Config config)
    {
        this.config = config;
    }

    public void configure()
    {
        System.out.println("Configuring log4j2");

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        final PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d %p %c{1.} [%t] %-5level %logger{36} - %msg%n")
                .withHeader(
                        "Runtime: ${java:runtime}\nJVM: ${java:vm}\nOS: ${java:os}\nHardware: ${java:hw}\nLocale: ${java:locale}\n")
                .withCharset(StandardCharsets.UTF_8)
                .withAlwaysWriteExceptions(true)
                .build();

        final Appender appender = RollingFileAppender.newBuilder().setName("File")
                .withFileName("logs/white-rabbit.log")
                .withFilePattern("logs/white-rabbit-%d{yyyy-MM-dd}-%i.log.gz")
                .setLayout(layout).withPolicy(SizeBasedTriggeringPolicy.createPolicy("10 MB")).build();

        appender.start();
        config.addAppender(appender);

        config.getRootLogger().addAppender(appender, Level.DEBUG, null);

        ctx.updateLoggers();
    }
}
