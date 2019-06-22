package org.itsallcode.whiterabbit.logic.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SingleInstanceService
{
    private static final Logger LOG = LogManager.getLogger(SingleInstanceService.class);

    private static final int PORT = 13373;
    private ServerSocket serverSocket;

    public void registerInstance()
    {
        try
        {
            serverSocket = createLocalhostSocket(PORT);
            LOG.info("Opened server socket {}", serverSocket);
        }
        catch (final BindException e)
        {
            LOG.error("Another instance is already running");
            throw new IllegalStateException("Another instance is already running", e);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when creating socket", e);
        }
    }

    @SuppressWarnings("squid:S4818") // Socket is used safely here
    private ServerSocket createLocalhostSocket(int port) throws IOException
    {
        return new ServerSocket(port, 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
    }

    public void shutdown()
    {
        try
        {
            serverSocket.close();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Unexpected exception when closing socket", e);
        }
    }
}
