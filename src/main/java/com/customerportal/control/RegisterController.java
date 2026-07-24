package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.Customer;
import javafx.beans.Observable;
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
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();
        String street = streetField.getText();
        String city = cityField.getText();
        String state = stateBox.getValue();
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