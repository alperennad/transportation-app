module com.transport {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires java.logging;

    exports com.transport;
    exports com.transport.model.location;
    exports com.transport.model.passenger;
    exports com.transport.model.route;
    exports com.transport.service;
}