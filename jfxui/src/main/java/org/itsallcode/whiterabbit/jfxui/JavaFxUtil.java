package org.itsallcode.whiterabbit.jfxui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;

public class JavaFxUtil
{
    private static final Logger LOG = LogManager.getLogger(JavaFxUtil.class);

    private JavaFxUtil()
    {
        // Not instantiable
    }

    public static void runOnFxApplicationThread(Runnable runnable)
    {
        runOnFxApplicationThread(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T runOnFxApplicationThread(Supplier<T> supplier)
    {
        if (Platform.isFxApplicationThread())
        {
            LOG.trace("Already on Fx thread: run directly");
            return supplier.get();
        }

        LOG.trace("Not running on Fx thread: schedule for execution...");
        final Future<T> future = scheduleOnFxThread(supplier);
        LOG.trace("Waiting for result from Fx thread...");
        final T result = waitForResult(future);
        LOG.trace("Got result from Fx thread: {}", result);
        return result;
    }

    private static <T> CompletableFuture<T> scheduleOnFxThread(Supplier<T> supplier)
    {
        final CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            LOG.trace("Running on Fx thread: run now...");
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
