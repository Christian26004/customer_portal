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
import javafx.stage.Modality;

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
            streetLabel.setText("Street: " + valueOrEmpty(address.getStreet()));
            cityStateZipLabel.setText(
                    valueOrEmpty(address.getCity())
                            + ", "
                            + valueOrEmpty(address.getState())
                            + " "
                            + valueOrEmpty(address.getZipCode())
            );
        } else {
            streetLabel.setText("No address saved.");
            cityStateZipLabel.setText("");
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource(
                            "/com/customerportal/view/edit-profile-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load(), 520, 760);

            EditProfileController controller = loader.getController();
            controller.setCustomer(customer);

            Stage parentStage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            Stage editStage = new Stage();
            editStage.initOwner(parentStage);
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.setTitle("Edit Customer Profile");
            editStage.setScene(scene);
            editStage.setResizable(false);
            editStage.showAndWait();

            // The editor updates the same Customer object before closing.
            setCustomer(customer);

        } catch (IOException exception) {
            exception.printStackTrace();
            messageLabel.setText("Could not open edit profile page.");
        }
    }

   @FXML
private void handleOrderHistory(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/order-history-view.fxml"
                )
        );

        Scene scene = Launcher.createScene(loader);

        OrderHistoryController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Launcher.showScene(stage, scene, "Order History");

    } catch (IOException exception) {
        exception.printStackTrace();
        messageLabel.setText("Could not open order history.");
    }
}


    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/login-view.fxml"
                )
        );

        Scene scene = Launcher.createScene(loader);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Launcher.showScene(stage, scene, "Customer Portal Login");
    }

    @FXML
private void handleOpenCart(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource(
                        "/com/customerportal/view/cart-view.fxml"
                )
        );

        Scene scene = Launcher.createScene(loader);

        CartController controller = loader.getController();
        controller.setCustomer(customer);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Launcher.showScene(stage, scene, "Shopping Cart");

    } catch (IOException exception) {
        exception.printStackTrace();
        messageLabel.setText("Could not open shopping cart.");
    }
}
}
