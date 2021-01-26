package org.itsallcode.whiterabbit.plugin.pmsmart.web;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Element
{
    private final Driver driver;
    private final WebElement element;

    private Element(Driver driver, WebElement element)
    {
        this.driver = driver;
        this.element = element;
    }

    public void click()
    {
        element.click();
    }

    public void sendKeys(String text)
    {
        element.sendKeys(text);
    }

    public void clear()
    {
        element.clear();
    }

    public String getTagName()
    {
        return element.getTagName();
    }

    public String getAttribute(String name)
    {
        return element.getAttribute(name);
    }

    public String getText()
    {
        return element.getText();
    }

    public boolean isEnabled()
    {
        return element.isEnabled();
    }

    public Element findElement(By by)
    {
        return wrap(driver, element.findElement(by));
    }

    public Optional<Element> findOptionalElement(By by)
    {
        final List<WebElement> elements = element.findElements(by);
        if (elements.size() > 1)
        {
            throw new AssertionError("Expected 1 element but found " + elements.size());
        }
        if (elements.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(wrap(driver, elements.get(0)));
    }

    public Element findChild()
    {
        final List<Element> children = findChildren();
        if (children.size() != 1)
        {
            throw new AssertionError("Expected 1 child but found " + children.size() + ": " + children);
        }
        return children.get(0);
    }

    public List<Element> findChildren()
    {
        return findElements(By.xpath("./*"));
    }

    public Element waitUntilVisible()
    {
        driver.waitUntil(Duration.ofSeconds(1), ExpectedConditions.visibilityOf(element));
        return this;
    }

    public List<Element> findElements(By by)
    {
        return wrap(driver, element.findElements(by));
    }

    static Element wrap(Driver driver, WebElement element)
    {
        return new Element(driver, element);
    }

    static List<Element> wrap(Driver driver, List<WebElement> elements)
    {
        return elements.stream().map(e -> wrap(driver, e)).collect(toList());
    }

    @Override
    public String toString()
    {
        return "Element [element=" + element + "]";
    }
}
