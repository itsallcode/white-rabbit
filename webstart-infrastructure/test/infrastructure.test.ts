import { expect as expectCDK, matchTemplate, MatchStyle } from '@aws-cdk/assert';
import * as cdk from '@aws-cdk/core';
import * as Infrastructure from '../lib/infrastructure-stack';

test('Empty Stack', () => {
  const app = new cdk.App();
  const props = {
    domain: "whiterabbit.example.com",
    hostedZoneName: "example.com.",
    sslCertificateArn: "arn:aws:acm:us-east-1:000000000000:certificate/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    stackName: "whiterabbit-webstart"
  };
  const stack = new Infrastructure.InfrastructureStack(app, 'MyTestStack', props);
  expectCDK(stack).to(matchTemplate({
    "Resources": {}
  }, MatchStyle.EXACT))
});
