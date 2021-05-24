package org.itsallcode.whiterabbit.logic.holidays;

import java.time.LocalDate;

public class GaussEasterCalculator
{
    /**
     * source
     * https://de.wikibooks.org/wiki/Algorithmensammlung:_Kalender:_Feiertage/
     * 
     * <p>
     * Note:
     * https://www.geeksforgeeks.org/how-to-calculate-the-easter-date-for-a-given-year-using-gauss-algorithm/
     * yields different values.
     */
    public static LocalDate calculate(int year)
    {
        final int x = year;
        int k; // secular number
        int m; // secular moon correction
        int s; // secular sun correction
        int a; // moon parameter
        int d; // seed for first full moon in spring
        int r; // calendar correction
        int eb; // Easter boundary
        int sz; // first Sunday in March
        int eo; // offset of Easter Sunday to Easter boundary
        int es; // Easter Sunday as day number of March (March 32nd = April 1st)
        int easterDay;
        int easterMonth;

        k = x / 100;
        m = 15 + (3 * k + 3) / 4 - (8 * k + 13) / 25;
        s = 2 - (3 * k + 3) / 4;
        a = x % 19;
        d = (19 * a + m) % 30;
        r = (d + a / 11) / 29;
        eb = 21 + d - r;
        sz = 7 - (x + x / 4 + s) % 7;
        eo = 7 - (eb - sz) % 7;
        es = eb + eo;

        easterMonth = 2 + (es + 30) / 31;
        easterDay = es - 31 * (easterMonth / 4);

        return LocalDate.of(year, easterMonth, easterDay);
    }
}