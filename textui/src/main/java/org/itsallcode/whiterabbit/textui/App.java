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
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.DayFormatter;
import org.itsallcode.whiterabbit.logic.service.Interruption;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);

	private static final char COMMAND_REPORT = 'r';
	private static final char COMMAND_UPDATE = 'u';
	private static final char COMMAND_TOGGLE_INTERRUPT = 'i';
	static final char COMMAND_QUIT = 'q';

	private final AppService appService;
	private final UiTerminal terminal;
	private Interruption interruption;

	private final DayFormatter dayFormatter;

	public App(AppService appService, DayFormatter dayFormatter, UiTerminal terminal) {
		this.appService = appService;
		this.dayFormatter = dayFormatter;
		this.terminal = terminal;
	}

	public static void main(String[] args) {
		final Config config = Config.read(Paths.get("time.properties"));
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final DayFormatter dayFormatter = new DayFormatter(Locale.US);
		final AppService appService = new AppService(storage, dayFormatter, new ClockService());
		final UiTerminal terminal = UiTerminal.create();
		new App(appService, dayFormatter, terminal).run();
	}

	private void run() {
		while (true) {
			final Optional<Character> command = promptUser();
			if (!command.isPresent()) {
				continue;
			}
			final char c = command.get().charValue();
			final char commandChar = Character.toLowerCase(c);
			if (commandChar == COMMAND_QUIT) {
				return;
			}
			if (Character.isWhitespace(commandChar)) {
				continue;
			}
			executeCommand(commandChar);
		}
	}

	private void executeCommand(final char command) {
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
		default:
			System.err.println("Unknown command '" + command + "'");
			break;
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
		final DayRecord updatedRecord = appService.update();
		LOG.info("Day:\n{}", dayFormatter.format(updatedRecord));
	}

	private Optional<Character> promptUser() {
		System.out.println(getPrompt());
		return terminal.getNextCommand();
	}

	private String getPrompt() {
		String prompt = MessageFormat.format("Press command key: {0}=update, {1}=begin/end interruption, {2}=report, {3}=quit", COMMAND_UPDATE,
				COMMAND_TOGGLE_INTERRUPT, COMMAND_REPORT, COMMAND_QUIT);
		if (interruption != null) {
			prompt += " (interruption " + interruption.currentDuration() + ")";
		}
		return prompt;
	}
}
