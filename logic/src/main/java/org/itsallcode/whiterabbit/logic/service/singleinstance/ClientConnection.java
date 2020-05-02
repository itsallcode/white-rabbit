package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientConnection implements AutoCloseable
{
    private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

    private final SocketChannel clientSocket;

    public ClientConnection(SocketChannel clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public String sendMessageWithResponse(String message)
    {
        sendMessage(message);
        return readResponse();
    }

    private String readResponse()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(250);
        try
        {
            final int read = clientSocket.read(buffer);
            LOG.debug("Read {} bytes: {}", read, buffer);
            if (read <= 0)
            {
                return null;
            }

            final String response = new String(buffer.array(), StandardCharsets.UTF_8).trim();
            LOG.debug("Got response '{}'", response);
            return response;
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error reading from socket " + clientSocket, e);
        }
    }

    public void sendMessage(String message)
    {
        LOG.debug("Sending message '{}' to {}", message, clientSocket);
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        try
        {
            clientSocket.configureBlocking(true);
            final int bytes = clientSocket.write(buffer);
            LOG.debug("Sent {} bytes for message '{}'", bytes, message);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing to client socket", e);
        }
        finally
        {
            buffer.clear();
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
