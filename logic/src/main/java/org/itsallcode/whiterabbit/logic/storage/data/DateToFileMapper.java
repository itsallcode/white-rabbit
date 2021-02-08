package org.itsallcode.whiterabbit.logic.storage.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DateToFileMapper
{
    private static final Logger LOG = LogManager.getLogger(DateToFileMapper.class);

    private final Path dataDir;
    private final DateTimeFormatter formatter;
    private final Pattern fileNamePattern = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)\\.json$");

    DateToFileMapper(Path dataDir)
    {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.ENGLISH);
        this.dataDir = dataDir;
    }

    Path getPathForDate(YearMonth date)
    {
        return dataDir.resolve(String.valueOf(date.getYear())).resolve(getFileName(date));
    }

    Path getLegacyPathForDate(YearMonth date)
    {
        return dataDir.resolve(getFileName(date));
    }

    private String getFileName(YearMonth date)
    {
        return date.format(formatter) + ".json";
    }

    Stream<Path> getAllFiles()
    {
        LOG.debug("Reading all files in {}", dataDir);
        try
        {
            return Files.walk(dataDir) //
                    .filter(file -> !file.toFile().isDirectory()) //
                    .filter(file -> file.getFileName().toString().endsWith(".json"));
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error listing directory " + dataDir, e);
        }
    }

    Stream<YearMonth> getAllYearMonths()
    {
        return getAllFiles() //
                .map(Path::getFileName) //
                .map(Path::toString) //
                .map(this::parseYearMonth) //
                .filter(Objects::nonNull);
    }

    private YearMonth parseYearMonth(String filename)
    {
        final Matcher matcher = fileNamePattern.matcher(filename);
        if (!matcher.matches())
        {
            return null;
        }
        final int year = Integer.parseInt(matcher.group(1));
        final int month = Integer.parseInt(matcher.group(2));
        return YearMonth.of(year, month);
    }
}
