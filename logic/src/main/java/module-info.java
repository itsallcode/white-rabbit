module logic
{
    exports org.itsallcode.whiterabbit.logic.service.scheduling;
    exports org.itsallcode.whiterabbit.logic;
    exports org.itsallcode.whiterabbit.logic.storage;
    exports org.itsallcode.whiterabbit.logic.model.json;
    exports org.itsallcode.whiterabbit.logic.service;
    exports org.itsallcode.whiterabbit.logic.model;

    requires java.json.bind;
    requires org.apache.logging.log4j;
}