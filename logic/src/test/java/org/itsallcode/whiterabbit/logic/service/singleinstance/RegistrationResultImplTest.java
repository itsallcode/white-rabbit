package org.itsallcode.whiterabbit.logic.service.singleinstance;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RegistrationResultImplTest
{
    @Test
    void sendingMessageWithResponseFailsWhenClientIsNull()
    {
        final RegistrationResultImpl result = RegistrationResultImpl.of((ClientConnection) null);
        assertThatThrownBy(() -> result.sendMessageWithResponse("msg"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Running as server: can't send message");
    }

    @Test
    void sendingMessageFailsWhenClientIsNull()
    {
        final RegistrationResultImpl result = RegistrationResultImpl.of((ClientConnection) null);
        assertThatThrownBy(() -> result.sendMessage("msg"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Running as server: can't send message");
    }
}
