# white-rabbit
A time recording tool

[![Build](https://github.com/itsallcode/white-rabbit/workflows/Build/badge.svg)](https://github.com/itsallcode/white-rabbit/actions?query=workflow%3ABuild)
![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/itsallcode/white-rabbit)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.whiterabbit%3Awhite-rabbit&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.itsallcode.whiterabbit%3Awhite-rabbit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode.whiterabbit%3Awhite-rabbit&metric=coverage)](https://sonarcloud.io/dashboard?id=org.itsallcode.whiterabbit%3Awhite-rabbit)
[![Maven Central](https://img.shields.io/maven-central/v/org.itsallcode.whiterabbit/whiterabbit-plugin-api.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.itsallcode.whiterabbit%22%20a%3A%22whiterabbit-plugin-api%22)

* [User Guide](docs/user_guide.md)
* [Troubleshooting](docs/user_guide.md#troubleshooting)
* [Changelog](CHANGELOG.md)
* [Developer Guide](docs/developer_guide.md)

![Screenshot of WhiteRabbit](screenshot.png)

## Features

* Records begin, end and interruption of your working day
* Data storage in human readable json files, one file per month
  * Backup data by creating a git repository for the data folder and commit every day
* Supported day types (see json example):
  * Normal working day (default for Monday to Friday): `WORK`
  * Weekend (Saturday and Sunday, detected automatically): `WEEKEND`
  * Public holiday (won't deduct overtime): `HOLIDAY`
  * Vacation (won't deduct overtime): `VACATION`
  * Flex time (will deduct overtime): `FLEX_TIME`
  * Sickness (won't deduct overtime): `SICK`
* Automatic update in the background: just keep it running, and it will record your working time:
  * Start of work is detected when
    * Program start
    * Computer resumes from sleep in the morning
  * Detects the end of work when
    * Program shutdown
    * Computer sleeps for the rest of the day
    * You click the "Stop working for today" button
  * Interruptions detected when computer sleeps for more than 2 minutes
* Generates reports for your vacation and monthly working time
* Detects when a second instance is started to avoid data corruption
* Export project working times to pm-smart. See [the user guide](docs/user_guide.md#pmsmart) for details.
