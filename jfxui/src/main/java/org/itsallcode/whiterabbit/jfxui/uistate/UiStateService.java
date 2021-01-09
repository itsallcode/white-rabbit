package org.itsallcode.whiterabbit.jfxui.uistate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.uistate.model.UiStateModel;
import org.itsallcode.whiterabbit.jfxui.uistate.widgets.StateManagerRegistry;
import org.itsallcode.whiterabbit.jfxui.uistate.widgets.WidgetStateManager;
import org.itsallcode.whiterabbit.logic.Config;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class UiStateService
{
    private static final Logger LOG = LogManager.getLogger(UiStateService.class);

    private final Jsonb jsonb;
    private final UiStateModel state;
    private final Config config;

    private final StateManagerRegistry widgetRegistry;

    private UiStateService(UiStateModel state, Config config, StateManagerRegistry widgetRegistry, Jsonb jsonb)
    {
        this.widgetRegistry = widgetRegistry;
        this.jsonb = jsonb;
        this.config = config;
        this.state = state;
    }

    public static UiStateService loadState(Config config)
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return new UiStateService(loadState(jsonb, config.getUiStatePath()), config, StateManagerRegistry.create(), jsonb);
    }

    private static UiStateModel loadState(Jsonb jsonb, Path path)
    {
        if (Files.exists(path))
        {
            try (InputStream inputStream = Files.newInputStream(path))
            {
                final UiStateModel state = jsonb.fromJson(inputStream, UiStateModel.class);
                LOG.info("Loaded ui-state from {}", path);
                LOG.info("State: {}", state);
                return state;
            }
            catch (final IOException | JsonbException e)
            {
                LOG.warn("Error loading ui-state from " + path + ": use default values", e);
            }
        }
        LOG.info("Using fresh ui-state");
        return new UiStateModel();
    }

    public void persistState()
    {
        LOG.info("Writing ui-state {}", state);
        try (OutputStream outputStream = Files.newOutputStream(config.getUiStatePath());)
        {
            jsonb.toJson(state, outputStream);
        }
        catch (final IOException e)
        {
            throw new UncheckedIOException("Error writing ui state to " + config.getUiStatePath(), e);
        }
    }

    public void register(String id, Stage node)
    {
        register(state.stages, id, node);
    }

    public void register(TableView<?> node)
    {
        register(state.tables, node.getId(), node);
    }

    public void register(TreeTableView<?> node)
    {
        register(state.tables, node.getId(), node);
    }

    public void register(SplitPane node)
    {
        register(state.splitPanes, node.getId(), node);
    }

    private <T, M> void register(Map<String, M> models, String id, T widget)
    {
        @SuppressWarnings("unchecked")
        final WidgetStateManager<T, M> manager = (WidgetStateManager<T, M>) widgetRegistry.getManager(widget.getClass());
        final M model = models.computeIfAbsent(Objects.requireNonNull(id), manager::createEmptyModel);
        manager.restore(widget, model);
        manager.watch(widget, model);
    }
}
