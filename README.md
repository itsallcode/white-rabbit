# white-rabbit
A time recording tool

## Usage

### Requirements

JDK 11, e.g. OpenJDK from https://jdk.java.net/11/

### Configuration

Create file `time.properties` in the current directory with the following content:

```properties
data = <path-to-data-dir> # e.g.: ../time-recording-data/
```

### Starting

```bash
cd white-rabbit
./gradlew build
java -jar textui/build/libs/textui.jar
```
