# Webstart configuration for WhiteRabbit JfxUi

## Initial setup

* Generate keystore

    ```shell script
    cd webstart
    keytool.exe -keystore keystore.jks -genkey -alias whiterabbit-webstart
    ```

* Install the [AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
* [Configure the AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html)
* Deploy infrastructure to get s3 bucket name and cloudfront distribution id, see [webstart-infrastructure/README.md](../webstart-infrastructure/README.md)
* Create `webstart.properties` with the following content:

    ```properties
    keystoreFile = keystore.jks
    keystorePassword = <password>
    keystoreAlias = <alias>
    domain = whiterabbit.example.com
    s3Bucket = <bucket name>
    cloudfrontDistribution = <cloudfront distribution id>
    ```
