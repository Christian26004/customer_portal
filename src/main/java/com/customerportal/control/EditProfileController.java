package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Address;
import com.customerportal.model.Customer;
import com.customerportal.model.CustomerManager;
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

public class EditProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

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

    public void setCustomer(Customer customer) {
        this.customer = customer;

        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());

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
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zipCode = zipCodeField.getText().trim();

        if (name.isBlank()
                || email.isBlank()
                || street.isBlank()
                || city.isBlank()
                || state.isBlank()
                || zipCode.isBlank()) {

            messageLabel.setText("Please complete every required field.");
            return;
        }

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

        Address address = customer.getAddress();

        if (address == null) {
            address = new Address(
                    0,
                    street,
                    city,
                    state,
                    zipCode
            );

            customer.setAddress(address);
        } else {
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
        }

        try {
            openProfile(event);
        } catch (IOException exception) {
            exception.printStackTrace();
            messageLabel.setText("Could not return to profile.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            openProfile(event);
        } catch (IOException exception) {
            exception.printStackTrace();
            messageLabel.setText("Could not return to profile.");
        }
    }

    private void openProfile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/profile-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load(), 500, 500);

        ProfileController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.setTitle("Customer Profile");
        stage.show();
    }
}
