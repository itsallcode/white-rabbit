package org.itsallcode.whiterabbit.jfxui.ui.widget;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.jfxui.ui.UiResources;
import org.itsallcode.whiterabbit.jfxui.ui.UiWidget;
import org.itsallcode.whiterabbit.jfxui.uistate.UiStateService;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportWindow
{
    private static final Logger LOG = LogManager.getLogger(ReportWindow.class);
    private final Stage primaryStage;
    private final String windowTitle;
    private final UiStateService uiState;
    private final String id;
    private Stage stage;

    public ReportWindow(final Stage primaryStage, final UiStateService uiState, final String id,
            final String windowTitle)
    {
        this.primaryStage = primaryStage;
        this.uiState = uiState;
        this.id = id;
        this.windowTitle = windowTitle;
    }

    public void show(final Node reportView, final Node... toolBarItems)
    {
        stage = createStage(reportView, toolBarItems);
        LOG.debug("Show report window");
        stage.show();
    }

    private Stage createStage(final Node reportView, final Node... toolBarItems)
    {
        final BorderPane pane = new BorderPane();
        pane.setTop(createToolBar(toolBarItems));
        pane.setCenter(reportView);
        BorderPane.setMargin(reportView, UiResources.DEFAULT_MARGIN);
        return createStage(pane);
    }

    private ToolBar createToolBar(final Node... items)
    {
        final Node closeButton = UiWidget.button("close-button", "Close Report", e -> closeReportWindow());
        final List<Node> allItems = new ArrayList<>();
        allItems.add(closeButton);
        allItems.addAll(asList(items));
        return new ToolBar(allItems.toArray(new Node[0]));
    }

    private Stage createStage(final Parent root)
    {
        final Stage newStage = new Stage();
        newStage.setTitle(windowTitle);
        newStage.setScene(UiWidget.scene(root, 500, 800));
        newStage.initModality(Modality.NONE);
        newStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                event.consume();
                closeReportWindow();
            }
        });
        newStage.initOwner(primaryStage);
        newStage.getIcons().add(UiResources.APP_ICON);
        uiState.register(id, newStage);
        return newStage;
    }

    public void updateTitle(final String title)
    {
        LOG.debug("Update window title to '{}'", title);
        stage.setTitle(title);
    }

    private void closeReportWindow()
    {
        LOG.debug("Closing window '{}'", stage.getTitle());
        this.stage.close();
    }
}
