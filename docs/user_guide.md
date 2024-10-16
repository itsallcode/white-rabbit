
# User Guide

## Install WhiteRabbit as Executable JAR

Precondition: Install Java Runtime Environment (JRE) 21, e.g. from [Adoptium / Eclipse Temurin](https://adoptium.net/).

1. Download the `.jar` file from the latest [release](https://github.com/itsallcode/white-rabbit/releases)
2. Launch it by

  * double clicking or
  * executing command

  ```sh
  java -jar whiterabbitfx-signed.jar
  ```

## Install Native Package

Native packages already contain a Java Runtime Environment. You can download native packages for your platform from the latest [release](https://github.com/itsallcode/white-rabbit/releases):

* Windows Installer: `.exe`
* macOS Disk Image: `.dmg`
* Ubuntu/Debian Package: `.deb`

### Installing a `.deb` file

Install the `.deb` file like this:

```shell
sudo dpkg --install whiterabbit_${version}_amd64.deb
```

This will install WhiteRabbit to `/opt/whiterabbit/`. You can launch it by running

```shell
/opt/whiterabbit/bin/WhiteRabbit &
```

To remove WhiteRabbit, run

```shell
sudo dpkg --remove whiterabbit
```

## <a name="configuration"></a>Configuration

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

### <a name="optional_config"></a>Optional configuration settings

* `locale`: format of date and time values as an [IETF language tag](https://en.wikipedia.org/wiki/IETF_language_tag), e.g. `de`, `de-DE` or `en-GB`. Default: system locale.
  * Note: enter a locale with country code (e.g. `de-DE`) to get correct formatting of date and time, e.g. the calendar week.
* `current_working_time_per_day`: working time per day (default: 8 hours). Format: see [below](#duration-format). Caution: This setting will only affect the future.
  * We recommend to configure this when starting to use WhiteRabbit.
* `mandatory_break`: mandatory break per day (default: 45 minutes). Format: see [below](#duration-format). Caution: This setting will also affect the past, i.e. the overtime of **all** days will be re-calculated.
  * We recommend to configure this when starting to use WhiteRabbit.
* `reduce_mandatory_break_by_interruption`: Reduce the mandatory break by entered interruption (`true` or `false`, default: `false`). If this is `true`, the mandatory break will be set to zero when the interruption is longer than the mandatory break.
  * We recommend to configure this when starting to use WhiteRabbit.

#### Duration format

Enter duration values in the format used by [Duration.parse()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Duration.html#parse(java.lang.CharSequence)). Examples:

* `PT5H`: 5 hours
* `PT5H30M`: 5 hours and 30 minutes
* `PT45M`: 45 minutes
* `PT0M`: 0 minutes

### <a name="project_config"></a>Project configuration

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

## Usage

### Java FX user interface

* Double click on a table cell (Type, Begin, End, Interruption and Comment) to edit it
  * Interruptions must be entered as `01:23` for 1 hour, 23 minutes
* Close the window to minimize in the task bar: <img src="../jfxui/src/main/resources/icon.png" alt="white rabbit icon" width="16px"/>
  * Double click <img src="../jfxui/src/main/resources/icon.png" alt="white rabbit icon" width="16px"/> to open the window again
  * Right click on <img src="../jfxui/src/main/resources/icon.png" alt="white rabbit icon" width="16px"/> to add an interruption or exit the program

### Notes

* Won't work on weekends. To force working on a weekend, manually change the day type to `WORK`.
* Public holidays can be calculated using plugin [holidays-calculator](#holidays_calculator) or user can are set the day type to `HOLIDAY` manually.
* If you manually change the working time in previous months you might need to adjust the `overtimePreviousMonth` field in the following months by selecting menu item `File -> Update overtime for all months`.
* Assumptions:
    * Working time of 8h Monday to Friday
    * Mandatory break of 45 minutes after 6 hours of working

## Logging

WhiteRabbit logs to stdout and to `$data/logs/white-rabbit.log` where `$data` is the data directory defined in the [configuration](#configuration).

## <a name="plugins"></a>Using Plugins

The default plugins `holidays-calculator` and `csv` are already included in the download packages. To install third-party plugins, copy them to `$HOME/.whiterabbit/plugins/`.

### <a name="holidays_calculator"></a>Using the Holidays Calculator Plugin

Optionally you can configure holidays-calculator plugin to enable WhiteRabbit to display your personal selection of holidays.

#### Setup and usage

Create a file named `holidays.cfg` in your data directory defined in the [configuration file](#configuration) of WhiteRabbit.

You can use one of the <a href="https://github.com/itsallcode/holiday-calculator/tree/main/holidays">predefined holiday definition files</a> or you can edit the file
and add or remove holidays to your own taste, see the <a href="https://github.com/itsallcode/holiday-calculator/blob/main/README.md#configuration-file">holiday-calculator documentation</a> for a detailed description of the syntax.

**Note:** WhiteRabbit adds holidays provided by plugins only to new months without any time recordings. As soon as the user adds a time recording for a particular month, WhiteRabbit saves the time recordings for this month including any holidays whether provided by plugins or entered manually. After this point in time for the given month WhiteRabbit uses only on the saved file and will not ask any plugin to update the holidays.

### <a name="csvexport"></a>Using the CSV Export Plugin

#### What can it do / limitations

CSVExport plugin supports the export of the current monthly report to a pre-configured path. The file names are hard coded, and have the format of `YYYY-MM_working_time.csv`. Please note that all days must have a valid project assigned for the correct export.

#### Setup and usage

1. Create a project configuration as described [above](#project_config).
1. Start the export in WhiteRabbit:

    1. Select the month that you want to export
    1. Select menu Reports > Project report
    1. Click the "Export to csv" button

#### Optional configuration settings

Currently, you can configure the destination path, separator and flag in WhiteRabbit's configuration file:

```properties
csv.destination = ~/working_time_reports
csv.separator = /t
csv.filter_for_weekdays = True
```

The default values are:

```properties
csv.destination = $HOME
csv.separator = ","
csv.filter_for_weekdays = False
```
