package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import java.util.List;

import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class ListViewPage implements Page
{
    private final Driver driver;

    public ListViewPage(Driver driver)
    {
        this.driver = driver;
    }

    @Override
    public void assertOnPage()
    {
        if (!driver.getTitle().equals("Zeiterfassung - Listenansicht"))
        {
            throw new IllegalStateException("Not on list view page");
        }
    }

    public CreateTimeBookingDialog openCreateDialog()
    {
        List<Element> popups = findPopups();
        if (popups.isEmpty())
        {
            driver.findElement(By.xpath("//*[@title='Neuen Eintrag erstellen']")).click();
        }
        popups = findPopups();
        if (popups.size() == 1)
        {
            return new CreateTimeBookingDialog(driver, popups.get(0));
        }
        throw new IllegalStateException("Create time booking dialog not found");
    }

    private List<Element> findPopups()
    {
        return driver.findElements(By.xpath("//*[@id=\"MainContent_ASPxPopTimeBookingDialog_PW-1\"]"));
    }
}
