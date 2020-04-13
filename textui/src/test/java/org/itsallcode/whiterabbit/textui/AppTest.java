package org.itsallcode.whiterabbit.textui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.DayRecord;
import org.itsallcode.whiterabbit.logic.service.AppService;
import org.itsallcode.whiterabbit.logic.service.AppServiceCallback;
import org.itsallcode.whiterabbit.logic.service.ClockService;
import org.itsallcode.whiterabbit.logic.service.FormatterService;
import org.itsallcode.whiterabbit.logic.service.Interruption;
import org.itsallcode.whiterabbit.logic.service.scheduling.ScheduledTaskFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
class AppTest
{
    private static final String FORMATTED_DAY_RECORD = "Formatted day record";

    private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");

    @Mock
    private AppService appServiceMock;
    @Mock
    private FormatterService formatterServiceMock;
    @Mock
    private UiTerminal terminalMock;
    @Mock
    private Interruption interruptionMock;
    @Mock
    private ScheduledTaskFuture autoUpdateFutureMock;
    @Mock
    private ClockService clockMock;
    @Mock
    private DayRecord dayRecordMock;

    @Captor
    private ArgumentCaptor<AppServiceCallback> appServiceCallbackArg;

    private App app;

    @BeforeEach
    void setUp()
    {
        app = new App(appServiceMock, formatterServiceMock, terminalMock);
        lenient().when(appServiceMock.getClock()).thenReturn(clockMock);
    }

    @Test
    void testRunMainThrowsIoException()
    {
        final UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> App.main(new String[0]));
        assertThat(exception).hasMessage("Error reading time.properties");
    }

    @Test
    void testWhitespaceIgnored()
    {
        runCommand(' ', 'q');
        verifyTerminalPrintsPrompt(2);
    }

    @Test
    void testMissingCommandIgnored()
    {
        runCommand(null, 'q');
        verifyTerminalPrintsPrompt(2);
    }

    @Test
    void testRunTogglesAutoUpdate()
    {
        runCommand('q');
        verify(appServiceMock).start();
    }

    @Test
    void testAutoUpdatePrintsUpdate()
    {
        when(appServiceMock.getClock()).thenReturn(clockMock);
        when(clockMock.instant()).thenReturn(NOW);
        when(formatterServiceMock.format(same(dayRecordMock))).thenReturn(FORMATTED_DAY_RECORD);

        runAutoUpdateListener();
        verifyTerminalPrintsPrompt(1);
        verify(terminalMock).println(NOW + " Update: " + FORMATTED_DAY_RECORD);
    }

    private void runAutoUpdateListener()
    {
        runCommand('q');
        verify(appServiceMock).setUpdateListener(appServiceCallbackArg.capture());
        appServiceCallbackArg.getValue().recordUpdated(dayRecordMock);
    }

    @Test
    void testRunPrintsPrompt()
    {
        runCommand('q');
        verifyTerminalPrintsPrompt(1);
    }

    @Test
    void testQuitShutsdownAppService()
    {
        runCommand('q');
        verify(appServiceMock).close();
    }

    @Test
    void testRunUpdateCommand()
    {
        runCommand('u', 'q');
        verify(appServiceMock).updateNow();
    }

    @Test
    void testCaseIgnoredUpdateCommand()
    {
        runCommand('U', 'q');
        verify(appServiceMock).updateNow();
    }

    @Test
    void testToggleInterruptStartsInterrupt()
    {
        runCommand('i', 'q');
        verify(appServiceMock).startInterruption();
    }

    @Test
    void testToggleInterruptSecondTimeStopsInterrupt()
    {
        when(appServiceMock.startInterruption()).thenReturn(interruptionMock);
        runCommand('i', 'i', 'q');
        verify(interruptionMock).end();
    }

    @Test
    void testReport()
    {
        runCommand('r', 'q');
        verify(appServiceMock).report();
    }

    @Test
    void testUnknownCommand()
    {
        runCommand('z', 'q');
        verifyTerminalPrintsPrompt(3);
    }

    private void verifyTerminalPrintsPrompt(int count)
    {
        verify(terminalMock, times(count))
                .println("Press command key: u=update now, i=begin/end interruption, r=report, q=quit");
    }

    private void runCommand(Character... commands)
    {
        simulateCommands(commands);
        app.run();
    }

    private void simulateCommands(Character... commands)
    {
        OngoingStubbing<Optional<Character>> stubbing = when(terminalMock.getNextCommand());
        for (final Character command : commands)
        {
            stubbing = stubbing.thenReturn(Optional.ofNullable(command));
        }
    }
}
