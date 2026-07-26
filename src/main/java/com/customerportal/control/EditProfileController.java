package com.customerportal.control;

import com.customerportal.model.Address;
import com.customerportal.model.Customer;
import com.customerportal.model.CustomerManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private VBox passwordRequirementsBox;

    @FXML
    private Label numberRequirementLabel;

    @FXML
    private Label uppercaseRequirementLabel;

    @FXML
    private Label lowercaseRequirementLabel;

    @FXML
    private Label lengthRequirementLabel;

    @FXML
    private Label passwordMatchLabel;

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

    private Customer customer;

    @FXML
    private void initialize() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordRequirements(newValue);
            updatePasswordMatch();
        });
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) ->
                updatePasswordMatch());

        passwordField.focusedProperty().addListener((observable, oldValue, focused) -> {
            passwordRequirementsBox.setVisible(focused);
            passwordRequirementsBox.setManaged(focused);
            if (focused) {
                updatePasswordRequirements(passwordField.getText());
            }
        });

        confirmPasswordField.focusedProperty().addListener((observable, oldValue, focused) -> {
            passwordMatchLabel.setVisible(focused);
            passwordMatchLabel.setManaged(focused);
            if (focused) {
                updatePasswordMatch();
            }
        });
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;

        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());
        passwordField.clear();
        confirmPasswordField.clear();

        Address address = customer.getAddress();

        if (address != null) {
            streetField.setText(address.getStreet());
            cityField.setText(address.getCity());
            stateField.setText(address.getState());
            zipCodeField.setText(address.getZipCode());
        }
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmation = confirmPasswordField.getText();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zipCode = zipCodeField.getText().trim();

        if (name.isBlank() || email.isBlank()) {

            messageLabel.setText("Please complete every required field.");
            return;
        }

        if (!newPassword.isBlank()
                && (!isValidPassword(newPassword) || !newPassword.equals(confirmation))) {
            messageLabel.setText(!isValidPassword(newPassword)
                    ? "New password does not meet the requirements above."
                    : "Passwords do not match.");
            return;
        }

        Address updatedAddress = street.isBlank() && city.isBlank() && state.isBlank() && zipCode.isBlank()
                ? null
                : new Address(0, emptyToNull(street), emptyToNull(city), emptyToNull(state), emptyToNull(zipCode));

        boolean emailChanged =
                !customer.getEmail().equalsIgnoreCase(email);

        if (emailChanged && CustomerManager.emailExists(email)) {
            messageLabel.setText(
                    "Another account already uses that email."
            );
            return;
        }

        customer.setName(name);
        customer.setEmail(email);

        if (!newPassword.isBlank()) {
            customer.setPassword(newPassword);
        }

        customer.setAddress(updatedAddress);

        if (!CustomerManager.updateCustomer(customer)) {
            messageLabel.setText("Could not save your changes.");
            return;
        }

        closeEditor(event);
    }

    private String emptyToNull(String value) {
        return value.isBlank() ? null : value;
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*");
    }

    private void updatePasswordRequirements(String password) {
        setRequirement(numberRequirementLabel, password.matches(".*\\d.*"), "Have One number");
        setRequirement(uppercaseRequirementLabel, password.matches(".*[A-Z].*"),
                "Have One uppercase character");
        setRequirement(lowercaseRequirementLabel, password.matches(".*[a-z].*"),
                "Have One lowercase character");
        setRequirement(lengthRequirementLabel, password.length() >= 8, "Have 8 characters minimum");
    }

    private void setRequirement(Label label, boolean met, String requirement) {
        label.setText((met ? "✓ " : "• ") + requirement);
        label.setStyle("-fx-text-fill: " + (met ? "green" : "#b00020") + ";");
    }

    private void updatePasswordMatch() {
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();

        if (confirmation.isEmpty() || password.isEmpty()) {
            passwordMatchLabel.setText("Must match password");
            passwordMatchLabel.setStyle("-fx-text-fill: #b00020;");
        } else if (password.equals(confirmation)) {
            passwordMatchLabel.setText("✓ Passwords match");
            passwordMatchLabel.setStyle("-fx-text-fill: green;");
        } else {
            passwordMatchLabel.setText("Must match password");
            passwordMatchLabel.setStyle("-fx-text-fill: #b00020;");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeEditor(event);
    }

    private void closeEditor(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
