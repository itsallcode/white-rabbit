package org.itsallcode.whiterabbit.jfxui.ui.widget;

import org.itsallcode.whiterabbit.jfxui.ui.UiResources;
import org.itsallcode.whiterabbit.jfxui.ui.UiWidget;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportWindow
{
    private final Stage primaryStage;
    private Stage stage;

    public ReportWindow(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

    public void show(Node reportView)
    {
        stage = createStage(reportView);
        stage.show();
    }

    private Stage createStage(Node reportView)
    {
        final BorderPane pane = new BorderPane();
        pane.setTop(createToolBar());
        pane.setCenter(reportView);
        BorderPane.setMargin(reportView, UiResources.DEFAULT_MARGIN);
        return createStage(pane);
    }

    private ToolBar createToolBar()
    {
        return new ToolBar(UiWidget.button("close-button", "Close Report", e -> closeReportWindow()));
    }

    private Stage createStage(final Parent root)
    {
        final Stage newStage = new Stage();
        newStage.setTitle("Project report");
        newStage.setScene(new Scene(root, 500, 800));
        newStage.initModality(Modality.NONE);
        newStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                closeReportWindow();
            }
        });
        newStage.initOwner(primaryStage);
        newStage.getIcons().add(UiResources.APP_ICON);
        return newStage;
    }

    private void closeReportWindow()
    {
        this.stage.close();
    }
}
