package org.itsallcode.whiterabbit.api;

import java.util.Optional;

import org.itsallcode.whiterabbit.api.features.PluginFeature;

public abstract class AbstractPlugin<S extends PluginFeature> implements Plugin
{
    private final String id;
    private final Class<S> featureType;
    protected PluginConfiguration config;

    protected AbstractPlugin(String id, Class<S> featureType)
    {
        this.featureType = featureType;
        this.id = id;
    }

    @Override
    public void init(PluginConfiguration config)
    {
        this.config = config;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void close()
    {
        // ignore
    }

    @Override
    public boolean supports(Class<? extends PluginFeature> featureType)
    {
        return this.featureType.isAssignableFrom(featureType);
    }

    protected abstract S createInstance();

    @Override
    public <T extends PluginFeature> Optional<T> getFeature(Class<T> featureType)
    {
        if (this.supports(featureType))
        {
            return Optional.of(featureType.cast(createInstance()));
        }
        return Optional.empty();
    }
}
