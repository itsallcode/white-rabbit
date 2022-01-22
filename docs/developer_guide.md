# <a name="development"></a>Development

## Clone and configure

```bash
mkdir time-recording-data
git clone https://github.com/itsallcode/white-rabbit.git
cd white-rabbit
# Configure
echo "data = $HOME/time-recording-data/" > $HOME/.whiterabbit.properties
```

## Build and launch

```bash
# Build WhiteRabbit and install plugins to $HOME/.whiterabbit/plugins/
./gradlew build installPlugins
# To skip unit and ui-tests, run
./gradlew build installPlugins -x test -x uiTest
# Run
java -jar jfxui/build/libs/white-rabbit-fx-<version>[-SNAPSHOT].jar

# Build and run, loading plugins from $HOME/.whiterabbit/plugins/
./gradlew run

# Build and run including plugins. Useful when developing plugins.
# Make sure to remove unwanted plugins from $HOME/.whiterabbit/plugins/
./gradlew runWithPlugins
```

## Run UI-Tests

```bash
# Headless (default)
./gradlew check
# Not Headless (don't move mouse while running)
./gradlew check -PuiTestsHeadless=false
```

## Check that dependencies are up-to-date

```bash
./gradlew dependencyUpdates
```

## <a name="build_native_package"></a>Build Native Packages

Precondition for Windows: Install the [WiX Toolset](https://wixtoolset.org) and add it to the `PATH`.

```bash
./gradlew jpackage --info -PreleaseVersion=x.y.z
```

## Deployment

This will build WhiteRabbit, upload it to the AWS S3 bucket and publish the plugin api to Maven Central.

### Initial setup

1. Setup [keystore and AWS configuration](webstart/README.md).
2. Add the following to your `~/.gradle/gradle.properties`:

    ```properties
    ossrhUsername=<your maven central username>
    ossrhPassword=<your maven central passwort>

    signing.keyId=<gpg key id (last 8 chars)>
    signing.password=<gpg key password>
    signing.secretKeyRingFile=<path to secret keyring file>
    ```

### <a name="build_and_deploy"></a>Build and deploy

1. Make sure the [Changelog](CHANGELOG.md) is updated
2. Run the following command:

    ```bash
    ./gradlew clean build publish closeAndReleaseRepository webstart:publishWebstart --info -PreleaseVersion=<version>
    ```

    The release will be written to `jfxui/build/libs/white-rabbit-fx-<version>.jar` and the uploaded content will be available at [whiterabbit.chp1.net](https://whiterabbit.chp1.net). Snapshots will be available at [oss.sonatype.org](https://oss.sonatype.org/content/repositories/snapshots/org/itsallcode/whiterabbit/).

3. Create a new [release](https://github.com/itsallcode/white-rabbit/releases) in GitHub and attach the built jar.
4. Close the [milestone](https://github.com/itsallcode/white-rabbit/milestones) in GitHub.
5. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/whiterabbit/).

## Managing WebStart configuration in a private branch

This project requires some configuration files with deployment specific information, e.g. domain names that should not be stored in a public git repository. That's why these files are added to `.gitignore`. If you want to still keep your configuration under version control you can do so in a private branch (e.g. `private-master`) that you push to a private repository only.

When switching from `private-master` to the public `main` branch, git will delete the configuration files. To restore them you can run the following command in the project root:

```bash
git show private-master:webstart-infrastructure/config.ts > webstart-infrastructure/config.ts \
    && git show private-master:webstart/webstart.properties > webstart/webstart.properties \
    && git show private-master:webstart/keystore.jks > webstart/keystore.jks
```
