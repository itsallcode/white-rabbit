package org.itsallcode.whiterabbit.logic.storage.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.itsallcode.whiterabbit.api.model.MonthData;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsonFileStorageTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2020, Month.NOVEMBER);
    @TempDir
    Path tempDir;
    @Mock
    DateToFileMapper dateToFileMapperMock;

    JsonFileStorage jsonFileStorage;
    Jsonb jsonb;

    @BeforeEach
    void setUp()
    {
        jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(false));
        jsonFileStorage = new JsonFileStorage(jsonb, dateToFileMapperMock);
    }

    @Test
    void sortsAvailableYearMonths()
    {
        final YearMonth april2020 = YearMonth.of(2020, Month.APRIL);
        final YearMonth january2020 = YearMonth.of(2020, Month.JANUARY);
        final YearMonth december2019 = YearMonth.of(2019, Month.DECEMBER);
        when(dateToFileMapperMock.getAllYearMonths()).thenReturn(Stream.of(april2020, january2020, december2019));
        assertThat(jsonFileStorage.getAvailableDataMonths()).containsExactly(december2019, january2020, april2020);
    }

    @Test
    void returnsEmptyAvailableMonths()
    {
        when(dateToFileMapperMock.getAllYearMonths()).thenReturn(Stream.empty());
        assertThat(jsonFileStorage.getAvailableDataMonths()).isEmpty();
    }

    @Test
    void loadMonthReturnsEmptyOptional_WhenLegacyFileDoesNotExist()
    {
        when(dateToFileMapperMock.getPathForDate(YEAR_MONTH)).thenReturn(tempDir.resolve("does-not-exist"));
        when(dateToFileMapperMock.getLegacyPathForDate(YEAR_MONTH)).thenReturn(tempDir.resolve("does-not-exist"));
        assertThat(jsonFileStorage.load(YEAR_MONTH)).isEmpty();
    }

    @Test
    void loadMonthReturnsMonth_WhenLegacyFileExists() throws IOException
    {
        final MonthData month = new JsonMonth();
        month.setYear(2020);
        final Path file = writeTempFile(month);

        when(dateToFileMapperMock.getPathForDate(YEAR_MONTH)).thenReturn(tempDir.resolve("does-not-exist"));
        when(dateToFileMapperMock.getLegacyPathForDate(YEAR_MONTH)).thenReturn(file);
        final Optional<MonthData> loadedMonth = jsonFileStorage.load(YEAR_MONTH);
        assertThat(loadedMonth).isNotEmpty();
        assertThat(loadedMonth.get().getYear()).isEqualTo(2020);
    }

    @Test
    void loadMonthReturnsMonth_WhenFileExists() throws IOException
    {
        final MonthData month = new JsonMonth();
        month.setYear(2020);
        final Path file = writeTempFile(month);

        when(dateToFileMapperMock.getPathForDate(YEAR_MONTH)).thenReturn(file);
        final Optional<MonthData> loadedMonth = jsonFileStorage.load(YEAR_MONTH);
        assertThat(loadedMonth).isNotEmpty();
        assertThat(loadedMonth.get().getYear()).isEqualTo(2020);
    }

    @Test
    void writeToFile_writesFile() throws IOException
    {
        final Path file = createTempFile();
        when(dateToFileMapperMock.getPathForDate(YEAR_MONTH)).thenReturn(file);

        final MonthData month = new JsonMonth();
        month.setYear(2020);
        jsonFileStorage.store(YEAR_MONTH, month);

        assertThat(file).exists().hasContent("{\"year\":2020}");
    }

    @Test
    void writeToFile_createsDirectory() throws IOException
    {
        final Path file = tempDir.resolve("sub-dir1").resolve("sub-dir2").resolve("file.json");
        when(dateToFileMapperMock.getPathForDate(YEAR_MONTH)).thenReturn(file);

        final MonthData month = new JsonMonth();
        month.setYear(2020);
        jsonFileStorage.store(YEAR_MONTH, month);

        assertThat(file).exists().hasContent("{\"year\":2020}");
    }

    @Test
    void loadAll_returnsEmptyList_WhenNoFileExists()
    {
        when(dateToFileMapperMock.getAllFiles()).thenReturn(Stream.empty());
        assertThat(jsonFileStorage.loadAll()).isEmpty();
    }

    @Test
    void loadAll_failsWhenFileDoesNotExist()
    {
        final Path notExistingFile = tempDir.resolve("does-not-exist");
        when(dateToFileMapperMock.getAllFiles()).thenReturn(Stream.of(notExistingFile));
        assertThatThrownBy(() -> jsonFileStorage.loadAll())
                .isInstanceOf(UncheckedIOException.class)
                .hasMessage("Error reading file " + notExistingFile);
    }

    @Test
    void loadAll_returnsFiles() throws IOException
    {
        final Path file1 = tempDir.resolve("file1.json");
        final Path file2 = tempDir.resolve("file2.json");
        when(dateToFileMapperMock.getAllFiles()).thenReturn(Stream.of(file1, file2));

        final MonthData month1 = month(2019, Month.DECEMBER);
        final MonthData month2 = month(2020, Month.JANUARY);
        writeMonth(month1, file1);
        writeMonth(month2, file2);

        final List<MonthData> months = jsonFileStorage.loadAll();
        assertThat(months).hasSize(2);

        assertMonth(months.get(0), month1);
        assertMonth(months.get(1), month2);
    }

    @Test
    void loadAll_sortsByYearMonth() throws IOException
    {
        final Path file1 = tempDir.resolve("file1.json");
        final Path file2 = tempDir.resolve("file2.json");
        final Path file3 = tempDir.resolve("file3.json");
        when(dateToFileMapperMock.getAllFiles()).thenReturn(Stream.of(file3, file2, file1));

        final MonthData month1 = month(2019, Month.DECEMBER);
        final MonthData month2 = month(2020, Month.JANUARY);
        final MonthData month3 = month(2020, Month.APRIL);
        writeMonth(month1, file1);
        writeMonth(month2, file2);
        writeMonth(month3, file3);

        final List<MonthData> months = jsonFileStorage.loadAll();
        assertThat(months).hasSize(3);

        assertMonth(months.get(0), month1);
        assertMonth(months.get(1), month2);
        assertMonth(months.get(2), month3);
    }

    private void assertMonth(MonthData actual, MonthData expected)
    {
        assertAll(() -> assertThat(actual.getYear()).isEqualTo(expected.getYear()),
                () -> assertThat(actual.getMonth()).isEqualTo(expected.getMonth()));
    }

    private MonthData month(int year, Month month)
    {
        final MonthData jsonMonth = new JsonMonth();
        jsonMonth.setYear(year);
        jsonMonth.setMonth(month);
        return jsonMonth;
    }

    private Path writeTempFile(MonthData month) throws IOException
    {
        final Path file = createTempFile();
        writeMonth(month, file);
        return file;
    }

    private void writeMonth(MonthData month, final Path file) throws IOException
    {
        Files.writeString(file, jsonb.toJson(month));
    }

    private Path createTempFile() throws IOException
    {
        return Files.createTempFile(tempDir, "month", ".json");
    }
}
