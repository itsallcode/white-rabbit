package org.itsallcode.whiterabbit.logic.service.holidays;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.itsallcode.whiterabbit.api.features.Holidays.HolidayInstance;
import org.itsallcode.whiterabbit.api.model.DayData;
import org.itsallcode.whiterabbit.logic.storage.data.JsonModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HolidayAggregatorTest
{
    private static final YearMonth YEAR_MONTH = YearMonth.of(2021, Month.MAY);

    @Mock
    Holidays holidayProvider1;
    @Mock
    Holidays holidayProvider2;

    @Mock
    HolidayInstance h1;
    @Mock
    HolidayInstance h2;
    @Mock
    HolidayInstance h3;
    @Mock
    HolidayInstance h4;

    @Test
    void test()
    {
        final LocalDate D1 = YEAR_MONTH.atDay(3);
        final LocalDate D2 = YEAR_MONTH.atDay(1);

        when(h1.getName()).thenReturn("h1");
        when(h2.getName()).thenReturn("h2");
        when(h3.getName()).thenReturn("h3");
        when(h4.getName()).thenReturn("h4");

        when(holidayProvider1.getHolidays(D1)).thenReturn(createHolidays(h1, h2));
        when(holidayProvider1.getHolidays(not(eq(D1)))).thenReturn(Collections.emptyList());
        when(holidayProvider2.getHolidays(D1)).thenReturn(createHolidays(h3));
        when(holidayProvider2.getHolidays(D2)).thenReturn(createHolidays(h4));
        when(holidayProvider2.getHolidays(and(not(eq(D1)), not(eq(D2))))).thenReturn(Collections.emptyList());

        final HolidayAggregator aggregator = new HolidayAggregator();
        aggregator.collect(holidayProvider1, YEAR_MONTH);
        aggregator.collect(holidayProvider2, YEAR_MONTH);

        final List<DayData> days = aggregator.createDayData(new JsonModelFactory());
        assertThat(days.get(0).getComment()).isEqualTo("h4");
        assertThat(days.get(1).getComment()).isEqualTo("h1, h2, h3");
    }

    private List<HolidayInstance> createHolidays(HolidayInstance... instances)
    {
        final List<HolidayInstance> result = new ArrayList<>();
        result.addAll(asList(instances));
        return result;
    }
}
