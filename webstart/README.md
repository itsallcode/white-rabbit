# Webstart configuration for WhiteRabbit JfxUi

## Initial setup

* Generate keystore

    ```shell script
    cd webstart
    keytool.exe -keystore keystore.jks -genkey -alias whiterabbit-webstart
    ```

* Install the [AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
* [Configure the AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html)
* Create `webstart.properties` with the following content:

    ```properties
    keystoreFile = keystore.jks
    keystorePassword = <password>
    keystoreAlias = <alias>
    s3Bucket = <bucket name>
    cloudfrontDistribution = <cloudfront distribution id>
    ```

## Deployment

Run

```shell script
./gradlew build webstart:publishWebstart --info -PreleaseVersion=<version>
```

The uploaded content will be available at [https://whiterabbit.chp1.net](https://whiterabbit.chp1.net).
