package org.itsallcode.whiterabbit.logic.service.singleinstance;

public class RegistrationResult implements OtherInstance, AutoCloseable
{
    private final Server server;
    private final ClientConnection client;

    private RegistrationResult(Server server, ClientConnection client)
    {
        this.server = server;
        this.client = client;
    }

    public static RegistrationResult of(ClientConnection client)
    {
        return new RegistrationResult(null, client);
    }

    public static RegistrationResult of(Server server)
    {
        return new RegistrationResult(server, null);
    }

    public boolean isOtherInstanceRunning()
    {
        return client != null;
    }

    @Override
    public void sendMessage(String message)
    {
        if (client == null)
        {
            throw new IllegalStateException("Running as server: can't send message");
        }
        client.sendMessage(message);
    }

    @Override
    public String sendMessageWithResponse(String message)
    {
        if (client == null)
        {
            throw new IllegalStateException("Running as server: can't send message");
        }
        return client.sendMessageWithResponse(message);
    }

    @Override
    public void close()
    {
        if (server != null)
        {
            server.close();
        }
        if (client != null)
        {
            client.close();
        }
    }
}
