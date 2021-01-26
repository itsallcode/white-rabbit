package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class ProjectTable
{
    private static final Logger LOG = LogManager.getLogger(ProjectTable.class);
    private final Driver driver;
    private final Element table;
    private final WeekViewPage weekViewPage;

    public ProjectTable(Driver driver, Element table, WeekViewPage weekViewPage)
    {
        this.driver = driver;
        this.table = table;
        this.weekViewPage = weekViewPage;
    }

    public Map<String, ProjectRow> getProjects()
    {
        final List<Element> tableRows = table.findElements(By.xpath("./tbody/tr"));
        return convertRows(tableRows).stream()
                .collect(toMap(ProjectRow::getProjectId, Function.identity()));
    }

    private List<ProjectRow> convertRows(final List<Element> tableRows)
    {
        final List<ProjectRow> projectList = new ArrayList<>();
        for (int i = 0; i < tableRows.size(); i++)
        {
            final ProjectRow convertedRow = convert(i, tableRows.get(i));
            if (convertedRow != null)
            {
                projectList.add(convertedRow);
            }
        }
        return projectList;
    }

    private ProjectRow convert(int rowIndex, Element row)
    {
        LOG.info("Converting row {}", row);
        final List<Element> cells = row.findElements(By.xpath("./td"));

        if (cells.size() != 11)
        {
            throw new AssertionError("Expected 11 columns but got " + cells.size());
        }

        final Optional<Element> firstColImage = cells.get(0).findOptionalElement(By.xpath("./img"));
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

        final Optional<String> projectId = cells.get(1).findOptionalElement(By.className("shortNameDiv"))
                .map(Element::getText).filter(s -> !s.isBlank());

        final Optional<String> activityId = cells.get(3).findOptionalElement(By.className("shortNameDiv"))
                .map(Element::getText)
                .filter(s -> !s.isBlank());

        if (activityId.isEmpty() && projectId.isEmpty())
        {
            throw new AssertionError("Both project and activity ids are empty");
        }
        if (activityId.isPresent() && projectId.isPresent())
        {
            throw new AssertionError("Both project and activity ids are present");
        }

        final String rowId = activityId.orElseGet(projectId::get);
        LOG.debug("Found project {}", rowId);

        return new ProjectRow(driver, rowId, rowIndex, weekViewPage);
    }

}
