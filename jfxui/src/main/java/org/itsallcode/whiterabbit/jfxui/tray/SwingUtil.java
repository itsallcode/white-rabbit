package org.itsallcode.whiterabbit.jfxui.tray;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SwingUtil
{
    private static final Logger LOG = LogManager.getLogger(SwingUtil.class);

    private SwingUtil()
    {
        // Not instantiable
    }

    public static <T> T runOnSwingThread(Supplier<T> supplier)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            LOG.debug("Already on Fx thread: run directly");
            return supplier.get();
        }

        LOG.debug("Not running on Swing thread: schedule for execution...");
        final Future<T> future = scheduleOnSwingThread(supplier);
        LOG.debug("Waiting for result from Swing thread...");
        final T result = waitForResult(future);
        LOG.debug("Got result from Swing thread: {}", result);
        return result;
    }

    private static <T> CompletableFuture<T> scheduleOnSwingThread(Supplier<T> supplier)
    {
        final CompletableFuture<T> future = new CompletableFuture<>();
        SwingUtilities.invokeLater(() -> {
            LOG.debug("Running on Fx thread: run now...");
            future.complete(supplier.get());
        });
        return future;
    }

    private static <T> T waitForResult(final Future<T> future)
    {
        try
        {
            return future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error executing: " + e.getMessage(), e);
        }
    }
}
