package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class WeekViewPage implements Page
{
    private static final Logger LOG = LogManager.getLogger(WeekViewPage.class);

    private final Driver driver;

    public WeekViewPage(Driver driver)
    {
        this.driver = driver;
    }

    @Override
    public void assertOnPage()
    {
        if (!driver.getTitle().equals("Zeiterfassung - Wochenansicht"))
        {
            throw new IllegalStateException("Not on week view page");
        }
    }

    public void selectWeek(LocalDate day)
    {
        LOG.debug("Selecting week for day {}...", day);

        driver.findElement(By.id("MainContent_EdtDate_B-1")).click();
        final DateSelector dateSelector = new DateSelector(driver,
                driver.findElement(By.id("MainContent_EdtDate_DDD_PW-1")));
        dateSelector.select(day);
        if (!isDaySelected(day))
        {
            throw new IllegalStateException("Expected day " + day + " selected");
        }
    }

    public boolean isDaySelected(LocalDate day)
    {
        final LocalDate firstWeekDay = getSelectedWeekFirstDay();
        final int diff = Period.between(firstWeekDay, day).getDays();
        return diff >= 0 && diff < 7;
    }

    public ProjectTable getProjectTable()
    {
        final Element table = driver
                .findElement(By.xpath("//*[@id=\"MainContent_ASPxCpWbGrid_WeekBookingGrid_DXMainTable\"]"));
        return new ProjectTable(driver, table, this);
    }

    public LocalDate getSelectedWeekFirstDay()
    {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final String text = getCurrentWeek();
        final String[] parts = text.split("[\\s-,]");

        final LocalDate firstDay = LocalDate.parse(parts[0], formatter);
        final LocalDate lastDay = LocalDate.parse(parts[3], formatter);
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
    }
}
