package org.itsallcode.whiterabbit.jfxui.ui.widget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itsallcode.whiterabbit.api.features.ProgressMonitor;
import org.itsallcode.whiterabbit.jfxui.JavaFxUtil;
import org.itsallcode.whiterabbit.jfxui.ui.UiResources;
import org.itsallcode.whiterabbit.jfxui.ui.UiWidget;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressDialog
{
    private static final Logger LOG = LogManager.getLogger(ProgressDialog.class);

    private ProgressDialog()
    {
        // not instantiable
    }

    public static DialogProgressMonitor show(Stage primaryStage, String windowTitle)
    {
        final var dialog = new Dialog(primaryStage, windowTitle);
        dialog.show();
        final var monitor = new DialogProgressMonitor(dialog);
        dialog.cancelButton.addEventHandler(ActionEvent.ACTION, event -> monitor.setCanceled());
        return monitor;
    }

    private static class Dialog
    {
        private final Stage primaryStage;
        private final String windowTitle;
        private Stage stage;
        private Label taskLabel;
        private ProgressBar progressBar;
        private Button cancelButton;

        public Dialog(Stage primaryStage, String windowTitle)
        {
            this.primaryStage = primaryStage;
            this.windowTitle = windowTitle;
        }

        public void show()
        {
            stage = createStage();
            stage.show();
        }

        private Stage createStage()
        {
            taskLabel = new Label("");
            progressBar = new ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressBar.setPrefWidth(200);
            cancelButton = new Button("Cancel");
            final var pane = new VBox();
            pane.setAlignment(Pos.CENTER);
            pane.setPadding(UiResources.DEFAULT_MARGIN);
            pane.setSpacing(8);
            pane.getChildren().addAll(taskLabel, progressBar, cancelButton);
            return createStage(pane);
        }

        private Stage createStage(final Parent root)
        {
            final var newStage = new Stage();
            newStage.setTitle(windowTitle);
            newStage.setScene(UiWidget.scene(root));
            newStage.initModality(Modality.NONE);
            newStage.initOwner(primaryStage);
            newStage.getIcons().add(UiResources.APP_ICON);
            return newStage;
        }

        public void setTask(String name)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> taskLabel.setText(name));
        }

        public void setProgress(double progress)
        {
            JavaFxUtil.runOnFxApplicationThread(() -> progressBar.setProgress(progress));
        }

        public void close()
        {
            JavaFxUtil.runOnFxApplicationThread(() -> stage.close());
        }
    }

    public static class DialogProgressMonitor implements ProgressMonitor
    {
        private boolean canceled = false;
        private final Dialog dialog;
        private int totalWork = 0;
        private int workDone = 0;

        public DialogProgressMonitor(Dialog dialog)
        {
            this.dialog = dialog;
        }

        @Override
        public boolean isCanceled()
        {
            if (canceled)
            {
                LOG.debug("Dialog is cancelled: abort task");
            }
            return canceled;
        }

        public void setCanceled()
        {
            LOG.debug("User cancelled task");
            this.canceled = true;
        }

        @Override
        public void beginTask(String name, int totalWork)
        {
            this.totalWork = totalWork;
            setTaskName(name);
            dialog.setProgress(0.0);
        }

        @Override
        public void setTaskName(String name)
        {
            dialog.setTask(name);
        }

        @Override
        public void worked(int work)
        {
            workDone += work;
            final double progress = (double) workDone / totalWork;
            dialog.setProgress(progress);
        }

        public void done()
        {
            dialog.close();
        }
    }
}
