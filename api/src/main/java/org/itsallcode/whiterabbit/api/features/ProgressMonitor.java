package org.itsallcode.whiterabbit.api.features;

/**
 * A progress monitor passed to a {@link PluginFeature} that allows reporting
 * the progress of process.
 */
public interface ProgressMonitor
{
    /**
     * Call this method repeatedly to check, if the user wants to cancel the
     * process. Abort the process if this returns <code>true</code>.
     * 
     * @return <code>true</code> if the user has cancelled the process.
     */
    boolean isCanceled();

    /**
     * Notifies that the main task is beginning.
     * 
     * @param name
     *            the name of the current task shown to the user.
     * @param totalWork
     *            the total number of work units that will be done by the task.
     */
    void beginTask(String name, int totalWork);

    /**
     * @param name
     *            the name of the current task shown to the user.
     */
    void setTaskName(String name);

    /**
     * Notifies that a given number of work unit of the main task has been
     * completed.
     * 
     * @param work
     *            the number of work units just completed.
     */
    void worked(int work);
}
