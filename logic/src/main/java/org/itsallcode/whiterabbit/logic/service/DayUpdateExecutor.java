package org.itsallcode.whiterabbit.logic.service;

import java.util.function.Consumer;

import org.itsallcode.whiterabbit.logic.AutoInterruptionStrategy;
import org.itsallcode.whiterabbit.logic.model.DayRecord;

class DayUpdateExecutor implements Runnable {

    private final AppService appService;
    private final Consumer<DayRecord> listener;
    private final AutoInterruptionStrategy autoInterruptionStrategy;

    DayUpdateExecutor(AppService appService, Consumer<DayRecord> listener,
	    AutoInterruptionStrategy autoInterruptionStrategy) {
	this.appService = appService;
	this.listener = listener;
	this.autoInterruptionStrategy = autoInterruptionStrategy;
    }

    @Override
    public void run() {
	final DayRecord day = appService.updateNow(autoInterruptionStrategy);
	listener.accept(day);
    }
}
