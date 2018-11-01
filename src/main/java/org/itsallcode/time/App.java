package org.itsallcode.time;

import java.nio.file.Paths;

import org.itsallcode.time.service.AppService;
import org.itsallcode.time.service.ClockService;
import org.itsallcode.time.storage.DateToFileMapper;
import org.itsallcode.time.storage.Storage;

public class App {

	public static void main(String[] args) {
		final Config config = Config.read(Paths.get("time.properties"));
		final Storage storage = new Storage(new DateToFileMapper(config.getDataDir()));
		final AppService appService = new AppService(storage, new ClockService());

		appService.update();
		appService.report();
	}
}
