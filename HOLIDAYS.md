# Holidays

Plugin (name) provides a configurable list of holidays.

Each holiday is meant to repeat every year and is defined by a formula in
order to compute concrete holiday instances for a given year.  Plugin (name)
supports the following formula flavors:

- a fixed date identical for every year
- a floating date defined by a specific date in each year and an offset
  restricted to a particular day of the week, e.g. fourth Sunday before
  Christmas
- a date defined relatively to Easter Sunday with a positive or negative
  offset of days

## Configuration

User can set up his or her individual personal list of favorite holidays using
the supported formula flavors.

### Configuration file

Plugin (name) expects the configuration file named `holidays.txt` to be
located in data directory next to file `projects.json`.

See WR configuration about how to configure the location of the data
directory.

### Content of configuration file

The configuration file is organized in lines. Each line can contain one of 5
types of content:

1. Empty
2. Comment
3. Fixed date holiday definition
4. Floating holiday definition
5. Easter-based holiday definition

All other lines are rated as illegal and ignored by the plugin, logging an
error message.

Whitespace is allowed in most places without changing the nature of the
line. Hence, a line containing nothing but tabs and spaces is still an empty
line.

#### Comment lines

A comment line is identified to start (after optional whitespace) with a hash
mark character "`#`".

#### Holiday definitions

All holiday definitions start with a category.  The category is an arbitrary
string of non-whitespace characters. The application evaluating your holidays
might support different categories of holidays, e.g. birthdays, anyversaries,
etc.  As a default we propose to use category "holiday".

The category is followed by a *tag* identifying the flavor of the holiday
definition, followed by arguments depending on the type. The last argument is
always a string containing the name of the holiday. All other strings except
the name of the holiday are case-insensitive.

In all definitions including the number of a month, January is 1,
December is 12. Day of month is an integer from 1 to 31.

In the following cases plugin (name) will log an error message and ignore the
holiday definition:
- if the tag does not match any of the three supported tags
  "fixed", "float", "easter"
- if the holiday definition contains illegal numbers, such as month 0 or 13,
  day 32, or day 30 for February
- if the day of week does not match the abbreviation of any of the english
  names Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
- if the day of week is abbreviated ambiguously, e.g. "T" or "S"

#### Fixed date holiday definition

A fixed date holiday definition has the tag "fixed", followed by the number of
the month and day of month.

Syntax: `holiday fixed <month> <day> <name>`

Sample: `holiday fixed 1 1 New Year`

#### Floating holiday definition

A floating holiday definition has the tag "float", followed by the offset, the
day of week, the number of the month and day of month.  Month and day of month
specify a *pivot date*.

If the offset is negative then the holiday is on or before the pivot date. If
the offset is positive then the holiday is on or after the pivot date.

If the day of week of the pivot date is identical to the specified one then
the offset starts to count on the pivot date, otherwise on the next instance
of the specified weekday before or after the pivot date.

If the day of month is less than 1 then plugin (name) uses a default value.
For positive offsets the default is the first day of the month, for negative
offsets the default is the last day of the month.

Syntax: `holiday float <offset> <day of week> <month> <day> <name>`

Samples:
- `holiday float 1 W 1 1 First Wednesday on or after New Year`
- `holiday float -2 MON 12 -1 Second Monday before New Year's eve, December the 31st`
- `holiday float -4 SUNDAY 12 24 First Advent`

#### Easter-based holiday definition

An Easter-based holiday definition has the tag "easter", followed by the
offset. The offset is the number of days from Easter Sunday. If offset is
negative then the holiday is before Easter Sunday, otherwise after.

Syntax: `holiday easter <offset> <name>`

Samples:
- `holiday easter   0 Easter Sunday`
- `holiday easter  -2 Good Friday`
- `holiday easter +49 Pentecost Sunday`
