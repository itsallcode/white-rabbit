# Developer Guide

## Clone and configure

```sh
mkdir time-recording-data
git clone https://github.com/itsallcode/white-rabbit.git
cd white-rabbit
# Configure
echo "data = $HOME/time-recording-data/" > $HOME/.whiterabbit.properties
```

## Using Build Scripts

### Build and launch

```sh
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

### Running Tests

Run all tests:

```sh
./gradlew check
```

Run only UI-Tests:

```sh
# Headless (default)
./gradlew check
# Not Headless (don't move mouse while running!)
./gradlew check -PuiTestsHeadless=false
```

Run a single test:

```sh
./gradlew uiTest -i --tests AboutDialogUiTest
```

### Check that dependencies are up-to-date

```sh
./gradlew dependencyUpdates
```

### <a name="build_native_package"></a>Build Native Packages

Precondition for Windows: Install the [WiX Toolset](https://wixtoolset.org) and add it to the `PATH`.

```sh
./gradlew jpackage --info
```

## Deployment

This will build WhiteRabbit and publish the plugin api to Maven Central.

### Initial setup

Add the following to your `~/.gradle/gradle.properties`:

    ```properties
    ossrhUsername=<your maven central username>
    ossrhPassword=<your maven central passwort>

    signing.keyId=<gpg key id (last 8 chars)>
    signing.password=<gpg key password>
    signing.secretKeyRingFile=<path to secret keyring file>
    ```

### <a name="build_and_deploy"></a>Build and deploy

1. Make sure the [Changelog](../CHANGELOG.md) is updated
2. Run the following command:

    ```sh
    ./gradlew clean build publish closeAndReleaseRepository --info
    ```

    The release will be written to `jfxui/build/libs/white-rabbit-fx-<version>.jar`. Snapshots will be available at [oss.sonatype.org](https://oss.sonatype.org/content/repositories/snapshots/org/itsallcode/whiterabbit/).

3. Create a new [release](https://github.com/itsallcode/white-rabbit/releases) in GitHub and attach the built jar.
4. Close the [milestone](https://github.com/itsallcode/white-rabbit/milestones) in GitHub.
5. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/whiterabbit/).
