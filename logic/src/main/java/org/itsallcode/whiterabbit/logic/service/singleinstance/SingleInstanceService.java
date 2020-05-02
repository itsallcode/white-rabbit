package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
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
            final Optional<ServerSocket> serverSocket = bindServerSocket();
            if (serverSocket.isEmpty())
            {
                final ClientConnection clientConnection = ClientConnection.connect(createLocalhostAddress(), port);
                return RegistrationResult.of(clientConnection);
            }

            final SingleInstanceServer server = new SingleInstanceServer(serverSocket.get(), callback);
            server.start();

            LOG.info("Opened server socket to {}", serverSocket.get().getLocalSocketAddress());
            return RegistrationResult.of(server);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
    }

    private Optional<ServerSocket> bindServerSocket() throws IOException
    {
        final InetAddress address = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
        try
        {
            final ServerSocket serverSocket = new ServerSocket(port, 0, address);
            LOG.info("Opened server socket {}", serverSocket);
            return Optional.of(serverSocket);
        }
        catch (final BindException e)
        {
            LOG.debug("Another instance is already running on address {}, port {}: '{}'", address, port,
                    e.getMessage());
            LOG.trace("Root cause", e);
            return Optional.empty();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
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
}
