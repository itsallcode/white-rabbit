package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Server
{
    private static final Logger LOG = LogManager.getLogger(Server.class);

    private final ServerSocketChannel serverSocket;
    private final RunningInstanceCallback callback;
    private final ExecutorService executorService;

    public Server(ServerSocketChannel serverSocket, RunningInstanceCallback callback)
    {
        this(Executors.newSingleThreadExecutor(), serverSocket, callback);
    }

    public Server(ExecutorService executorService, ServerSocketChannel serverSocket, RunningInstanceCallback callback)
    {
        this.executorService = executorService;
        this.serverSocket = serverSocket;
        this.callback = Objects.requireNonNull(callback);
    }

    public void start()
    {
        executorService.execute(this::run);
    }

    private void run()
    {
        LOG.info("Starting server thread");
        try
        {
            doRun();
        }
        catch (final Exception e)
        {
            LOG.error("Error running server: {}", e.getMessage(), e);
        }
    }

    private void doRun() throws IOException
    {
        final int ops = serverSocket.validOps();
        final Selector selector = Selector.open();
        final SelectionKey selectKey = serverSocket.register(selector, ops, null);
        LOG.info("Got selection key {} for ops {}", selectKey, ops);

        while (selector.isOpen() && serverSocket.isOpen())
        {
            selector.select();
            final Set<SelectionKey> keys = selector.selectedKeys();
            final Iterator<SelectionKey> selectionKeys = keys.iterator();

            while (selectionKeys.hasNext())
            {
                final SelectionKey key = selectionKeys.next();

                if (key.isAcceptable())
                {
                    LOG.info("Got acceptable selection key {}", key);
                    final SocketChannel client = serverSocket.accept();

                    client.configureBlocking(false);

                    client.register(selector, SelectionKey.OP_READ);
                    LOG.info("Connection Accepted from client {}", client.getLocalAddress());
                }
                else if (key.isReadable())
                {
                    LOG.info("Got readable key {}", key);

                    final SocketChannel client = (SocketChannel) key.channel();
                    final ByteBuffer buffer = ByteBuffer.allocate(256);
                    final int readBytes = client.read(buffer);
                    if (readBytes < 0)
                    {
                        LOG.info("Client connection closed, read bytes = {}. close client {}", readBytes, client);
                        client.close();
                    }
                    else
                    {
                        final String message = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                        LOG.info("Message received: '{}'", message);
                        callback.messageReceived(message);
                    }
                }
                else
                {
                    LOG.info("Got invalid key {}", key);
                }
                selectionKeys.remove();
            }
        }
    }

    public void close()
    {
        LOG.info("Shutting down server");
        executorService.shutdownNow();
        try
        {
            serverSocket.close();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error closing server socket", e);
        }
        LOG.info("Shutdown complete");
    }
}
