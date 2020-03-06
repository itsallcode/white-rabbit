package org.itsallcode.whiterabbit.jfxui;

import org.itsallcode.whiterabbit.logic.service.vacation.VacationReport;

public class VacationReportViewer
{
    private final VacationReport report;

    public VacationReportViewer(VacationReport report)
    {
        this.report = report;
    }

    public void show()
    {
        System.out.println(report.months);
        System.out.println(report.years);
    }
}
