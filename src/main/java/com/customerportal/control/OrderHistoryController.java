package com.customerportal.control;

import com.customerportal.Launcher;
import com.customerportal.model.Customer;
import com.customerportal.model.Order;
import com.customerportal.model.OrderDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class OrderHistoryController {

    @FXML
    private ListView<String> orderListView;

    @FXML
    private Label messageLabel;

    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        loadOrders();
    }

    private void loadOrders() {
        orderListView.getItems().clear();

        List<Order> orders = OrderDAO.getOrdersForCustomer(
                customer.getCustomerId()
        );

        if (orders.isEmpty()) {
            messageLabel.setText("You have no previous orders.");
            return;
        }

        messageLabel.setText("");

        orderListView.setItems(
                FXCollections.observableArrayList(
                        orders.stream()
                                .map(this::formatOrder)
                                .toList()
                )
        );
    }

    private String formatOrder(Order order) {
        return "Order #" + order.getOrderId()
                + " | Date: " + order.getOrderDate()
                + " | Total: $"
                + String.format("%.2f", order.getTotal())
                + " | Status: " + order.getStatus();
    }

    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource(
                            "/com/customerportal/view/profile-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load(), 500, 500);

            ProfileController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = (Stage) orderListView
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("Customer Profile");
            stage.show();

        } catch (IOException exception) {
            exception.printStackTrace();
            messageLabel.setText("Could not return to profile.");
        }
    }
}