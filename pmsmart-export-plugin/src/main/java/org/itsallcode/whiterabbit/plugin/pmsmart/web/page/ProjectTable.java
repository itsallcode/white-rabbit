package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class ProjectTable
{
    private static final Logger LOG = LogManager.getLogger(ProjectTable.class);
    private static final String BACKSPACE_CHAR = "\u0008";
    private final Driver driver;
    private final Element table;
    private final WeekViewPage weekViewPage;

    public ProjectTable(Driver driver, Element table, WeekViewPage weekViewPage)
    {
        this.driver = driver;
        this.table = table;
        this.weekViewPage = weekViewPage;
    }

    public List<ProjectRow> getRows()
    {
        final List<Element> tableRows = table.findElements(By.xpath("./tbody/tr"));
        final LocalDate firstWeekDay = weekViewPage.getSelectedWeekFirstDay();

        final List<LocalDate> days = tableRows.get(1).findChildren().stream().skip(4).map(e -> e.getText().trim())
                .map(label -> convertColumnLabelToDate(label, firstWeekDay)).collect(toList());
        System.out.println(days);

        return tableRows.stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private LocalDate convertColumnLabelToDate(String label, LocalDate firstWeekDay)
    {
        final String[] parts = label.split("[\\s,]");
        final int day = Integer.parseInt(parts[2]);
        if (firstWeekDay.getDayOfMonth() <= day)
        {
            return firstWeekDay.withDayOfMonth(day);
        }
        return firstWeekDay.withDayOfMonth(day).plusMonths(1);
    }

    private ProjectRow convert(Element row)
    {
        LOG.info("Converting row {}", row);
        final List<Element> columns = row.findElements(By.xpath("./td"));

        if (columns.size() != 11)
        {
            throw new AssertionError("Expected 11 columns but got " + columns.size());
        }

        final Optional<Element> firstColImage = columns.get(0).findOptionalElement(By.xpath("./img"));
        if (firstColImage.isEmpty())
        {
            LOG.trace("Skip row, now img");
            return null;
        }

        final String title = firstColImage.get().getAttribute("title").trim();
        if (!title.equals("Von der Favoritenliste entfernen"))
        {
            LOG.trace("Skip row, wrong img title: {}", title);
            return null;
        }

        final Optional<String> projectId = columns.get(1).findOptionalElement(By.className("shortNameDiv"))
                .map(Element::getText).filter(s -> !s.isBlank());

        final Optional<String> activityId = columns.get(3).findOptionalElement(By.className("shortNameDiv"))
                .map(Element::getText)
                .filter(s -> !s.isBlank());

        if (activityId.isEmpty() && projectId.isEmpty())
        {
            throw new AssertionError("Both project and activity ids are empty");
        }
        LOG.debug("Found project id {} / activity id {}", projectId, activityId);

        return new ProjectRow();
    }

    public void enter(String string)
    {
        final Element field = table
                .findElement(By.id("MainContent_ASPxCpWbGrid_WeekBookingGrid_cell5_4_Row_2_Col_Monday_5_I"));
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(BACKSPACE_CHAR);
        field.sendKeys(string);
        field.sendKeys("\t");
    }
}
