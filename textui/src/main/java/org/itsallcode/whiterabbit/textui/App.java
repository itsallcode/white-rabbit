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
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;
import org.itsallcode.whiterabbit.logic.service.SchedulingService;
import org.itsallcode.whiterabbit.logic.service.SchedulingService.ScheduledTaskFuture;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);

	private static final char COMMAND_AUTO_UPDATE = 'a';
	private static final char COMMAND_REPORT = 'r';
	private static final char COMMAND_UPDATE = 'u';
	private static final char COMMAND_TOGGLE_INTERRUPT = 'i';
	static final char COMMAND_QUIT = 'q';

	private final AppService appService;
	private final UiTerminal terminal;
	private Interruption interruption;

	private final FormatterService formatterService;

	private ScheduledTaskFuture autoUpdateFuture;

	public App(AppService appService, FormatterService formatterService, UiTerminal terminal) {
		this.appService = appService;
		this.formatterService = formatterService;
		this.terminal = terminal;
	}

	public static void main(String[] args) {
		final Config config = Config.read(Paths.get("time.properties"));
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final FormatterService formatterService = new FormatterService(Locale.US);
		final ClockService clockService = new ClockService();
		final SchedulingService schedulingService = new SchedulingService(clockService);
		final AppService appService = new AppService(storage, formatterService, clockService, schedulingService);
		final UiTerminal terminal = UiTerminal.create();
		new App(appService, formatterService, terminal).run();
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
		case COMMAND_AUTO_UPDATE:
			toggleAutoUpdate();
			break;
		default:
			LOG.error("Unknown command '" + command + "'");
			break;
		}
	}

	private void toggleAutoUpdate() {
		if (autoUpdateFuture == null) {
			autoUpdateFuture = appService.startAutoUpdate(day -> LOG.info("Scheduled update: {}", formatterService.format(day)));
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
		System.out.println(getPrompt());
		return terminal.getNextCommand();
	}

	private String getPrompt() {
		String prompt = MessageFormat.format("Press command key: {0}=update now, {1}=begin/end interruption, {2}=toggle auto-update, {3}=report, {4}=quit",
				COMMAND_UPDATE, COMMAND_TOGGLE_INTERRUPT, COMMAND_AUTO_UPDATE, COMMAND_REPORT, COMMAND_QUIT);
		if (interruption != null) {
			prompt += " (interruption " + interruption.currentDuration() + ")";
		}
		if (autoUpdateFuture != null) {
			prompt += " (auto-update on)";
		}
		return prompt;
	}
}
