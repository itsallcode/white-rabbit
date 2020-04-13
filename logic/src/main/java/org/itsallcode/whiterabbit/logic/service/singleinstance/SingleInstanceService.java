package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleInstanceService
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceService.class);

    private static final int DEFAULT_PORT = 13373;

    private final int port;

    private ServerSocketChannel serverSocket;

    private final ExecutorService executorService;

    private Server server;

    public SingleInstanceService()
    {
        this(DEFAULT_PORT, Executors.newFixedThreadPool(1));
    }

    SingleInstanceService(int port, ExecutorService executorService)
    {
        this.port = port;
        this.executorService = executorService;
    }

    public Optional<OtherInstance> tryToRegisterInstance(RunningInstanceCallback callback)
    {
        try
        {
            serverSocket = ServerSocketChannel.open();
            final InetSocketAddress addr = new InetSocketAddress(createLocalhostAddress(), port);
            serverSocket.configureBlocking(false);
            serverSocket.bind(addr);

            final int ops = serverSocket.validOps();
            final Selector selector = Selector.open();
            final SelectionKey selectKey = serverSocket.register(selector, ops, null);
            LOG.debug("Got seleciton key {} for ops {}", selectKey, ops);

            server = new Server(serverSocket, selector, callback);
            executorService.execute(server);

            LOG.info("Opened server socket {}", serverSocket);
        }
        catch (final BindException e)
        {
            LOG.error("Another instance is already running: {}", e.getMessage());
            return Optional.of(connectToOtherInstance());
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
        return Optional.empty();
    }

    private InetAddress createLocalhostAddress()
    {
        try
        {
            return InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
        }
        catch (final UnknownHostException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private OtherInstance connectToOtherInstance()
    {
        final InetSocketAddress addr = new InetSocketAddress(createLocalhostAddress(), port);
        SocketChannel clientSocket;
        try
        {
            LOG.info("Connecting to Server {}", addr);
            clientSocket = SocketChannel.open(addr);
            clientSocket.configureBlocking(false);
            LOG.info("Connecting to Server {}, blocking = {}", addr, clientSocket.isBlocking());
            return new OtherInstance(clientSocket);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error connectiong to " + addr, e);
        }
    }

    private void shutdown()
    {
        executorService.shutdownNow();
        if (server == null)
        {
            return;
        }
        try
        {
            server.close();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when closing socket", e);
        }
    }

    public void close()
    {
        shutdown();
    }
}
