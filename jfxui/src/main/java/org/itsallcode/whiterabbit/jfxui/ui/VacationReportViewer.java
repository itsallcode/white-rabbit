package org.itsallcode.whiterabbit.jfxui.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.logic.report.vacation.VacationReport;

public class VacationReportViewer
{
    private static final Logger LOG = LogManager.getLogger(VacationReportViewer.class);

    private final VacationReport report;

    public VacationReportViewer(VacationReport report)
    {
        this.report = report;
    }

    public void show()
    {
        LOG.info(report.months);
        LOG.info(report.years);
    }
}
