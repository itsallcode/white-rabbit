package org.itsallcode.whiterabbit.textui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SystemOutGuard.class)
class UiTerminalTest
{
    @Mock
    private Terminal terminalMock;
    @Mock
    private NonBlockingReader readerMock;

    private UiTerminal term;

    @BeforeEach
    void setUp()
    {
        term = new UiTerminal(terminalMock);
    }

    @Test
    void testCreateTerminal()
    {
        assertThat(UiTerminal.create()).isNotNull();
    }

    @Test
    void testPrintln(Capturable stream)
    {
        stream.capture();
        term.println("message");
        assertThat(stream.getCapturedData()).isEqualTo("message" + System.lineSeparator());
    }

    @Test
    void testExceptionDuringReadHandled() throws IOException
    {
        when(terminalMock.reader()).thenReturn(readerMock);
        when(readerMock.read()).thenThrow(new IOException("expected"));
        assertThrows(AppException.class, () -> term.getNextCommand());
    }

    @Test
    void testGetNextCommand() throws IOException
    {
        simulateCharacterRead('a');
        assertThat(term.getNextCommand()).isPresent().hasValue('a');
    }

    @Test
    void testWhitespacesSkippedCommand() throws IOException
    {
        simulateCharacterRead(' ', 'a');
        assertThat(term.getNextCommand()).isPresent().hasValue('a');
    }

    @Test
    void testNextCommandEmptyWhenWaitTimeExeeded() throws IOException
    {
        simulateCharacterRead(-2);
        assertThat(term.getNextCommand()).isEmpty();
    }

    @Test
    void testNextCommandQWhenEndOfFile() throws IOException
    {
        simulateCharacterRead(-1);
        assertThat(term.getNextCommand()).isPresent().hasValue('q');
    }

    private void simulateCharacterRead(int... characters) throws IOException
    {
        when(terminalMock.reader()).thenReturn(readerMock);
        OngoingStubbing<Integer> stubbing = when(readerMock.read());
        for (final int character : characters)
        {
            stubbing = stubbing.thenReturn(character);
        }
    }
}
