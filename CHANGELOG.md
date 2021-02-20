# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0] - unreleased

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.6.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/8?closed=1)

## [1.5.0] - 2021-02-20

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.5.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/7?closed=1)

### Breaking change

* [#76](https://github.com/itsallcode/white-rabbit/issues/76): Remove deprecated text ui

### Added

* [#74](https://github.com/itsallcode/white-rabbit/issues/74): Map activity comments to PMSmart comment

### Fixed

* [#78](https://github.com/itsallcode/white-rabbit/issues/78): Don't fail when restoring UI-State when the number of table columns etc. changes.

## [1.4.0] - 2021-02-09

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.4.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/6?closed=1)

### Added

* [#69](https://github.com/itsallcode/white-rabbit/pull/69) Export monthly project report to [pm-smart](https://www.pm-smart.com/en/projekt-management-software). See [README.md for details](README.md#pmsmart)
* [#75](https://github.com/itsallcode/white-rabbit/pull/75): Allow plugins to contribute a storage plugin

## [1.3.0] 2021-01-09

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.3.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/5?closed=1)

### Added

* [#13](https://github.com/itsallcode/white-rabbit/issues/13): Persist window position and column width.
* [#49](https://github.com/itsallcode/white-rabbit/issues/49): Added monthly project report.
* [#59](https://github.com/itsallcode/white-rabbit/issues/59): Log to data directory.
* [#9](https://github.com/itsallcode/white-rabbit/issues/9): Add vacation and project report.

### Fixed

* [#62](https://github.com/itsallcode/white-rabbit/issues/62): Fix editing table cells.

## [1.2.0] 2020-12-05

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.2.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/4?closed=1)

### Added

* [#52](https://github.com/itsallcode/white-rabbit/issues/52): Improve keyboard usage for autocomplete text fields, immediately show proposals.
* [#54](https://github.com/itsallcode/white-rabbit/issues/54): First added activity has the remainder flag set.

### Fixed

* [#50](https://github.com/itsallcode/white-rabbit/issues/50), [#51](https://github.com/itsallcode/white-rabbit/issues/51): Switch view when month changes.

## [1.1.0] 2020-11-29

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.1.0) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/2?closed=1)

### Added

* [#16](https://github.com/itsallcode/white-rabbit/issues/16): Search-as-you-type for comments, select most common project for new activities.

## [1.0.1] 2020-11-25

See [Release](https://github.com/itsallcode/white-rabbit/releases/tag/v1.0.1) / [Milestone](https://github.com/itsallcode/white-rabbit/milestone/3?closed=1)

### Added

* [#21](https://github.com/itsallcode/white-rabbit/issues/21): Add menu items for editing configuration files

### Fixed

* [#44](https://github.com/itsallcode/white-rabbit/pull/44): Bugfix: Update activity duration when modifying day entry
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
