package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SingleInstanceServer
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceServer.class);

    private final ServerSocket serverSocket;
    private final RunningInstanceCallback callback;
    private final ExecutorService executorService;
    private final CountDownLatch serverRunning = new CountDownLatch(1);
    private final CountDownLatch serverStopped = new CountDownLatch(1);

    public SingleInstanceServer(ServerSocket serverSocket, RunningInstanceCallback callback)
    {
        this(Executors.newCachedThreadPool(), serverSocket, callback);
    }

    private SingleInstanceServer(ExecutorService executorService, ServerSocket serverSocket,
            RunningInstanceCallback callback)
    {
        this.executorService = executorService;
        this.serverSocket = serverSocket;
        this.callback = Objects.requireNonNull(callback);
    }

    public void start()
    {
        LOG.info("Executing server thread using {}", executorService);
        executorService.execute(this::run);
        waitUntilServerRunning();
        LOG.info("Server thread is running");
    }

    private void waitUntilServerRunning()
    {
        try
        {
            serverRunning.await();
        }
        catch (final InterruptedException ignored)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void run()
    {
        LOG.info("Starting server thread");
        try
        {
            serverRunning.countDown();
            doRun();
        }
        finally
        {
            LOG.debug("Server loop finished");
            serverStopped.countDown();
        }
    }

    private void doRun()
    {
        while (serverSocket.isBound() && !serverSocket.isClosed())
        {
            try (final Socket socket = serverSocket.accept())
            {
                LOG.debug("Client connected on {}: reading from socket", socket);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message = null;
                while ((message = reader.readLine()) != null)
                {
                    LOG.debug("Got message '{}' from client socket {}", message, socket);
                    callback.messageReceived(message, new Client(socket));
                }
            }
            catch (final SocketException e)
            {
                if (serverSocket.isClosed())
                {
                    LOG.debug("Accept failed during shutdown: ignore.");
                }
                else
                {
                    LOG.error("Error in server loop: continue", e);
                }
            }
            catch (final Exception e)
            {
                LOG.error("Error in server loop: continue", e);
            }
        }
    }

    private static class Client implements RunningInstanceCallback.ClientConnection
    {
        private final Socket socket;

        public Client(Socket client)
        {
            this.socket = client;
        }

        @Override
        public void sendMessage(String message)
        {
            if (message.contains("\n"))
            {
                throw new IllegalArgumentException("Message '" + message + "' must not contain \\n");
            }

            try
            {
                LOG.debug("Sending message '{}' to client {}", message, socket);
                final OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
                outputStream.write('\n');
                outputStream.flush();
            }
            catch (final IOException e)
            {
                throw new UncheckedIOException("Error sending message " + message + " to client " + socket, e);
            }
        }
    }

    public void close()
    {
        LOG.info("Shutting down server for socket {}", serverSocket);
        executorService.shutdownNow();
        try
        {
            serverSocket.close();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error closing server socket", e);
        }
        try
        {
            serverStopped.await();
        }
        catch (final InterruptedException ignore)
        {
            Thread.currentThread().interrupt();
        }
        LOG.info("Shutdown complete");
    }
}
