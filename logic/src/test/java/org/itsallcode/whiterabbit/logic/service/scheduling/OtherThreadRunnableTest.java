package org.itsallcode.whiterabbit.logic.service.scheduling;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jparams.verifier.tostring.ToStringVerifier;

@ExtendWith(MockitoExtension.class)
class OtherThreadRunnableTest
{
    @Mock
    ExecutorService executorServiceMock;
    @Mock
    Runnable delegateMock;

    private OtherThreadRunnable otherThreadRunnable;

    @BeforeEach
    void setUp()
    {
        otherThreadRunnable = new OtherThreadRunnable(executorServiceMock, delegateMock);
    }

    @Test
    void run()
    {
        otherThreadRunnable.run();
        verify(executorServiceMock).execute(same(delegateMock));
    }

    @Test
    void testToString()
    {
        ToStringVerifier.forClass(OtherThreadRunnable.class).verify();
    }
}
