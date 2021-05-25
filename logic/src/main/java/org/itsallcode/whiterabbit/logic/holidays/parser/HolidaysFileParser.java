package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.itsallcode.whiterabbit.logic.holidays.Holiday;
import org.itsallcode.whiterabbit.logic.holidays.Holidays;

public class HolidaysFileParser
{
    public static Holidays parse(InputStream stream) throws IOException
    {
        final Holidays holidays = new Holidays();
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
