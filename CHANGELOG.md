# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] (unreleased)

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.0.1) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/3?closed=1)

### Fixed

* [#42](https://github.com/itsallcode/white-rabbit/pull/42): Bugfix: Keep edit focus for activities when table is updated every minute

## [1.0.0]

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.0.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/1?closed=1)

* [#40](https://github.com/itsallcode/white-rabbit/pull/40): Bugfix: Keep edit focus when table is updated every minute
* [#39](https://github.com/itsallcode/white-rabbit/pull/39): Publish WhiteRabbit using WebStart
* [#5](https://github.com/itsallcode/white-rabbit/issues/5): Add presets for adding interruptions
* [#20](https://github.com/itsallcode/white-rabbit/issues/20): Allow configuring the location of the configuration file, see [details below](#configuration)
* [#22](https://github.com/itsallcode/white-rabbit/issues/22), [#18](https://github.com/itsallcode/white-rabbit/issues/18): Add version number to build, show "About" dialog and build executable jars for all platforms, see [building a release](#building_release)
* [#15](https://github.com/itsallcode/white-rabbit/issues/15): Freeze previous end time on "stop working" in pop-up
* [#29](https://github.com/itsallcode/white-rabbit/issues/29): Relaxed parsing of time and duration
* [#27](https://github.com/itsallcode/white-rabbit/issues/27): Delete begin, end and interruption when changing day type to "not working"
* [#26](https://github.com/itsallcode/white-rabbit/issues/26): Omit "activities" from json when list is empty
* [#10](https://github.com/itsallcode/white-rabbit/issues/10): Facelift: Improved menu. Turned buttons and drop-down into toolbar. Turned OT (thanks to [redcatbear](https://github.com/redcatbear))
* [#6](https://github.com/itsallcode/white-rabbit/issues/6): Persist cell changes on focus loss
* Text UI is now deprecated, please use the new Java FX UI.
* Keep track of activities for time booking on multiple projects, See [project configuration](#project_config)
* Supports reduced working hours / short-time work, see [configuration option `current_working_time_per_day`](#optional_config)
