package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnection implements AutoCloseable
{
    private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

    private final Socket clientSocket;

    public ClientConnection(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void sendMessage(String message)
    {
        if (message.contains("\n"))
        {
            throw new IllegalArgumentException("Message '" + message + "' must not contain \\n");
        }
        LOG.debug("Sending message '{}' to {}", message, clientSocket);
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

    public String sendMessageWithResponse(String message)
    {
        sendMessage(message);
        return readResponse();
    }

    private String readResponse()
    {
        try
        {
            LOG.debug("Reading response...");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            final String response = reader.readLine();
            LOG.debug("Read response '{}'", response);
            return response;
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading from socket " + clientSocket, e);
        }
    }

    @Override
    public void close()
    {
        LOG.info("Closing client socket {}", clientSocket);
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
