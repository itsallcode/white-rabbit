# white-rabbit
A time recording tool

[![Build](https://github.com/itsallcode/white-rabbit/workflows/Build/badge.svg)](https://github.com/itsallcode/white-rabbit/actions?query=workflow%3ABuild)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=white-rabbit&metric=alert_status)](https://sonarcloud.io/dashboard?id=white-rabbit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=white-rabbit&metric=coverage)](https://sonarcloud.io/dashboard?id=white-rabbit)
[![deepcode](https://www.deepcode.ai/api/gh/badge?key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwbGF0Zm9ybTEiOiJnaCIsIm93bmVyMSI6Iml0c2FsbGNvZGUiLCJyZXBvMSI6IndoaXRlLXJhYmJpdCIsImluY2x1ZGVMaW50IjpmYWxzZSwiYXV0aG9ySWQiOjE1ODQ3LCJpYXQiOjE2MDExMjk1NTB9.J8l6aFttX7uETXvT1KzG2ai2kER_GJF94SZBOX9FTP0)](https://www.deepcode.ai/app/gh/itsallcode/white-rabbit/_/dashboard?utm_content=gh%2Fitsallcode%2Fwhite-rabbit)

* [Features](#features)
* [Usage](#usage)
* [Recent changes](#changes)

![Screenshot of WhiteRabbit](screenshot.png)

## <a name="features"></a>Features

* Records begin, end and interruption of your working day
* Data storage in human readable json files, one file per month
  * Backup data by creating a git repository for the data folder and commit every day
* Supported day types (see json example):
  * normal working day (default for Monday to Friday): `WORK`
  * weekend (Saturday and Sunday, detected automatically): `WEEKEND`
  * public holiday (won't deduct overtime): `HOLIDAY`
  * vacation (won't deduct overtime): `VACATION`
  * flex time (will deduct overtime): `FLEX_TIME`
  * sickness (won't deduct overtime): `SICK`
* Automatic update in the background: just keep it running, and it will record your working time
  * Start of work is detected via
    * Program start
    * Computer resumes from sleep in the morning
  * Detects the end of work via
    * Program shutdown
    * Computer sleeps for the rest of the day
  * Interruptions detected when computer sleeps for more than 2 minutes
* Keeps track of the overtime in previous months to speed up reports
* Generates vacation report (no UI yet, written to standard out)
* Detects when a second instance is started to avoid data corruption
* Assumptions:
  * Working time of 8h Monday to Friday
  * Mandatory break of 45 minutes after 6 hours of working

### Java FX user interface

* Double click on a table cell (Type, Begin, End, Interruption and Comment) to edit it
  * Interruptions must be entered as `01:23` for 1 hour, 23 minutes
* Close the window to minimize in the task bar: <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/>
  * Double click <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/> to open the window again
  * Right click on <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/> to add an interruption or exit the program

### Example data file `<data>/2019/2019-03.json`

This is generated automatically. The Java FX user interface allows you to edit it. When using the text ui you need to edit the file with your favorite text editor.

```json
{
    "year": 2019,
    "month": "MARCH",
    "overtimePreviousMonth": "PT3H10M",
    "days": [
        {
            "date": "2019-03-01",
            "begin": "08:00:00",
            "end": "17:00:00",
            "comment": "First working day (type WORK is optional)"
        },
        {
            "date": "2019-03-04",
            "type": "VACATION",
            "comment": "Vacation day, no change to working time"
        },
        {
            "date": "2019-03-05",
            "type": "FLEX_TIME",
            "comment": "Flex time, deducts 8h from your time"
        },
        {
            "date": "2019-03-06",
            "type": "HOLIDAY",
            "comment": "A public holiday, not working"
        },
        {
            "date": "2019-03-07",
            "type": "SICK",
            "comment": "Sick day, no change to working time"
        },
        {
            "date": "2019-03-08",
            "begin": "08:00:00",
            "end": "18:30:00",
            "interruption": "PT1H20M",
            "comment": "1h 20min of interruption"
        },
        {
            "date": "2019-03-09",
            "type": "WEEKEND",
            "comment": "Saturday and Sunday automatically detected, no need to add them here."
        },
        {
            "date": "2019-03-11",
            "begin": "08:00:00",
            "end": "15:00:00",
            "workingHours": "PT6H",
            "comment": "Working short time, 6 hours per day"
        }
    ]
}
```

### Notes

* Won't work on weekends. To force working on a weekend, manually create an entry with `"type" = "WORK"`.
* Public holidays are not detected automatically. Set the day type to `HOLIDAY` manually.
* If you change the working time in previous months you might need to adjust the `overtimePreviousMonth` field in the following months by selecting menu item `File -> Update overtime for all months` in the Java FX UI.
* When you modify config file `time.properties` you need to restart WhiteRabbit manually.

## <a name="changes"></a>Recent changes

* [#5](https://github.com/itsallcode/white-rabbit/issues/5): Add presets for adding interruptions
* [#20](https://github.com/itsallcode/white-rabbit/issues/20): Allow configuring the location of the configuration file, see [details below](#configuration)
* [#22](https://github.com/itsallcode/white-rabbit/issues/22), [#18](https://github.com/itsallcode/white-rabbit/issues/18): Add version number to build, show "About" dialog and build executable jars for all platforms, see [building a release](#building_release)
* [#15](https://github.com/itsallcode/white-rabbit/issues/15): Freeze previous end time on "stop working" in pop-up
* [#29](https://github.com/itsallcode/white-rabbit/issues/29): Relaxed parsing of time and duration
* [#27](https://github.com/itsallcode/white-rabbit/issues/27): Delete begin, end and interruption when changing day type to "not working"
* [#26](https://github.com/itsallcode/white-rabbit/issues/26): Omit "activities" from json when list is empty
* [#10](https://github.com/itsallcode/white-rabbit/issues/10): Facelift: Improved menu. Turned buttons an drop-down into toolbar. Turned OT (thanks to [redcatbear](https://github.com/redcatbear))
* [#6](https://github.com/itsallcode/white-rabbit/issues/6): Persist cell changes on focus loss
* Text UI is now deprecated, please use the new Java FX UI.
* Keep track of activities for time booking on multiple projects, See [project configuration](#project_config)
* Supports reduced working hours / short-time work, see [configuration option `current_working_time_per_day`](#optional_config)

## <a name="usage"></a>Usage

### Requirements

JDK 11, e.g. [AdoptOpenJDK](https://adoptopenjdk.net/).

### <a name="configuration"></a>Configuration

White Rabbit will search for the configuration file in the following locations:

1. The path specified via command line parameter `--config=<path>`
2. `time.properties` in the current working directory
3. `$HOME/.whiterabbit.properties`

If the no config file is found, White Rabbit will create a default file at `$HOME/.whiterabbit.properties`, using data directory `$HOME/whiterabbit-data`.

The config file has the following content:

```properties
data = <path-to-data-dir> # e.g.: ../time-recording-data/
```

Restart WhiteRabbit after changing the configuration file.

#### <a name="optional_config"></a>Optional configuration settings

* `locale`: format of date and time values, e.g. `de` or `en`. Default: system locale.
* `current_working_time_per_day`: custom working time per day differing from the default of 8 hours. Format: see [Duration.parse()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html#parse(java.lang.CharSequence)), e.g. `PT5H` for 5 hours or `PT5H30M` for 5 hours and 30 minutes. This setting will only affect the future.

#### <a name="project_config"></a>Project configuration

To use activity tracking, create file `projects.json` in your data directory with the following content:

```json
{
    "projects": [
        {
            "projectId": "p1",
            "label": "Project 1",
            "costCarrier": "P1001"
        },
        {
            "projectId": "p2",
            "label": "Project 2",
            "costCarrier": "P1002"
        },
        {
            "projectId": "general",
            "label": "General",
            "costCarrier": "P0001"
        },
        {
            "projectId": "training",
            "label": "Training",
            "costCarrier": "P0002"
        }
    ]
}
```

### <a name="running"></a>Running WhiteRabbit

#### Clone and configure

```bash
mkdir time-recording-data
git clone https://github.com/itsallcode/white-rabbit.git
cd white-rabbit
# Configure
echo "data = ../time-recording-data/" > time.properties
```

#### Build and launch

```bash
./gradlew build
java -jar jfxui/build/libs/jfxui.jar
```

#### Build and launch with gradle

```bash
./gradlew runJfxui
```

#### Run UI-Tests

```bash
# Headless (default)
./gradlew check
# Not Headless (don't move mouse while running)
./gradlew check -PuiTestsHeadless=false
```

#### <a name="building_release"></a>Building a release

```bash
./gradlew build -PreleaseVersion=<version>
```

The release will be written to `jfxui/build/libs/white-rabbit-fx-<version>.jar`

## WebStart Infrastructure

### Managing configuration in a private branch

This project requires some configuration files with deployment specific information, e.g. domain names that should not be stored in a public git repository. That's why these files are added to `.gitignore`. If you want to still keep your configuration under version control you can do so in a private branch (e.g. `private-master`) that you could push to a private repository only.

When switching from `private-master` to the public `develop` branch, git will delete the configuration files. To restore them you can use the following command:

```bash
git show private-master:webstart-infrastructure/config.ts > webstart-infrastructure/config.ts \
        && git show private-master:webstart/webstart.properties > webstart/webstart.properties \
        && git show private-master:webstart/keystore.jks > webstart/keystore.jks
```
