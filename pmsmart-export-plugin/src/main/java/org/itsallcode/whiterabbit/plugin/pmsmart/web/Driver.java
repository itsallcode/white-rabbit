package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Driver
{
    private static final Logger LOG = LogManager.getLogger(Driver.class);

    private final WebDriver driver;

    public Driver(WebDriver driver)
    {
        this.driver = driver;
    }

    public void get(String url)
    {
        driver.get(url);
    }

    public String getTitle()
    {
        return driver.getTitle();
    }

    public Element findElement(By by)
    {
        return Element.wrap(this, driver.findElement(by));
    }

    public List<Element> findElements(By by)
    {
        return Element.wrap(this, driver.findElements(by));
    }

    public void waitUntil(Duration timeout, ExpectedCondition<?> condition)
    {
        final WebDriverWait wait = new WebDriverWait(driver, timeout.toSeconds());
        wait.until(condition);
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
