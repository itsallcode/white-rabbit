package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import org.openqa.selenium.edge.EdgeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory
{

    public Driver createWebDriver()
    {
        final WebDriverManager driverManager = WebDriverManager.edgedriver();
        // .browserVersion("87.0.664.75");
        driverManager.setup();

        return new Driver(new EdgeDriver());
    }

}
