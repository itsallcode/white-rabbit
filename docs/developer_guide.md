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

# Build and run the shadowJar of the final product.
./gradlew runProduct
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

### Creating a Release

#### Preparations

1. Checkout the `main` branch, create a new branch.
2. Update version number in `build.gradle` and `README.md`.
3. Add changes in new version to `CHANGELOG.md`.
4. Commit and push changes.
5. Create a new pull request, have it reviewed and merged to `main`.

#### Perform the Release

1. Start the release workflow
  * Run command `gh workflow run release.yml --repo itsallcode/white-rabbit --ref main`
  * or go to [GitHub Actions](https://github.com/itsallcode/white-rabbit/actions/workflows/release.yml) and start the `release.yml` workflow on branch `main`.
2. Update title and description of the newly created [GitHub release](https://github.com/itsallcode/white-rabbit/releases).
3. Close the [milestone](https://github.com/itsallcode/white-rabbit/milestones) in GitHub.
4. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/whiterabbit/).
