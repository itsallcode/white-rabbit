package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OtherThreadRunnableTest
{
    @Mock
    private ExecutorService executorServiceMock;
    @Mock
    private Runnable delegateMock;

    private OtherThreadRunnable otherThreadRunnable;

    @BeforeEach
    void setUp()
    {
        otherThreadRunnable = new OtherThreadRunnable(executorServiceMock, delegateMock);
    }

    @Test
    void test()
    {
        otherThreadRunnable.run();
        verify(executorServiceMock).execute(same(delegateMock));
    }
}
