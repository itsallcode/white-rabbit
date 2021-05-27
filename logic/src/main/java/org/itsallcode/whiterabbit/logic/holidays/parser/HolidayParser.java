package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.time.DayOfWeek;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.whiterabbit.logic.holidays.EasterBasedHoliday;
import org.itsallcode.whiterabbit.logic.holidays.FixedDateHoliday;
import org.itsallcode.whiterabbit.logic.holidays.FloatingHoliday;
import org.itsallcode.whiterabbit.logic.holidays.Holiday;

public class HolidayParser
{
    // names of groups in regular expressions in order to easily extract matched
    // parts
    private static final String CATEGORY_GROUP = "category";
    private static final String TYPE_GROUP = "type";
    private static final String MONTH_GROUP = "month";
    private static final String DAY_GROUP = "day";
    private static final String OFFSET_GROUP = "offset";
    private static final String DAY_OF_WEEK_GROUP = "dayOfWeek";
    private static final String NAME_GROUP = "name";

    // tokens for regular expressions
    private static final Token MONTH = new Token(MONTH_GROUP, "0?1|0?2|0?3|0?4|0?5|0?6|0?7|0?8|0?9|10|11|12");
    private static final Token DAY = new Token(DAY_GROUP,
            "0?1|0?2|0?3|0?4|0?5|0?6|0?7|0?8|0?9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31");
    private static final Token DAY_OR_DEFAULT = new Token(DAY_GROUP, "-1|0|" + DAY.pattern);
    private static final Token OFFSET = new Token(OFFSET_GROUP, "[+-]?\\d\\d?");
    private static final Token DAY_OF_WEEK = new Token(DAY_OF_WEEK_GROUP, "[a-z]+");
    private static final Token HOLIDAY_NAME = new Token(NAME_GROUP, ".*");

    private static final Token HOLIDAY = new Token(CATEGORY_GROUP, "holiday");
    private static final Token FIXED = new Token(TYPE_GROUP, "fixed");
    private static final Token FLOAT = new Token(TYPE_GROUP, "float");
    private static final Token EASTER = new Token(TYPE_GROUP, "easter");

    // patterns
    private static final String SPACE_REGEXP = "\\s+";
    private static final Pattern FIXED_HOLIDAY = buildRegexp(HOLIDAY, FIXED, MONTH, DAY, HOLIDAY_NAME);
    private static final Pattern FLOATING_HOLIDAY = buildRegexp(HOLIDAY, FLOAT, OFFSET, DAY_OF_WEEK, MONTH,
            DAY_OR_DEFAULT, HOLIDAY_NAME);
    private static final Pattern EASTER_BASED_HOLIDAY = buildRegexp(HOLIDAY, EASTER, OFFSET, HOLIDAY_NAME);

    static Pattern buildRegexp(final Token... tokens)
    {
        final StringBuilder sb = new StringBuilder();
        for (final Token token : tokens)
        {
            if (sb.length() > 0)
            {
                sb.append(SPACE_REGEXP);
            }
            sb.append(String.format("(?<%s>%s)", token.groupName, token.pattern));
        }

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    public Holiday parse(String line)
    {
        final String trimmed = line.trim();
        final HolidayMatcher[] matchers = new HolidayMatcher[] {
                new FixedDateMatcher(), new FloatingDateMatcher(), new EasterBasedMatcher() };

        for (final HolidayMatcher m : matchers)
        {
            final Holiday holiday = m.createHoliday(trimmed);
            if (holiday != null)
            {
                return holiday;
            }
        }

        return null;
    }

    private static class FixedDateMatcher extends HolidayMatcher
    {
        public FixedDateMatcher()
        {
            super(FIXED_HOLIDAY);
        }

        @Override
        Holiday createHoliday(Matcher matcher)
        {
            return new FixedDateHoliday(
                    "holiday",
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(MONTH_GROUP)),
                    Integer.parseInt(matcher.group(DAY_GROUP)));
        }
    }

    private static class FloatingDateMatcher extends HolidayMatcher
    {
        private final DayOfWeekParser dayOfWeekParser = new DayOfWeekParser();

        public FloatingDateMatcher()
        {
            super(FLOATING_HOLIDAY);
        }

        @Override
        Holiday createHoliday(Matcher matcher)
        {
            final DayOfWeek dayOfWeek = dayOfWeekParser.getDayOfWeek(matcher.group(DAY_OF_WEEK_GROUP));
            if (dayOfWeek == null)
            {
                return null;
            }

            return new FloatingHoliday(
                    "holiday",
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(OFFSET_GROUP)),
                    dayOfWeek,
                    Integer.parseInt(matcher.group(MONTH_GROUP)), Integer.parseInt(matcher.group(DAY_GROUP)));
        }
    }

    private static class EasterBasedMatcher extends HolidayMatcher
    {
        public EasterBasedMatcher()
        {
            super(EASTER_BASED_HOLIDAY);
        }

        @Override
        Holiday createHoliday(Matcher matcher)
        {
            return new EasterBasedHoliday(
                    "holiday",
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(OFFSET_GROUP)));
        }
    }

    private static class Token
    {
        public final String groupName;
        public final String pattern;

        public Token(String groupName, String pattern)
        {
            this.groupName = groupName;
            this.pattern = pattern;
        }
    }
}
