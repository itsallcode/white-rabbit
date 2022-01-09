package org.itsallcode.whiterabbit.api;

import java.util.Optional;

import org.itsallcode.whiterabbit.api.features.PluginFeature;

/**
 * Base class for plugins that support a defined {@link PluginFeature}.
 * 
 * @param <S>
 *            the supported {@link PluginFeature}.
 */
public abstract class AbstractPlugin<S extends PluginFeature> implements Plugin
{
    private final String id;
    private final Class<S> featureType;

    /**
     * The plugin configuration. Will be {@code null} until
     * {@link #init(PluginConfiguration)} was called.
     */
    protected PluginConfiguration config;

    /**
     * Constructor.
     * 
     * @param id
     *            the id of the new plugin.
     * @param featureType
     *            the {@link PluginFeature} that the new plugin supports.
     */
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
    public final String getId()
    {
        return id;
    }

    @Override
    public void close()
    {
        // ignore
    }

    @Override
    public final boolean supports(Class<? extends PluginFeature> featureType)
    {
        return this.featureType.isAssignableFrom(featureType);
    }

    /**
     * Plugins must implement this and return a new instance of the
     * {@link PluginFeature} type.
     * 
     * @return a new {@link PluginFeature} instance.
     */
    protected abstract S createInstance();

    @Override
    public final <T extends PluginFeature> Optional<T> getFeature(Class<T> featureType)
    {
        if (this.supports(featureType))
        {
            return Optional.of(featureType.cast(createInstance()));
        }
        return Optional.empty();
    }
}
