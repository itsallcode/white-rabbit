package org.itsallcode.whiterabbit.logic.service;

import java.util.function.Consumer;

import org.itsallcode.whiterabbit.logic.model.DayRecord;

class DayUpdateExecutor implements Runnable
{

    private final AppService appService;
    private final Consumer<DayRecord> listener;

    DayUpdateExecutor(AppService appService, Consumer<DayRecord> listener)
    {
        this.appService = appService;
        this.listener = listener;
    }

    @Override
    public void run()
    {
        final DayRecord day = appService.updateNow();
        listener.accept(day);
    }
}
