package org.itsallcode.whiterabbit.plugin.pmsmart.web.page;

import org.itsallcode.whiterabbit.plugin.pmsmart.web.Driver;
import org.itsallcode.whiterabbit.plugin.pmsmart.web.Element;
import org.openqa.selenium.By;

public class CreateTimeBookingDialog
{
    private final Driver driver;
    private final Element dialog;

    public CreateTimeBookingDialog(Driver driver, Element element)
    {
        this.driver = driver;
        this.dialog = element;
    }

    public CreateTimeBookingDialog selectBookingType(BookingType bookingType)
    {
        if (bookingType == BookingType.PROJECT)
        {
            return this;
        }
        dialog.findElement(By.id(bookingType.id)).waitUntilVisible().click();
        return this;
    }

    public CreateTimeBookingDialog enterProject(String project)
    {
        dialog.findElement(By.id(
                "MainContent_ASPxPopTimeBookingDialog_ASPeTbdEditList_ASPxCPProject_ASPxRPProject_CobProject_B-1Img"))
                .waitUntilVisible()
                .click();
        final Element field = dialog.findElement(
                By.id("MainContent_ASPxPopTimeBookingDialog_ASPeTbdEditList_ASPxCPProject_ASPxRPProject_CobProject_I"));
        field.waitUntilVisible().click();
        field.sendKeys(project);
        return this;
    }

    public CreateTimeBookingDialog filterProject()
    {
        dialog.findElement(By.id(
                "MainContent_ASPxPopTimeBookingDialog_ASPeTbdEditList_ASPxCPProject_ASPxRPProject_ASPxImgFilterProject"))
                .click();
        return this;
    }

    public enum BookingType
    {
        PROJECT("MainContent_ASPxPopTimeBookingDialog_ASPeTbdEditList_ASPxRPBookingType_ASPxRbBookingType_RB0_I"), INTERNAL(
                "MainContent_ASPxPopTimeBookingDialog_ASPeTbdEditList_ASPxRPBookingType_ASPxRbBookingType_RB1_I");

        private final String id;

        BookingType(String id)
        {
            this.id = id;
        }
    }
}
