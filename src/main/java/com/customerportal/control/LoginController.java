package com.customerportal.control;

import com.customerportal.Launcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    
    @FXML //email
    private TextField emailField;

    @FXML //password
    private PasswordField passwordField;

    @FXML // message
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            messageLabel.setText("Please enter your email address and password.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/home-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load(), 550, 650);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.setTitle("Home");
    }

    @FXML
private void handleCreateAccount(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(
            Launcher.class.getResource(
                    "/com/customerportal/view/register-view.fxml"
            )
    );

    Scene scene = new Scene(loader.load(), 550, 650);

    Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();

    stage.setScene(scene);
    stage.setTitle("Create Customer Account");
}
}
