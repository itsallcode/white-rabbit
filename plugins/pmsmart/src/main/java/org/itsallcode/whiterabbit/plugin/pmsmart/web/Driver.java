package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Driver implements Closeable
{
    private static final Logger LOG = LogManager.getLogger(Driver.class);

    private final WebDriver webDriver;

    public Driver(WebDriver driver)
    {
        this.webDriver = driver;
    }

    public void get(String url)
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
        final WebDriverWait wait = new WebDriverWait(webDriver, timeout);
        wait.until(condition);
    }

    @Override
    public void close()
    {
        LOG.debug("Close browser window");
        webDriver.close();
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
}
