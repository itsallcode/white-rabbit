package org.itsallcode.whiterabbit.logic.storage.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DateToFileMapperTest
{
    private DateToFileMapper mapper;

    @BeforeEach
    void setUp(@TempDir Path tempDir)
    {
        mapper = new DateToFileMapper(tempDir);
    }

    @Test
    void testGetPathForDate(@TempDir Path tempDir)
    {
        final Path path = mapper.getPathForDate(YearMonth.of(2019, Month.MAY));
        assertThat(path).isEqualTo(tempDir.resolve("2019/2019-05.json"));
    }

    @Test
    void testGetLegacyPathForDate(@TempDir Path tempDir)
    {
        final Path path = mapper.getLegacyPathForDate(YearMonth.of(2019, Month.MAY));
        assertThat(path).isEqualTo(tempDir.resolve("2019-05.json"));
    }

    @Test
    void testGetAllFilesDirDoesNotExist()
    {
        assertThrows(UncheckedIOException.class, new DateToFileMapper(Paths.get("notExistingDir"))::getAllFiles);
    }

    @Test
    void testGetAllFilesDirEmptyDir()
    {
        assertThat(mapper.getAllFiles()).isEmpty();
    }

    @Test
    void testGetAllFilesDirInvalidFiles(@TempDir Path tempDir) throws IOException
    {
        Files.createDirectory(tempDir.resolve("ignored-dir"));
        Files.createFile(tempDir.resolve("non-json-file.txt"));
        Files.createFile(tempDir.resolve("Upper-case-suffix.JSON"));
        assertThat(mapper.getAllFiles()).isEmpty();
    }

    @Test
    void testGetAllFilesDirValidFiles(@TempDir Path tempDir) throws IOException
    {
        final Path file1 = tempDir.resolve("file1.json");
        Files.createFile(file1);
        assertThat(mapper.getAllFiles()).hasSize(1).contains(file1);
    }

    @Test
    void testGetAllFilesDirValidFilesInSubDir(@TempDir Path tempDir) throws IOException
    {
        final Path subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        final Path file1 = subDir.resolve("file1.json");
        Files.createFile(file1);
        assertThat(mapper.getAllFiles()).hasSize(1).contains(file1);
    }

    @Test
    void testGetAllYearMonthDirDoesNotExist()
    {
        assertThrows(UncheckedIOException.class, new DateToFileMapper(Paths.get("notExistingDir"))::getAllYearMonths);
    }

    @Test
    void testGetAllYearMonthDirEmptyDir()
    {
        assertThat(mapper.getAllYearMonths()).isEmpty();
    }

    @Test
    void testGetAllYearMonthDirInvalidFiles(@TempDir Path tempDir) throws IOException
    {
        Files.createDirectory(tempDir.resolve("ignored-dir"));
        Files.createFile(tempDir.resolve("non-json-file.txt"));
        Files.createFile(tempDir.resolve("Upper-case-suffix.JSON"));
        Files.createFile(tempDir.resolve("wrong-name.json"));
        assertThat(mapper.getAllYearMonths()).isEmpty();
    }

    @Test
    void testGetAllYearMonthDirValidFiles(@TempDir Path tempDir) throws IOException
    {
        Files.createFile(tempDir.resolve("2019-05.json"));
        assertThat(mapper.getAllYearMonths()).hasSize(1).contains(YearMonth.of(2019, Month.MAY));
    }

    @Test
    void testGetAllYearMonthDirValidFilesInSubDir(@TempDir Path tempDir) throws IOException
    {
        final Path subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        Files.createFile(subDir.resolve("2019-05.json"));
        assertThat(mapper.getAllYearMonths()).hasSize(1).contains(YearMonth.of(2019, Month.MAY));
    }
}
