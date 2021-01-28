package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class DateSelector
{
    private final Driver driver;
    private final Element selector;

    public DateSelector(Driver driver, Element selector)
    {
        this.driver = driver;
        this.selector = selector;
    }

    public void select(LocalDate day)
    {
        selectMonth(YearMonth.from(day));
        selector.findElement(By.xpath("//td[not(contains(@class, 'dxeCalendarOtherMonth_PlasticBlue')) and text()='"
                + day.getDayOfMonth() + "']")).click();
        driver.sleep(Duration.ofSeconds(1));
    }

    private void selectMonth(YearMonth month)
    {
        final String selectedMonthText = selector.findElement(By.id("MainContent_EdtDate_DDD_C_T")).waitUntilVisible()
                .getText();
        final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN);
        final YearMonth selectedMonth = YearMonth.parse(selectedMonthText, monthFormatter);
        if (!month.equals(selectedMonth))
        {
            throw new IllegalStateException("Expected month " + month + " but " + selectedMonth + " is selected");
        }
    }
}
