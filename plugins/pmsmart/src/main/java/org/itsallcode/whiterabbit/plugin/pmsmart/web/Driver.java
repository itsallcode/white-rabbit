package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.page.WeekViewPage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Driver implements Closeable
{
    static final int NUMBER_OF_TENACIOUS_CHECKS = 10;
    private static final Logger LOG = LogManager.getLogger(Driver.class);

    private final WebDriver webDriver;
    private final String baseUrl;

    public Driver(WebDriver driver, String baseUrl)
    {
        this.webDriver = driver;
        this.baseUrl = baseUrl;
    }

    public String getTitle()
    {
        return webDriver.getTitle();
    }

    public Element findElement(By by)
    {
        return Element.wrap(this, webDriver.findElement(by));
    }

    public List<Element> findElements(By by)
    {
        return Element.wrap(this, webDriver.findElements(by));
    }

    public void waitUntil(Duration timeout, ExpectedCondition<?> condition)
    {
        new WebDriverWait(webDriver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(condition);
    }

    public boolean tenaciousCheck(Duration maxDuration, Supplier<Boolean> condition)
    {
        final Duration interval = maxDuration.dividedBy(NUMBER_OF_TENACIOUS_CHECKS);
        for (int i = 0; i < NUMBER_OF_TENACIOUS_CHECKS; i++)
        {
            if (Boolean.TRUE.equals(condition.get()))
            {
                LOG.debug("Condition was met after {} ms.", interval.multipliedBy(i).toMillis());
                return true;
            }
            sleep(interval);
        }
        return false;
    }

    @Override
    public void close()
    {
        LOG.debug("Close browser window");
        webDriver.quit();
    }

    public void sleep(Duration duration)
    {
        try
        {
            Thread.sleep(duration.toMillis());
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException();
        }
    }

    public WeekViewPage getWeekViewPage()
    {
        get(baseUrl + "/Pages/TimeTracking/TimeBookingWeek.aspx");
        final var weekViewPage = new WeekViewPage(this);
        weekViewPage.assertOnPage();
        return weekViewPage;
    }

    private void get(String url)
    {
        LOG.debug("Getting URL '{}'", url);
        try
        {
            webDriver.get(url);
        }
        catch (final WebDriverException e)
        {
            throw new IllegalStateException("Error getting URL '" + url + "': " + e.getMessage(), e);
        }
    }
}
