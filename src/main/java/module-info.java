module com.warehouse.warehouse {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    opens com.warehouse.warehouse to javafx.fxml;
    exports com.warehouse.warehouse;
    exports com.warehouse.warehouse.controller;
    opens com.warehouse.warehouse.controller to javafx.fxml;
    exports com.warehouse.warehouse.database;
    opens com.warehouse.warehouse.database to javafx.fxml;
}