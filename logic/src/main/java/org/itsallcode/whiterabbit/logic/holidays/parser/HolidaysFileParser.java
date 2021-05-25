package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.whiterabbit.logic.holidays.Holiday;

public class HolidaysFileParser
{
    public static List<Holiday> parse(InputStream stream) throws IOException
    {
        final List<Holiday> holidays = new ArrayList<>();
        final HolidayParser holidayParser = new HolidayParser();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();
            if (line.isEmpty())
            {
                continue;
            }
            if (line.startsWith("#"))
            {
                continue;
            }

            final Holiday holiday = holidayParser.parse(line);
            if (holiday != null)
            {
                holidays.add(holiday);
            }
        }
        return holidays;
    }
}
