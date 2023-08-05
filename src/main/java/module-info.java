module odc.busui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jgrapht.core;

    exports odc.busui.controllers;
    exports odc.busui.models;
    exports odc.busui.models.entities;
    exports odc.busui.models.locations;
    exports odc.busui.models.events;
    exports odc.busui;

    opens odc.busui.controllers to javafx.fxml;
    opens odc.busui.models to javafx.fxml;
    opens odc.busui.models.entities to javafx.fxml;
}