package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class ProjectRow
{
    private static final Logger LOG = LogManager.getLogger(ProjectRow.class);

    private static final String BACKSPACE_CHAR = "\u0008";

    private final Driver driver;
    private final String projectId;
    private final int rowIndex;
    private final WeekViewPage weekViewPage;

    public ProjectRow(Driver driver, String projectId, int rowIndex, WeekViewPage weekViewPage)
    {
        this.driver = driver;
        this.projectId = projectId;
        this.rowIndex = rowIndex;
        this.weekViewPage = weekViewPage;
    }

    public void enterDuration(LocalDate day, Duration duration)
    {
        final int dayOffset = dayOffset(day);
        final String text = format(duration);
        LOG.debug("Enter duration {} ({}) for project {} (row {}) day {}: day offset = {}", duration, text, projectId,
                rowIndex, day, dayOffset);
        final Element dayCell = findCell(dayOffset);
        enterText(dayCell, text);
    }

    private Element findCell(int dayOffset)
    {
        final int rowPosition = rowIndex + 1;
        final int columnPosition = 5 + dayOffset;
        return driver.findElement(
                By.xpath("//table[@id='MainContent_ASPxCpWbGrid_WeekBookingGrid_DXMainTable']/tbody/tr[position()="
                        + rowPosition + "]/td[position()=" + columnPosition + "]//input"));
    }

    private String format(Duration duration)
    {
        final long hours = Math.abs(duration.toHours());
        final int minutes = Math.abs(duration.toMinutesPart());
        return format("{0,number,00}:{1,number,00}", hours, minutes);
    }

    private String format(String pattern, final Object... arguments)
    {
        final MessageFormat temp = new MessageFormat(pattern, Locale.GERMAN);
        return temp.format(arguments);
    }

    private int dayOffset(LocalDate day)
    {
        final Period period = Period.between(weekViewPage.getSelectedWeekFirstDay(), day);
        final int offset = period.getDays();
        if (offset >= 0 && offset < 7)
        {
            return offset;
        }
        throw new IllegalStateException("Got invalid day offset " + offset);
    }

    private void enterText(Element field, String text)
    {
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(text);
        field.sendKeys("\t");
    }

    public String getProjectId()
    {
        return projectId;
    }
}
