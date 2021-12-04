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
import org.itsallcode.whiterabbit.plugin.pmsmart.web.WebDriverKey;
import org.openqa.selenium.By;

public class ProjectRow
{
    private static final Logger LOG = LogManager.getLogger(ProjectRow.class);

    private static final int MAX_COMMENT_LENGTH = 250;
    private static final String COMMENT_FILLER = "...";

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
        final String text = format(duration);
        LOG.debug("Enter duration {} ({}) for project {} (row {}) day {}", duration, text, projectId,
                rowIndex, day);
        final Element textField = findCell(day).findElement(By.xpath(".//input"));
        clearAndEnterDuration(textField, text);
    }

    private Element findCell(LocalDate day)
    {
        final int dayOffset = dayOffset(day);
        LOG.debug("Found day offset {} for day {}", dayOffset, day);
        return findCell(dayOffset);
    }

    public void enterComment(LocalDate day, String comment)
    {
        final Element infoButton = findCell(day).findElement(By.xpath(".//img"));
        infoButton.click();

        final Element popup = driver.findElement(By.xpath("//div[@id='MainContent_TimePopup_PW-1']"));
        popup.waitUntilVisible();
        final Element commentField = popup.findElement(By.xpath(
                "//textarea[@id='MainContent_TimePopup_ASPxRoundPanel2_ASPeLookupMemo_ASPxCpMemo_ASPxMemoRemark_I']"));
        commentField.waitUntilClickable();
        commentField.clear();
        final String shortenedComment = shortenComment(comment);
        LOG.debug("Enter comment '{}' for {}", shortenedComment, day);
        commentField.sendKeys(shortenedComment);
        commentField.sendKeys("\t");
        driver.sleep(Duration.ofMillis(200));
        final Element button = popup.findElement(By.xpath("//div[@id='MainContent_TimePopup_BTPopupOK_CD']"));
        button.click();
    }

    static String shortenComment(String comment)
    {
        if (comment.length() <= MAX_COMMENT_LENGTH)
        {
            return comment;
        }
        return comment.substring(0, MAX_COMMENT_LENGTH - COMMENT_FILLER.length()) + COMMENT_FILLER;
    }

    private Element findCell(int dayOffset)
    {
        final int rowPosition = rowIndex + 1;
        final int columnPosition = 5 + dayOffset;
        return driver.findElement(
                By.xpath("//table[@id='MainContent_ASPxCpWbGrid_WeekBookingGrid_DXMainTable']/tbody/tr[position()="
                        + rowPosition + "]/td[position()=" + columnPosition + "]"));
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

    private void clearAndEnterDuration(Element field, String text)
    {
        field.waitUntilVisible();
        field.sendKeys(WebDriverKey.BACKSPACE.toString());
        field.sendKeys(WebDriverKey.BACKSPACE.toString());
        field.sendKeys(WebDriverKey.BACKSPACE.toString());
        field.sendKeys(WebDriverKey.BACKSPACE.toString());
        field.sendKeys(WebDriverKey.BACKSPACE.toString());
        field.sendKeys(text);
        field.sendKeys("\t");
    }

    public String getProjectId()
    {
        return projectId;
    }
}
