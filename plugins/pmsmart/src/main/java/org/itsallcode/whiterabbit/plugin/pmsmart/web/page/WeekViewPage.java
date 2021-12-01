package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.TenaciousChecker;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverKey;
import org.openqa.selenium.By;

public class WeekViewPage implements Page
{
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
                + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(day)
                + WebDriverKey.RETURN);
        final TenaciousChecker checker = new TenaciousChecker(() -> isDaySelected(day));
        if (!checker.check(Duration.ofSeconds(5)))
        {
            throw new IllegalStateException("Expected day " + day + " was not selected");
        }
    }

    public boolean isDaySelected(LocalDate day)
    {
        final LocalDate firstWeekDay = getSelectedWeekFirstDay();
        final int diff = Period.between(firstWeekDay, day).getDays();
        final boolean daySelected = diff >= 0 && diff < 7;
        if (!daySelected)
        {
            LOG.debug("Day {} is not selected. First week day: {}, difference: {}", day, firstWeekDay, diff);
        }
        return daySelected;
    }

    public ProjectTable getProjectTable()
    {
        final var table = driver
                .findElement(By.xpath("//*[@id=\"MainContent_ASPxCpWbGrid_WeekBookingGrid_DXMainTable\"]"));
        return new ProjectTable(driver, table, this);
    }

    public LocalDate getSelectedWeekFirstDay()
    {
        final var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final String text = getCurrentWeek();
        final String[] parts = text.split("[\\s-,]");

        final var firstDay = LocalDate.parse(parts[0], formatter);
        final var lastDay = LocalDate.parse(parts[3], formatter);
        LOG.trace("First day: {}, last day: {}", firstDay, lastDay);
        return firstDay;
    }

    private String getCurrentWeek()
    {
        return driver.findElement(By.id("MainContent_ASPxCPWeek_ASPxLblWeek")).waitUntilVisible().getText();
    }

    public void saveWeek()
    {
        LOG.info("Saving current week");
        driver.findElement(By.id("MainContent_BTSubmit")).click();
        driver.sleep(Duration.ofSeconds(1));
    }
}
