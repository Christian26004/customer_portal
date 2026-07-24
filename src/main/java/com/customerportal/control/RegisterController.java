package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField stateField;

    @FXML
    private TextField zipCodeField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();
        String street = streetField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zipCode = zipCodeField.getText();

        if (name.isBlank()
                || email.isBlank()
                || password.isBlank()
                || confirmation.isBlank()
                || street.isBlank()
                || city.isBlank()
                || state.isBlank()
                || zipCode.isBlank()) {

            messageLabel.setText("Please complete every field.");
            return;
        }

        if (!password.equals(confirmation)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        Customer customer = new Customer(name, email, password);

        Address address = new Address(
                0,
                street,
                city,
                state,
                zipCode
        );

        messageLabel.setText(
                "Account created for " + customer.getName() + "."
        );

        System.out.println("Registered: " + customer.getEmail());
        System.out.println("Address: " + address.getStreet());
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/customerportal/view/login-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 500, 400);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.setTitle("Customer Portal Login");
    }
}