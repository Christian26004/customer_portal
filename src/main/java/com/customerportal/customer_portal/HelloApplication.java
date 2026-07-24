package com.customerportal.customer_portal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(
            HelloApplication.class.getResource("/com/customerportal/customer_portal/login-view.fxml"));

    Scene scene = new Scene(fxmlLoader.load(), 500, 400);

    stage.setTitle("Customer Portal Login");
    stage.setScene(scene);
    stage.show();
}
}
