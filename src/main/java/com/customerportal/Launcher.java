package com.customerportal;

import com.customerportal.model.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    public static final double WINDOW_WIDTH = 900;
    public static final double WINDOW_HEIGHT = 700;

    @Override
    public void init() throws Exception {
        Database.initializeDatabase();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/com/customerportal/view/login-view.fxml"));

        Scene scene = createScene(fxmlLoader);
        showScene(stage, scene, "Customer Portal Login");
    }

    public static Scene createScene(FXMLLoader loader) throws IOException {
        return new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public static void showScene(Stage stage, Scene scene, String title) {
        boolean maximized = stage.isMaximized();

        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);

        if (!maximized) {
            stage.setWidth(WINDOW_WIDTH);
            stage.setHeight(WINDOW_HEIGHT);
            stage.centerOnScreen();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
