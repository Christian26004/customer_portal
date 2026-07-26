package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.Customer;
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
import javafx.scene.layout.VBox;
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
    public void initialize() {
        stateBox.setItems(getStateList());

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

        if (!isValidPassword(password)) {
            messageLabel.setText("Password does not meet the requirements above.");
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

        Customer customer = CustomerManager.loginCustomer(email, password);
        if (customer == null) {
            messageLabel.setText("Account created. Please return to login.");
            return;
        }

        openProducts(event, customer);
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

    private void openProducts(ActionEvent event, Customer customer) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/customerportal/view/product-page.fxml"));
        Scene scene = Launcher.createScene(loader);

        ProductController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Launcher.showScene(stage, scene, "Products");
    }

    private void updatePasswordMatch() {
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();

        if (confirmation.isEmpty()) {
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
    private void handleBackToLogin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/login-view.fxml"
                )
        );

        Scene scene = Launcher.createScene(loader);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Launcher.showScene(stage, scene, "Customer Portal Login");
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
