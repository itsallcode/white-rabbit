package org.itsallcode.whiterabbit.jfxui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		Application.launch(App.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final StackPane root = new StackPane(new Label("Hello World!"));

		final Scene scene = new Scene(root, 800, 600);

		primaryStage.setScene(scene);
		primaryStage.show();

		new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				throw new RuntimeException("Should not happen!");
			}

			System.exit(0);
		}).start();
	}
}
