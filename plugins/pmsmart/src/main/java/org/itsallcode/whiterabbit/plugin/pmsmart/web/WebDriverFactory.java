package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import org.openqa.selenium.edge.EdgeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverFactory
{
    public Driver createWebDriver()
    {
        WebDriverManager.edgedriver().setup();
        return new Driver(new EdgeDriver());
    }
}
