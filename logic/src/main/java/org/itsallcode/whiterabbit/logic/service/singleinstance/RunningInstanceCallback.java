package org.itsallcode.whiterabbit.logic.service.singleinstance;

@FunctionalInterface
public interface RunningInstanceCallback
{
    public interface ClientConnection
    {
        void sendMessage(String message);
    }

    void messageReceived(String message, ClientConnection client);
}
