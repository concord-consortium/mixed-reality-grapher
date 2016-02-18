package org.concord.mixedreality;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Saeid Nourian <snourian@concord.org>
 */
public class MainApplication extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(final Stage stage) throws Exception {
        MainApplication.stage = stage;
        stage.setTitle("Mixed Reality Grapher");

        final Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
