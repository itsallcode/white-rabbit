package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ClientConnection implements AutoCloseable
{
    private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

    private final Socket clientSocket;

    private ClientConnection(final Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    // Socket will be closed when client connection is closed.
    @SuppressWarnings("java:S2095")
    static ClientConnection connect(final InetAddress address, final int port)
    {
        try
        {
            LOG.trace("Creating client connection to server {}", address);
            final Socket socket = new Socket(address, port);
            return new ClientConnection(socket);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error connecting to " + address, e);
        }
    }

    public void sendMessage(final String message)
    {
        if (message.contains("\n"))
        {
            throw new IllegalArgumentException("Message '" + message + "' must not contain \\n");
        }
        LOG.trace("Sending message '{}' to {}", message, clientSocket);
        try
        {
            final OutputStream output = clientSocket.getOutputStream();
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.write('\n');
            output.flush();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing to client socket", e);
        }
    }

    public String sendMessageWithResponse(final String message)
    {
        sendMessage(message);
        return readResponse();
    }

    private String readResponse()
    {
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return reader.readLine();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading from socket " + clientSocket, e);
        }
    }

    @Override
    public void close()
    {
        LOG.trace("Closing client socket {}", clientSocket);
        try
        {
            clientSocket.close();
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error closing client socket", e);
        }
    }
}
