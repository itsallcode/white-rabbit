package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements Runnable
{
    public static final String EXIT_COMMAND = "exit";

    private static final Logger LOG = LogManager.getLogger(Server.class);

    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    private final RunningInstanceCallback callback;

    public Server(ServerSocketChannel serverSocket, Selector selector, RunningInstanceCallback callback)
    {
        this.serverSocket = serverSocket;
        this.selector = selector;
        this.callback = callback;
    }

    @Override
    public void run()
    {
        try
        {
            doRun();
        }
        catch (final IOException e)
        {
            LOG.error("Error running server: {}", e.getMessage(), e);
        }
    }

    private void doRun() throws IOException
    {
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

    public void close() throws IOException
    {
        LOG.info("Shutting down server");
        selector.close();
        serverSocket.close();
    }
}
