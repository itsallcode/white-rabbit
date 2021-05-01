package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.edge.EdgeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory
{
    private static final Logger LOG = LogManager.getLogger(WebDriverFactory.class);

    public Driver createWebDriver(String baseUrl)
    {
        WebDriverManager.edgedriver().setup();
        final var classLoader = getClass().getClassLoader();
        LOG.debug("Using context class loader {} for selenium http client", classLoader);
        Thread.currentThread().setContextClassLoader(classLoader);
        final var driver = new EdgeDriver();
        return new Driver(driver, baseUrl);
    }
}
