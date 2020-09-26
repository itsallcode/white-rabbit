package org.itsallcode.whiterabbit.logic.service.singleinstance;

class RegistrationResultImpl implements RegistrationResult
{
    private final SingleInstanceServer server;
    private final ClientConnection client;

    private RegistrationResultImpl(SingleInstanceServer server, ClientConnection client)
    {
        this.server = server;
        this.client = client;
    }

    public static RegistrationResultImpl of(ClientConnection client)
    {
        return new RegistrationResultImpl(null, client);
    }

    public static RegistrationResultImpl of(SingleInstanceServer server)
    {
        return new RegistrationResultImpl(server, null);
    }

    @Override
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
