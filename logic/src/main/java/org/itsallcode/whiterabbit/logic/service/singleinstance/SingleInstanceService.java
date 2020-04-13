package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleInstanceService
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceService.class);

    private static final int PORT = 13373;
    private ServerSocket serverSocket;

    public Optional<OtherInstance> tryToRegisterInstance(RunningInstanceCallback callback)
    {
        try
        {
            serverSocket = createLocalhostSocket(PORT);
            LOG.info("Opened server socket {}", serverSocket);
        }
        catch (final BindException e)
        {
            LOG.error("Another instance is already running", e);
            return Optional.of(new OtherInstance());
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
        return Optional.empty();
    }

    @SuppressWarnings("squid:S4818") // Socket is used safely here
    private ServerSocket createLocalhostSocket(int port) throws IOException
    {
        return new ServerSocket(port, 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
    }

    private void shutdown()
    {
        if (serverSocket == null)
        {
            return;
        }
        try
        {
            serverSocket.close();
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
