module org.itsallcode.whiterabbit.plugin.pmsmarts
{
    exports org.itsallcode.whiterabbit.plugin.pmsmart.web.page;
    exports org.itsallcode.whiterabbit.plugin.pmsmart.web;
    exports org.itsallcode.whiterabbit.plugin.pmsmart;

    requires com.google.common;
    requires org.apache.logging.log4j;
    requires org.itsallcode.whiterabbit.api;
    requires selenium.api;
}