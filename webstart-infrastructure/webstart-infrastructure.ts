#!/usr/bin/env node
import "source-map-support/register";
import cdk = require("@aws-cdk/core");
import { InfrastructureStack, InfrastructureStackProps } from "./lib/infrastructure-stack";
import { CONFIG } from "./config";
import { InfrastructureConfig } from "./lib/config-interface";

const config: InfrastructureConfig = CONFIG;

const props: InfrastructureStackProps = {
    env: { region: config.region },
    description: `WhiteRabbit Webstart ${config.domain}`,
    tags: { stack: config.stackName },
    domain: config.domain,
    hostedZoneName: config.hostedZoneName,
    sslCertificateArn: config.sslCertificateArn
};

const app = new cdk.App();
const stack = new InfrastructureStack(app, config.stackName, props);
