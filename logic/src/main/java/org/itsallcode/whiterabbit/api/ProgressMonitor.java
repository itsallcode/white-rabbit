package org.itsallcode.whiterabbit.api;

public interface ProgressMonitor
{
    boolean isCanceled();

    void beginTask(String name, int totalWork);

    void setTaskName(String name);

    void worked(int work);
}
