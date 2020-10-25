package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class InfrastructureApp {
    public static void main(final String[] args) {
        App app = new App();

        new InfrastructureStack(app, "InfrastructureStack");

        app.synth();
    }
}
