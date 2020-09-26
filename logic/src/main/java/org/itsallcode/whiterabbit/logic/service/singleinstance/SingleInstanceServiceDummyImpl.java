package org.itsallcode.whiterabbit.logic.service.singleinstance;

class SingleInstanceServiceDummyImpl implements SingleInstanceService
{
    @Override
    public RegistrationResult tryToRegisterInstance(RunningInstanceCallback callback)
    {
        return new DummyRegistrationResult();
    }

    private static class DummyRegistrationResult implements RegistrationResult
    {
        @Override
        public boolean isOtherInstanceRunning()
        {
            return false;
        }

        @Override
        public void sendMessage(String message)
        {
            // ignore
        }

        @Override
        public String sendMessageWithResponse(String message)
        {
            return null;
        }

        @Override
        public void close()
        {
            // ignore
        }
    }
}
