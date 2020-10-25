import * as cdk from '@aws-cdk/core';
import { StaticContentConstruct } from './static-content';

export interface InfrastructureStackProps extends cdk.StackProps {
  domain: string;
  hostedZoneName: string;
  sslCertificateArn: string;
}

export class InfrastructureStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props: InfrastructureStackProps) {
    super(scope, id, props);

    new StaticContentConstruct(this, 'StaticContent', {
      domain: props.domain,
      hostedZoneName: props.hostedZoneName,
      sslCertificateArn: props.sslCertificateArn
    });
  }
}
