package org.itsallcode.whiterabbit.textui;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.Interruption;
import org.itsallcode.whiterabbit.logic.service.singleinstance.OtherInstance;
import org.itsallcode.whiterabbit.logic.service.singleinstance.RunningInstanceCallback.ClientConnection;

public class App
{
    private static final Logger LOG = LogManager.getLogger(App.class);

    private static final char COMMAND_REPORT = 'r';
    private static final char COMMAND_UPDATE = 'u';
    private static final char COMMAND_TOGGLE_INTERRUPT = 'i';
    static final char COMMAND_QUIT = 'q';

    private final AppService appService;
    private final UiTerminal terminal;

    private Interruption interruption;
    private boolean running = true;

    public App(AppService appService, UiTerminal terminal)
    {
        this.appService = appService;
        this.terminal = terminal;
    }

    public static void main(String[] args)
    {
        final Config config = Config.read(Paths.get("time.properties"));
        final AppService appService = AppService.create(config);
        final UiTerminal terminal = UiTerminal.create();
        new App(appService, terminal).run();
    }

    void run()
    {
        final Optional<OtherInstance> otherInstance = appService.registerSingleInstance(this::messageReceived);
        if (otherInstance.isPresent())
        {
            otherInstance.get().sendMessage("bringToFront");
            throw new IllegalStateException("Another instance is already running");
        }
        appService.setUpdateListener(AppServiceCallback.createOnlyUpdate(this::dayRecordUpdated));
        appService.start();

        while (running)
        {
            final Optional<Character> command = promptUser();
            if (command.isEmpty())
            {
                continue;
            }
            final char c = command.get();
            final char commandChar = Character.toLowerCase(c);
            executeCommand(commandChar);
        }
    }

    private void messageReceived(String message, ClientConnection client)
    {
        LOG.debug("Received message '{}': ignore", message);
    }

    private void executeCommand(final char command)
    {
        if (Character.isWhitespace(command))
        {
            return;
        }
        switch (command)
        {
        case COMMAND_UPDATE:
            update();
            break;
        case COMMAND_TOGGLE_INTERRUPT:
            toggleInterrupt();
            break;
        case COMMAND_REPORT:
            appService.report();
            break;
        case COMMAND_QUIT:
            shutdown();
            break;
        default:
            LOG.error("Unknown command '{}'", command);
            printPrompt();
            break;
        }
    }

    private void shutdown()
    {
        this.appService.close();
        this.running = false;
    }

    private void dayRecordUpdated(DayRecord day)
    {
        final Instant now = appService.getClock().instant();
        final Instant displayTime = now.truncatedTo(ChronoUnit.SECONDS);
        final String message = "Update: " + appService.formatter().format(day);
        LOG.trace(message);
        println(displayTime + " " + message);
    }

    private void toggleInterrupt()
    {
        if (interruption == null)
        {
            this.interruption = appService.startInterruption();
        }
        else
        {
            this.interruption.end();
            this.interruption = null;
        }
    }

    private void update()
    {
        appService.updateNow();
        final String message = "Day updated";
        println(message);
        LOG.trace(message);
    }

    private Optional<Character> promptUser()
    {
        printPrompt();
        return terminal.getNextCommand();
    }

    private void printPrompt()
    {
        println(getPrompt());
    }

    private void println(String message)
    {
        terminal.println(message);
    }

    private String getPrompt()
    {
        String prompt = MessageFormat.format(
                "Press command key: {0}=update now, {1}=begin/end interruption, {2}=report, {3}=quit", COMMAND_UPDATE,
                COMMAND_TOGGLE_INTERRUPT, COMMAND_REPORT, COMMAND_QUIT);
        if (interruption != null)
        {
            final String currentInterruption = appService.formatter()
                    .format(interruption.currentDuration(appService.getClock().instant()));
            prompt += " (interruption " + currentInterruption + ")";
        }
        return prompt;
    }
}
