#!/usr/bin/env node
import "source-map-support/register";
import { InfrastructureStack, InfrastructureStackProps } from "./lib/infrastructure-stack";
import { CONFIG } from "./config";
import { InfrastructureConfig } from "./lib/config-interface";
import { App } from "aws-cdk-lib";

const config: InfrastructureConfig = CONFIG;

const props: InfrastructureStackProps = {
    env: { region: config.region },
    description: `WhiteRabbit Webstart ${config.domain}`,
    tags: { stack: config.stackName },
    domain: config.domain,
    hostedZoneName: config.hostedZoneName,
    sslCertificateArn: config.sslCertificateArn
};

const app = new App();
const stack = new InfrastructureStack(app, config.stackName, props);
