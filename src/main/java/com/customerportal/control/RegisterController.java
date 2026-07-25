package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.CustomerManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> stateBox;

    @FXML
    private TextField zipCodeField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        stateBox.setItems(getStateList());
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateBox.getValue() == null ? "" : stateBox.getValue().trim();
        String zipCode = zipCodeField.getText().trim();

        if (name.isBlank()
                || email.isBlank()
                || password.isBlank()
                || confirmation.isBlank()) {

            messageLabel.setText("Name, email, password, and confirmation are required.");
            return;
        }

        if (!password.equals(confirmation)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        Address address = createOptionalAddress(street, city, state, zipCode);

        boolean registered = CustomerManager.registerCustomer(
                name,
                email,
                password,
                address
        );

        if (!registered) {
            messageLabel.setText("An account with that email already exists.");
            return;
        }

        messageLabel.setText("Account created successfully. Return to login.");

        System.out.println("Registered: " + email);
        System.out.println("Customers stored: " + CustomerManager.getCustomers().size());
        if (address != null) {
            System.out.println("Address: " + address.getStreet());
        }
    }

    private Address createOptionalAddress(String street, String city, String state, String zipCode) {
        if (street.isBlank() && city.isBlank() && state.isBlank() && zipCode.isBlank()) {
            return null;
        }
        return new Address(0, emptyToNull(street), emptyToNull(city), emptyToNull(state), emptyToNull(zipCode));
    }

    private String emptyToNull(String value) {
        return value.isBlank() ? null : value;
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/login-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load(), 550, 400);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Customer Portal Login");
        stage.show();
    }

    private ObservableList<String> getStateList() {
        return FXCollections.observableArrayList(
                "Alabama", "Alaska", "Arizona", "Arkansas", "California",
                "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
                "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
                "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
                "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
                "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
                "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
                "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
        );
    }
}
