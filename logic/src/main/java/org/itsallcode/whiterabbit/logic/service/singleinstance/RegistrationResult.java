package org.itsallcode.whiterabbit.logic.service.singleinstance;

public interface RegistrationResult extends OtherInstance, AutoCloseable
{
    public boolean isOtherInstanceRunning();

    @Override
    public void sendMessage(String message);

    @Override
    public String sendMessageWithResponse(String message);

    @Override
    public void close();
}
