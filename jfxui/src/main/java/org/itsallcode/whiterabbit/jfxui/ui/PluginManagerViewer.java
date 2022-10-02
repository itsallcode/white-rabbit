package org.itsallcode.whiterabbit.jfxui.ui;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;
import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin;
import org.itsallcode.whiterabbit.logic.service.plugin.AppPlugin.AppPluginFeature;
import org.itsallcode.whiterabbit.logic.service.plugin.PluginManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

public class PluginManagerViewer
{
    private final Stage primaryStage;
    private final PluginManager pluginManager;
    private final UiStateService uiState;
    private Stage stage;

    public PluginManagerViewer(Stage primaryStage, UiStateService uiState, PluginManager pluginManager)
    {
        this.primaryStage = primaryStage;
        this.pluginManager = pluginManager;
        this.uiState = uiState;
    }

    public void show()
    {
        final BorderPane pane = new BorderPane();
        final TableView<PluginTableEntry> table = createTableView();
        pane.setCenter(table);
        BorderPane.setMargin(table, UiResources.DEFAULT_MARGIN);
        this.stage = createStage(pane);
        this.stage.show();
    }

    private TableView<PluginTableEntry> createTableView()
    {
        final ObservableList<PluginTableEntry> availablePlugins = FXCollections.observableArrayList(getAllPlugins());
        final TableView<PluginTableEntry> tableView = new TableView<>(availablePlugins);
        tableView.setId("plugin-table");
        tableView.setEditable(false);
        tableView.getColumns().addAll(List.of(
                UiWidget.readOnlyColumn("pluginId", "ID", new DefaultStringConverter(), PluginTableEntry::getId),
                UiWidget.readOnlyColumn("features", "Features", new DefaultStringConverter(),
                        PluginTableEntry::getFeatures),
                UiWidget.readOnlyColumn("origin", "Origin", new DefaultStringConverter(),
                        PluginTableEntry::getOrigin)));
        uiState.register(tableView);
        return tableView;
    }

    private List<PluginTableEntry> getAllPlugins()
    {
        return pluginManager.getAllPlugins().stream()
                .map(this::createTableEntry)
                .collect(toList());
    }

    private PluginTableEntry createTableEntry(AppPlugin plugin)
    {
        return new PluginTableEntry(plugin);
    }

    private Stage createStage(final Parent root)
    {
        final Stage newStage = new Stage();
        newStage.setTitle("Plugin Manager");
        newStage.setScene(UiWidget.scene(root, 600, 400));
        newStage.initModality(Modality.NONE);
        newStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                event.consume();
                this.stage.close();
            }
        });
        newStage.initOwner(primaryStage);
        newStage.getIcons().add(UiResources.APP_ICON);
        uiState.register("plugin-manager", newStage);
        return newStage;
    }

    public static class PluginTableEntry
    {
        private final AppPlugin plugin;

        PluginTableEntry(AppPlugin plugin)
        {
            this.plugin = plugin;
        }

        String getId()
        {
            return plugin.getId();
        }

        String getFeatures()
        {
            return plugin.getFeatures().stream().map(AppPluginFeature::getName).collect(joining(", "));
        }

        String getOrigin()
        {
            return plugin.getOrigin().getDescription();
        }
    }
}
