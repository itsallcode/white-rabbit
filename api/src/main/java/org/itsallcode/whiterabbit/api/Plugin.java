package org.itsallcode.whiterabbit.api;

public interface Plugin
{
    void init(PluginConfiguration config);

    String getId();

    void close();

    boolean supports(Class<?> featureType);

    <T> T getFeature(Class<T> featureType);
}
