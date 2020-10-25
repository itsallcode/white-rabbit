package org.itsallcode.whiterabbit.infrastructure;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.Bucket;

public class InfrastructureStack extends Stack
{
    public InfrastructureStack(final Construct scope, final String id)
    {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props)
    {
        super(scope, id, props);

        Bucket.Builder.create(this, "MyFirstBucket").build();
    }
}
