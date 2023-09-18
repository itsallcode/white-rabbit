package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.util.concurrent.ExecutorService;

class OtherThreadRunnable implements Runnable
{
    private final ExecutorService executorService;
    private final Runnable delegate;

    OtherThreadRunnable(ExecutorService executorService, Runnable delegate)
    {
        this.executorService = executorService;
        this.delegate = delegate;
    }

    @Override
    public void run()
    {
        executorService.execute(delegate);
    }

    @Override
    public String toString()
    {
        return "OtherThreadRunnable [executorService=" + executorService + ", delegate=" + delegate + "]";
    }
}
