package org.itsallcode.whiterabbit.jfxui.uistate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.uistate.widgets.WidgetRegistry;
import org.itsallcode.whiterabbit.logic.Config;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class UiStateService
{
    private static final Logger LOG = LogManager.getLogger(UiStateService.class);

    private final Jsonb jsonb;
    private final WidgetRegistry state;
    private final Config config;

    private UiStateService(WidgetRegistry state, Config config, Jsonb jsonb)
    {
        this.jsonb = jsonb;
        this.config = config;
        this.state = state;
    }

    public static UiStateService loadState(Config config)
    {
        final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return new UiStateService(loadState(jsonb, config.getUiStatePath()), config, jsonb);
    }

    private static WidgetRegistry loadState(Jsonb jsonb, Path path)
    {
        if (Files.exists(path))
        {
            try (InputStream inputStream = Files.newInputStream(path))
            {
                final WidgetRegistry state = jsonb.fromJson(inputStream, WidgetRegistry.class);
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
        return new WidgetRegistry();
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

    public void register(String id, Stage stage)
    {
        state.registerStage(id, stage);
    }

    public void register(TableView<?> node)
    {
        state.registerTableView(node);
    }

    public void register(TreeTableView<?> node)
    {
        state.registerTreeTableView(node);
    }

    public void register(SplitPane pane)
    {
        state.registerSplitPane(pane);
    }
}
