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
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label streetLabel;

    @FXML
    private Label cityStateZipLabel;

    @FXML
    private Label messageLabel;

    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;

        nameLabel.setText("Name: " + customer.getName());
        emailLabel.setText("Email: " + customer.getEmail());

        Address address = customer.getAddress();

        if (address != null) {
            streetLabel.setText(
                    "Street: " + address.getStreet()
            );

            cityStateZipLabel.setText(
                    address.getCity()
                            + ", "
                            + address.getState()
                            + " "
                            + address.getZipCode()
            );
        } else {
            streetLabel.setText("No address saved.");
            cityStateZipLabel.setText("");
        }
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource(
                            "/com/customerportal/view/edit-profile-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load(), 550, 650);

            EditProfileController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("Edit Customer Profile");
            stage.show();

        } catch (IOException exception) {
            exception.printStackTrace();
            messageLabel.setText("Could not open edit profile page.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/login-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load(), 500, 400);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.setTitle("Customer Portal Login");
        stage.show();
    }
}