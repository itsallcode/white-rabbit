package org.itsallcode.whiterabbit.logic.service.singleinstance;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OtherInstance implements AutoCloseable
{
    private static final Logger LOG = LogManager.getLogger(OtherInstance.class);

    private final SocketChannel clientSocket;

    public OtherInstance(SocketChannel clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void sendMessage(String message)
    {
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        try
        {
            clientSocket.write(buffer);
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
    public void close() throws IOException
    {
        LOG.info("Closing client socket {}", clientSocket);
        clientSocket.close();
    }
}
