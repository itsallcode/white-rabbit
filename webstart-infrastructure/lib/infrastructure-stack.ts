import { Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { StaticContentConstruct } from './static-content';

export interface InfrastructureStackProps extends StackProps {
  domain: string;
  hostedZoneName: string;
  sslCertificateArn: string;
}

export class InfrastructureStack extends Stack {
  constructor(scope: Construct, id: string, props: InfrastructureStackProps) {
    super(scope, id, props);

    new StaticContentConstruct(this, 'StaticContent', {
      domain: props.domain,
      hostedZoneName: props.hostedZoneName,
      sslCertificateArn: props.sslCertificateArn
    });
  }
}
