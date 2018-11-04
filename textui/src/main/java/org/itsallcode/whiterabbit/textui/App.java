package org.itsallcode.whiterabbit.textui;

import java.nio.file.Paths;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class App {

	private static final char COMMAND_REPORT = 'r';
	private static final char COMMAND_UPDATE = 'u';
	static final char COMMAND_QUIT = 'q';

	private final AppService appService;
	private final UiTerminal terminal;

	public App(AppService appService, UiTerminal terminal) {
		this.appService = appService;
		this.terminal = terminal;
	}

	public static void main(String[] args) {
		final Config config = Config.read(Paths.get("time.properties"));
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final AppService appService = new AppService(storage, new ClockService());
		final UiTerminal terminal = UiTerminal.create();
		new App(appService, terminal).run();
	}

	private void run() {
		while (true) {
			final Optional<Character> command = promptUser();
			if (!command.isPresent()) {
				continue;
			}
			final char commandChar = command.get().charValue();
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
			appService.update();
			break;
		case COMMAND_REPORT:
			appService.report();
			break;
		default:
			System.err.println("Unknown command '" + command + "'");
			break;
		}
	}

	private Optional<Character> promptUser() {
		System.out.println("Press key. u = update, r = report, q = quit");
		return terminal.getNextCommand();
	}
}
