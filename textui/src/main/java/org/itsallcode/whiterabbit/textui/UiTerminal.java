package org.itsallcode.whiterabbit.textui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class UiTerminal
{
    private static final int END_OF_FILE = -1;
    private static final int WAIT_TIME_EXPIRED = -2;

    private final Terminal terminal;

    UiTerminal(Terminal terminal)
    {
        this.terminal = terminal;
    }

    public static UiTerminal create()
    {
        return new UiTerminal(createTerminal());
    }

    private static Terminal createTerminal()
    {
        final TerminalBuilder terminalBuilder = TerminalBuilder.builder() //
                .name("WhiteRabbit time recording") //
                .system(true) //
                .encoding(StandardCharsets.UTF_8);
        try
        {
            final Terminal term = terminalBuilder.build();
            term.enterRawMode();
            return term;
        }
        catch (final IOException e)
        {
            throw new AppException("Error creating terminal", e);
        }
    }

    public Optional<Character> getNextCommand()
    {
        final int result = readNonWhitespaceCharFromTerminal();
        switch (result)
        {
        case WAIT_TIME_EXPIRED:
            return Optional.empty();
        case END_OF_FILE:
            return Optional.of(App.COMMAND_QUIT);
        default:
            return Optional.ofNullable((char) result);
        }
    }

    private int readNonWhitespaceCharFromTerminal()
    {
        int c = readCharFromTerminal();
        while (Character.isWhitespace(c))
        {
            c = readCharFromTerminal();
        }
        return c;
    }

    private int readCharFromTerminal()
    {
        try
        {
            return terminal.reader().read();
        }
        catch (final IOException e)
        {
            throw new AppException("Error reading from terminal", e);
        }
    }

    // Using System.out intentionally
    @SuppressWarnings("squid:S106")
    public void println(String message)
    {
        System.out.println(message);
    }
}
