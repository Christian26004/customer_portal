package com.customerportal;

import com.customerportal.model.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void init() throws Exception {
        // Runs database setup and creates tables BEFORE the GUI window opens
        Database.initializeDatabase();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/com/customerportal/view/login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 500, 400);

        stage.setTitle("Customer Portal Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
