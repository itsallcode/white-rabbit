package org.itsallcode.whiterabbit.jfxui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import javafx.application.Platform;

public class JavaFxUtil
{
    public static <T> T runOnFxApplicationThread(Supplier<T> supplier)
    {
        if (Platform.isFxApplicationThread())
        {
            return supplier.get();
        }

        final CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> future.complete(supplier.get()));
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
