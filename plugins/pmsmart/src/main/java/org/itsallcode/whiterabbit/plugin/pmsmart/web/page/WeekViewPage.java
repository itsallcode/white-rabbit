package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverKey;
import org.openqa.selenium.By;

public class WeekViewPage implements Page
{
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String EXPECTED_PAGE_TITLE = "Zeiterfassung - Wochenansicht";
    private static final Logger LOG = LogManager.getLogger(WeekViewPage.class);

    private final Driver driver;

    public WeekViewPage(Driver driver)
    {
        this.driver = driver;
    }

    @Override
    public void assertOnPage()
    {
        final String title = driver.getTitle();
        if (!title.equals(EXPECTED_PAGE_TITLE))
        {
            throw new IllegalStateException(
                    "Not on week view page. Expected title '" + EXPECTED_PAGE_TITLE + "' but was '" + title + "'");
        }
    }

    public void selectWeek(LocalDate day)
    {
        LOG.debug("Selecting day {}...", day);

        final Element el = driver.findElement(By.id("MainContent_EdtDate_I"));
        el.click();
        el.sendKeys(WebDriverKey.END
                + WebDriverKey.BACKSPACE.repeat(20)
                + DateTimeFormatter.ofPattern(DATE_FORMAT).format(day)
                + WebDriverKey.RETURN);

        final String expected = expectedWeekDisplay(day);
        final boolean result = driver.tenaciousCheck(Duration.ofSeconds(5),
                () -> expected.equals(getDisplayedWeek()));
        if (!result)
        {
            throw new IllegalStateException("Expected day " + day + " but selected was " + getDisplayedWeek());
        }
    }

    public boolean isDaySelected(LocalDate day)
    {
        return expectedWeekDisplay(day).equals(getDisplayedWeek());
    }

    public ProjectTable getProjectTable()
    {
        final var table = driver
                .findElement(By.xpath("//*[@id=\"MainContent_ASPxCpWbGrid_WeekBookingGrid_DXMainTable\"]"));
        return new ProjectTable(driver, table, this);
    }

    public LocalDate getSelectedWeekFirstDay()
    {
        final var formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        final String text = getDisplayedWeek();
        final String[] parts = text.split("[\\s-,]");

        final var firstDay = LocalDate.parse(parts[0], formatter);
        final var lastDay = LocalDate.parse(parts[3], formatter);
        LOG.trace("First day: {}, last day: {}", firstDay, lastDay);
        return firstDay;
    }

    private String getDisplayedWeek()
    {
        return driver.findElement(By.id("MainContent_ASPxCPWeek_ASPxLblWeek")).waitUntilVisible().getText();
    }

    private String expectedWeekDisplay(LocalDate day)
    {
        // span with id="MainContent_ASPxCPWeek_ASPxLblWeek" displays
        // 29.11.2021 - 05.12.2021, KW 48
        final LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        final LocalDate sunday = monday.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        final DateTimeFormatter week = DateTimeFormatter.ofPattern("w").withLocale(Locale.GERMANY);
        return String.format("%s - %s, KW %s", formatter.format(monday), formatter.format(sunday),
                week.format(monday));
    }

    public void saveWeek()
    {
        LOG.info("Saving current week");
        driver.findElement(By.id("MainContent_BTSubmit")).click();
        driver.sleep(Duration.ofSeconds(1));
    }
}
