module com.customerportal.customer_portal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.customerportal.control to javafx.fxml;
    opens com.customerportal to javafx.fxml;

    exports com.customerportal;
    exports com.customerportal.control;
    exports com.customerportal.model;
}