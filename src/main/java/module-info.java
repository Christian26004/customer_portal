module com.customerportal.customer_portal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.customerportal.customer_portal to javafx.fxml;
    exports com.customerportal.customer_portal;
}