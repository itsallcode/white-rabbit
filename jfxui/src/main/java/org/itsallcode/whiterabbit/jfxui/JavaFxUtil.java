package org.itsallcode.whiterabbit.jfxui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javafx.application.Platform;

public class JavaFxUtil
{
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
            return supplier.get();
        }

        final Future<T> future = scheduleOnFxThread(supplier);
        return waitForResult(future);
    }

    private static <T> CompletableFuture<T> scheduleOnFxThread(Supplier<T> supplier)
    {
        final CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> future.complete(supplier.get()));
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
            throw new IllegalStateException(
                    "Error executing: " + e.getClass() + ": " + e.getMessage(), e);
        }
    }
}
