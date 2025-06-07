module app.edugram {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jdk.jfr;
    requires java.desktop;

    opens app.edugram to javafx.fxml;
    exports app.edugram;
    exports app.edugram.controllers;
    opens app.edugram.controllers to javafx.fxml;
    exports app.edugram.controllers.cache;
    opens app.edugram.controllers.cache to javafx.fxml;
}