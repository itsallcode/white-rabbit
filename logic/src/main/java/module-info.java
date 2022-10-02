module org.itsallcode.whiterabbit.logic
{
    exports org.itsallcode.whiterabbit.logic.storage;
    exports org.itsallcode.whiterabbit.logic.model.json;
    exports org.itsallcode.whiterabbit.logic.service;
    exports org.itsallcode.whiterabbit.logic.service.scheduling;
    exports org.itsallcode.whiterabbit.logic;
    exports org.itsallcode.whiterabbit.logic.autocomplete;
    exports org.itsallcode.whiterabbit.logic.report.vacation;
    exports org.itsallcode.whiterabbit.logic.service.project;
    exports org.itsallcode.whiterabbit.logic.service.plugin.origin;
    exports org.itsallcode.whiterabbit.logic.model;
    exports org.itsallcode.whiterabbit.logic.service.plugin;
    exports org.itsallcode.whiterabbit.logic.service.contract;
    exports org.itsallcode.whiterabbit.logic.storage.data;
    exports org.itsallcode.whiterabbit.logic.report.project;
    exports org.itsallcode.whiterabbit.logic.service.singleinstance;

    requires jakarta.json.bind;
    requires org.apache.logging.log4j;
    requires org.eclipse.yasson;
    requires org.itsallcode.whiterabbit.api;
}