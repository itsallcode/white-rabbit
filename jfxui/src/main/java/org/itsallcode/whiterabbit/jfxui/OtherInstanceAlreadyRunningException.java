package org.itsallcode.whiterabbit.jfxui;

public class OtherInstanceAlreadyRunningException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public OtherInstanceAlreadyRunningException(String message)
    {
        super(message);
    }
}
