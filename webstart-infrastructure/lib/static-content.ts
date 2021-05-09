import { CfnOutput, RemovalPolicy } from 'aws-cdk-lib';
import { Certificate } from 'aws-cdk-lib/lib/aws-certificatemanager';
import { CloudFrontWebDistribution, HttpVersion, OriginAccessIdentity, PriceClass, SecurityPolicyProtocol, SSLMethod, ViewerCertificate, ViewerProtocolPolicy } from 'aws-cdk-lib/lib/aws-cloudfront';
import { Effect, PolicyStatement } from 'aws-cdk-lib/lib/aws-iam';
import { CfnRecordSetGroup } from 'aws-cdk-lib/lib/aws-route53';
import { BlockPublicAccess, Bucket, BucketPolicy } from 'aws-cdk-lib/lib/aws-s3';
import { Construct } from 'constructs';

interface StaticContentProps {
  domain: string;
  hostedZoneName: string;
  sslCertificateArn: string;
}

const CLOUDFRONT_HOSTED_ZONE_ID = "Z2FDTNDATAQYW2";

export class StaticContentConstruct extends Construct {
  constructor(scope: Construct, id: string, props: StaticContentProps) {
    super(scope, id);

    const staticContentBucket = new Bucket(this, "Bucket", {
      removalPolicy: RemovalPolicy.DESTROY,
      blockPublicAccess: BlockPublicAccess.BLOCK_ALL
    });

    const certificate = Certificate.fromCertificateArn(this, "Certificate", props.sslCertificateArn);

    const accessIdentity = new OriginAccessIdentity(this, "AccessIdentity", {
      comment: `Access bucket ${staticContentBucket}`
    });

    const cloudfrontDistribution = new CloudFrontWebDistribution(this, "CloudFrontDistribution", {
      comment: props.domain,
      originConfigs: [{
        behaviors: [{ isDefaultBehavior: true }],
        s3OriginSource: {
          s3BucketSource: staticContentBucket,
          originAccessIdentity: accessIdentity
        }
      }],
      defaultRootObject: "index.html",
      enableIpV6: true,
      viewerCertificate: ViewerCertificate.fromAcmCertificate(certificate, {
        securityPolicy: SecurityPolicyProtocol.TLS_V1_2_2019,
        sslMethod: SSLMethod.SNI,
        aliases: [props.domain]
      }),
      priceClass: PriceClass.PRICE_CLASS_100,
      viewerProtocolPolicy: ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
      httpVersion: HttpVersion.HTTP2
    });

    const bucketPolicy = new BucketPolicy(this, "AllowReadAccessToCloudFront", { bucket: staticContentBucket });
    bucketPolicy.document.addStatements(new PolicyStatement({
      effect: Effect.ALLOW,
      actions: ["s3:GetObject"],
      resources: [`${staticContentBucket.bucketArn}/*`],
      principals: [accessIdentity.grantPrincipal]
    }));

    new CfnRecordSetGroup(this, "DnsRecordSet", {
      comment: "DNS entries for S3 Media Player static content",
      hostedZoneName: props.hostedZoneName,
      recordSets: [{
        name: props.domain, type: "A", aliasTarget: {
          dnsName: cloudfrontDistribution.distributionDomainName,
          hostedZoneId: CLOUDFRONT_HOSTED_ZONE_ID
        }
      }, {
        name: props.domain, type: "AAAA", aliasTarget: {
          dnsName: cloudfrontDistribution.distributionDomainName,
          hostedZoneId: CLOUDFRONT_HOSTED_ZONE_ID
        }
      }]
    });

    new CfnOutput(this, "StaticContentBucketName", {
      description: "Static content bucket name",
      value: staticContentBucket.bucketName
    });

    new CfnOutput(this, "CloudFrontDistributionId", {
      description: "CloudFront distribution ID",
      value: cloudfrontDistribution.distributionId
    });
  }
}
