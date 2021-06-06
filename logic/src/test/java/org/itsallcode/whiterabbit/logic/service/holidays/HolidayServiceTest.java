package org.itsallcode.whiterabbit.logic.service.holidays;

import static org.mockito.Mockito.verify;

import java.time.YearMonth;
import java.util.Arrays;

import org.itsallcode.whiterabbit.api.features.Holidays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest
{
    static final YearMonth DATE = YearMonth.of(2021, 05);

    @Mock
    Holidays provider1;

    @Mock
    Holidays provider2;

    @Test
    void test()
    {
        final HolidayService holidayService = new HolidayService(Arrays.asList(provider1, provider2));

        holidayService.getHolidays(null, DATE);

        final int days = DATE.atEndOfMonth().getDayOfMonth();
        for (int day = 1; day <= days; day++)
        {
            verify(provider1).getHolidays(DATE.atDay(day));
            verify(provider2).getHolidays(DATE.atDay(day));
        }
    }
}
