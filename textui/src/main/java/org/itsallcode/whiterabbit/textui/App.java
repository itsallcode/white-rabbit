package org.itsallcode.whiterabbit.textui;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;
import org.itsallcode.whiterabbit.logic.service.SchedulingService.ScheduledTaskFuture;

public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);

	private static final char COMMAND_AUTO_UPDATE = 'a';
	private static final char COMMAND_REPORT = 'r';
	private static final char COMMAND_UPDATE = 'u';
	private static final char COMMAND_TOGGLE_INTERRUPT = 'i';
	static final char COMMAND_QUIT = 'q';

	private final AppService appService;
	private final UiTerminal terminal;
	private final FormatterService formatterService;

	private Interruption interruption;
	private ScheduledTaskFuture autoUpdateFuture;
	private boolean running = true;

	public App(AppService appService, FormatterService formatterService, UiTerminal terminal) {
		this.appService = appService;
		this.formatterService = formatterService;
		this.terminal = terminal;
	}

	public static void main(String[] args) {
		final FormatterService formatterService = new FormatterService(Locale.US);
		final Config config = Config.read(Paths.get("time.properties"));
		final AppService appService = AppService.create(config, formatterService);
		final UiTerminal terminal = UiTerminal.create();
		new App(appService, formatterService, terminal).run();
	}

	private void run() {
		this.toggleAutoUpdate();
		while (running) {
			final Optional<Character> command = promptUser();
			if (!command.isPresent()) {
				continue;
			}
			final char c = command.get().charValue();
			final char commandChar = Character.toLowerCase(c);
			executeCommand(commandChar);
		}
	}

	private void executeCommand(final char command) {
		if (Character.isWhitespace(command)) {
			return;
		}
		switch (command) {
		case COMMAND_UPDATE:
			update();
			break;
		case COMMAND_TOGGLE_INTERRUPT:
			toggleInterrupt();
			break;
		case COMMAND_REPORT:
			appService.report();
			break;
		case COMMAND_AUTO_UPDATE:
			toggleAutoUpdate();
			break;
		case COMMAND_QUIT:
			shutdown();
			break;
		default:
			LOG.error("Unknown command '" + command + "'");
			printPrompt();
			break;
		}
	}

	private void shutdown() {
		this.appService.shutdown();
		this.running = false;
	}

	private void toggleAutoUpdate() {
		if (autoUpdateFuture == null) {
			autoUpdateFuture = appService.startAutoUpdate(day -> {
				LOG.info("Scheduled update: {}", formatterService.format(day));
				printPrompt();
			});
		} else {
			autoUpdateFuture.cancel();
			autoUpdateFuture = null;
		}
	}

	private void toggleInterrupt() {
		if (interruption == null) {
			this.interruption = appService.startInterruption();
		} else {
			this.interruption.end();
			this.interruption = null;
		}
	}

	private void update() {
		final DayRecord updatedRecord = appService.updateNow();
		LOG.info("Day:\n{}", formatterService.format(updatedRecord));
	}

	private Optional<Character> promptUser() {
		printPrompt();
		return terminal.getNextCommand();
	}

	private void printPrompt() {
		System.out.println(getPrompt());
	}

	private String getPrompt() {
		String prompt = MessageFormat.format("Press command key: {0}=update now, {1}=begin/end interruption, {2}=toggle auto-update, {3}=report, {4}=quit",
				COMMAND_UPDATE, COMMAND_TOGGLE_INTERRUPT, COMMAND_AUTO_UPDATE, COMMAND_REPORT, COMMAND_QUIT);
		if (interruption != null) {
			prompt += " (interruption " + formatterService.format(interruption.currentDuration()) + ")";
		}
		if (autoUpdateFuture != null) {
			prompt += " (auto-update on)";
		}
		return prompt;
	}
}
