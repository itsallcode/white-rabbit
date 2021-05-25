package org.itsallcode.whiterabbit.logic.holidays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HolidayParser
{
    // names of groups in regular expressions in order to easily extract matched
    // parts
    private static final String TYPE_GROUP = "type";
    private static final String MONTH_GROUP = "month";
    private static final String DAY_GROUP = "day";
    private static final String OFFSET_GROUP = "offset";
    private static final String DAY_OF_WEEK_GROUP = "dayOfWeek";
    private static final String NAME_GROUP = "name";

    // tokens for regular expressions
    private static final Token MONTH = new Token(MONTH_GROUP, "1|2|3|4|5|6|7|8|9|10|11|12");
    private static final Token DAY = new Token(DAY_GROUP,
            "1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31");
    private static final Token DAY_OR_DEFAULT = new Token(DAY_GROUP, "-1|0|" + DAY.pattern);
    private static final Token OFFSET = new Token(OFFSET_GROUP, "[+-]?\\d+");
    private static final Token DAY_OF_WEEK = new Token(DAY_OF_WEEK_GROUP, "[a-z]+");
    private static final Token HOLIDAY_NAME = new Token(NAME_GROUP, ".*");

    private static final Token FIXED = new Token(TYPE_GROUP, "fixed");
    private static final Token FLOAT = new Token(TYPE_GROUP, "float");
    private static final Token EASTER = new Token(TYPE_GROUP, "easter");

    // patterns
    private static final String SPACE_REGEXP = "\\s+";
    private static final Pattern FIXED_HOLIDAY = buildRegexp(FIXED, MONTH, DAY, HOLIDAY_NAME);
    private static final Pattern FLOATING_HOLIDAY = buildRegexp(FLOAT, OFFSET, DAY_OF_WEEK, MONTH, DAY_OR_DEFAULT,
            HOLIDAY_NAME);
    private static final Pattern EASTER_BASED_HOLIDAY = buildRegexp(EASTER, OFFSET, HOLIDAY_NAME);

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

    // fields
    private final DayOfWeekParser dayOfWeekParser = new DayOfWeekParser();

    public Holiday parse(String line)
    {
        final String trimmed = line.trim();
        Matcher matcher;

        matcher = FIXED_HOLIDAY.matcher(trimmed);
        if (matcher.matches())
        {
            return new FixedDateHoliday(
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(MONTH_GROUP)),
                    Integer.parseInt(matcher.group(DAY_GROUP)));
        }

        matcher = FLOATING_HOLIDAY.matcher(trimmed);
        if (matcher.matches())
        {
            return new FloatingHoliday(
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(OFFSET_GROUP)),
                    dayOfWeekParser.getDayOfWeek(matcher.group(DAY_OF_WEEK_GROUP)),
                    Integer.parseInt(matcher.group(MONTH_GROUP)),
                    Integer.parseInt(matcher.group(DAY_GROUP)));
        }

        matcher = EASTER_BASED_HOLIDAY.matcher(trimmed);
        if (matcher.matches())
        {
            return new EasterBasedHoliday(
                    matcher.group(NAME_GROUP),
                    Integer.parseInt(matcher.group(OFFSET_GROUP)));
        }

        return null;
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
