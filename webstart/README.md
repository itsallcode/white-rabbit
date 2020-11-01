# Webstart configuration for WhiteRabbit JfxUi

## Initial setup

1. Generate keystore

    ```shell script
    cd webstart
    keytool.exe -keystore keystore.jks -genkey -alias whiterabbit-webstart
    ```

1. Create `webstart.properties` with the following content:

    ```properties
    keystoreFile = keystore.jks
    keystorePassword = <password>
    keystoreAlias = <alias>
    ```

1. Run

    ```shell script
     ./gradlew webstart:signJar --info
    ```
