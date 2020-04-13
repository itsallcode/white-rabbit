package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleInstanceService
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceService.class);
    private static final int DEFAULT_PORT = 13373;

    private final int port;

    public SingleInstanceService()
    {
        this(DEFAULT_PORT);
    }

    SingleInstanceService(int port)
    {
        this.port = port;
    }

    public RegistrationResult tryToRegisterInstance(RunningInstanceCallback callback)
    {
        try
        {
            final Optional<ServerSocketChannel> serverSocketOptional = bindServerSocket();
            if (serverSocketOptional.isEmpty())
            {
                return RegistrationResult.of(connectToOtherInstance());
            }

            final ServerSocketChannel serverSocket = serverSocketOptional.get();
            final Server server = new Server(serverSocket, callback);
            server.start();

            LOG.info("Opened server socket to {}", serverSocket.getLocalAddress());
            return RegistrationResult.of(server);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
    }

    private Optional<ServerSocketChannel> bindServerSocket() throws IOException
    {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        final InetSocketAddress addr = new InetSocketAddress(createLocalhostAddress(), port);
        LOG.debug("Trying to open server socket at {}", addr);
        try
        {
            serverSocket.bind(addr);
        }
        catch (final BindException e)
        {
            LOG.error("Another instance is already running on port {}: {}", port, e.getMessage());
            return Optional.empty();
        }
        return Optional.of(serverSocket);
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

    private ClientConnection connectToOtherInstance()
    {
        final InetSocketAddress addr = new InetSocketAddress(createLocalhostAddress(), port);
        SocketChannel clientSocket;
        try
        {
            LOG.info("Connecting to server {}", addr);
            clientSocket = SocketChannel.open(addr);
            clientSocket.configureBlocking(false);
            return new ClientConnection(clientSocket);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error connectiong to " + addr, e);
        }
    }
}
