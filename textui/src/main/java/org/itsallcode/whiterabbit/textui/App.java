package org.itsallcode.whiterabbit.textui;

import java.nio.file.Paths;

import org.itsallcode.whiterabbit.logic.Config;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.storage.DateToFileMapper;
import org.itsallcode.whiterabbit.logic.storage.Storage;

public class App {

	public static void main(String[] args) {
		final Config config = Config.read(Paths.get("../time.properties"));
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final AppService appService = new AppService(storage, new ClockService());

		appService.update();
		appService.report();
	}
}
