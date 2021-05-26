package org.itsallcode.whiterabbit.logic.holidays.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.holidays.Holiday;

public class HolidaysFileParser
{
    public static class Error
    {
        public final int lineNumber;
        public final String content;

        public Error(int lineNumber, String content)
        {
            this.lineNumber = lineNumber;
            this.content = content;
        }
    }

    private static final Logger LOG = LogManager.getLogger(HolidaysFileParser.class);

    final HolidayParser holidayParser = new HolidayParser();
    private final List<Error> errors = new ArrayList<>();
    private final String identifier;

    /**
     * @param inputSourceIdentifier
     *            Just a string in order to identify the stream in potential
     *            error messages. Could be name or path of the file represented
     *            by the stream.
     */
    public HolidaysFileParser(String inputSourceIdentifier)
    {
        this.identifier = inputSourceIdentifier;
    }

    public List<Holiday> parse(InputStream stream) throws IOException
    {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final List<Holiday> result = new ArrayList<>();

        int n = 0;
        String line;
        while ((line = reader.readLine()) != null)
        {
            n++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#"))
            {
                continue;
            }

            final Holiday holiday = holidayParser.parse(line);
            if (holiday != null)
            {
                result.add(holiday);
            }
            else
            {
                LOG.error("File {}:{}: Couldn't parse '{}'.", identifier, n, line);
                // LOG.error("File " + identifier + ":" + n + ": Couldn't parse
                // '" + line + "'.");
                errors.add(new Error(n, line));
            }
        }
        return result;
    }

    public List<Error> getErrors()
    {
        return errors;
    }

}
