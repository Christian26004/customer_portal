package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Customer;
import com.customerportal.model.CustomerManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            messageLabel.setText("Please enter your email address and password.");
            return;
        }

        Customer customer = CustomerManager.loginCustomer(email, password);

        if (customer == null) {
            messageLabel.setText("Incorrect email or password.");
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource(
                            "/com/customerportal/view/profile-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 450);

            ProfileController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Customer Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open profile page.");
        }
    }

    @FXML
    private void handleCreateAccount() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/register-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 550, 650);

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Create Customer Account");
        stage.show();
    }
}