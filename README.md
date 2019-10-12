# white-rabbit
A time recording tool

[![Build Status](https://travis-ci.org/itsallcode/white-rabbit.svg?branch=develop)](https://travis-ci.org/itsallcode/white-rabbit)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=white-rabbit&metric=alert_status)](https://sonarcloud.io/dashboard?id=white-rabbit)

## Features

* Two user interfaces:
  * Simple text user interface for running in a console window
  * Java FX based user interface
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
* Automatic update in the background: just keep it running and it will record your working time
  * Start of work is detected via
    * Program start
    * Computer resumes from sleep in the morning
  * End of work is detected via
    * Program shutdown
    * Computer sleeps for the rest of the day
  * Interruptions detected when computer sleeps for more than 2 minutes
* Keeps track of the overtime in previous months to speed up reports
* Assumptions:
  * Working time of 8h Monday to Friday
  * Mandatory break of 45 minutes after 6 hours of working

### Java FX user interface

* Double click on a table cell (Type, Begin, End, Interruption and Comment) to edit it
  * Interruptions must be entered as `01:23` for 1 hour, 23 minutes
* Close the window to minimize in the task bar: <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/>
  * Double click <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/> to open the window again
  * Right click on <img src="jfxui\src\main\resources\icon.png" alt="white rabbit icon" width="16px"/> to add an interruption or exit the program

### Text interface

Usage:

* Manual interruptions: press `i` to start and stop interruptions
* Manual updates: press `u`
* Reporting of total overtime: press `r`

Example report:

```
2019-03-01 Fri WORK      08:00 - 17:00 break: 00:45, working time: 08:15, overtime: 00:15, Acc. overtime: 00:15, First working day (type WORK is optional)
2019-03-04 Mon VACATION                break: 00:00, working time: 00:00, overtime: 00:00, Acc. overtime: 00:15, Vacation day, no change to working time
2019-03-05 Tue FLEX_TIME               break: 00:00, working time: 00:00, overtime: -08:00, Acc. overtime: -07:45, Flex time, deducts 8h from your time
2019-03-06 Wed HOLIDAY                 break: 00:00, working time: 00:00, overtime: 00:00, Acc. overtime: -07:45, A public holiday, not working
2019-03-07 Thu SICK                    break: 00:00, working time: 00:00, overtime: 00:00, Acc. overtime: -07:45, Sick day, no change to working time
2019-03-08 Fri WORK      08:00 - 18:30 break: 00:45, interr.: 01:20, working time: 08:25, overtime: 00:25, Acc. overtime: -07:20, 1h 20min of interruption
2019-03-09 Sat WEEKEND                 break: 00:00, working time: 00:00, overtime: 00:00, Acc. overtime: -07:20, Saturday and Sunday automatically detected, no need to add them here.
Total overtime: PT-7H-20M
```

### Example data file `2019-03.json`

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
        }
    ]
}
```

### Notes

* Won't work on weekends. To force working on a weekend, manually create an entry with `"type" = "WORK"`.
* Public holidays are not detected automatically. Set the day type to `HOLIDAY` manually.
* If you change the working time in previous months you might need to adjust the `overtimePreviousMonth` field in the following months, e.g. by clicking the `Update overtime for all months` button in the Java FX UI.

## Usage

### Requirements

JDK 11, e.g. https://adoptopenjdk.net/

### Configuration

Create file `time.properties` in the current working directory with the following content:

```properties
data = <path-to-data-dir> # e.g.: ../time-recording-data/
```

### Clone, configure and build

```bash
mkdir time-recording-data
git clone https://github.com/itsallcode/white-rabbit.git
cd white-rabbit
echo "data = ../time-recording-data/" > time.properties
./gradlew build
```

### Launching

Start text ui:
```bash
java -jar textui/build/libs/textui.jar
```

Start Java FX ui:
```bash
java -jar jfxui/build/libs/jfxui.jar
```
