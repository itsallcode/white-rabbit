package org.itsallcode.whiterabbit.logic.service.singleinstance;

import org.itsallcode.whiterabbit.logic.Config;

public interface SingleInstanceService
{
    public static SingleInstanceService create(Config config)
    {
        return config.allowMultipleInstances()
                ? new SingleInstanceServiceDummyImpl()
                : new SingleInstanceServiceImpl();
    }

    public RegistrationResult tryToRegisterInstance(RunningInstanceCallback callback);
}
