package org.itsallcode.whiterabbit.infrastructure;

import software.amazon.awscdk.core.App;

public class InfrastructureApp
{
    public static void main(final String[] args)
    {
        final App app = new App();

        new InfrastructureStack(app, "InfrastructureStack");

        app.synth();
    }
}
