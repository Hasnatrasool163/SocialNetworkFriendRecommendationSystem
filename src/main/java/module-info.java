module org.example.dsassignment3_4 {
    requires javafx.fxml;
    requires fontawesomefx;
    requires java.sql;
    requires javafx.controls;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires com.jfoenix;
    requires mysql.connector.j;


    opens org.example.dsassignment3_4 to javafx.fxml;
    exports org.example.dsassignment3_4;
    exports org.example.dsassignment3_4.model;
    exports org.example.dsassignment3_4.view to javafx.fxml,javafx.graphics;
    opens org.example.dsassignment3_4.view to javafx.fxml,javafx.graphics;
    exports org.example.dsassignment3_4.splash to javafx.fxml,javafx.graphics;
    opens org.example.dsassignment3_4.model to javafx.fxml;
    exports org.example.dsassignment3_4.controllers;
    opens org.example.dsassignment3_4.controllers to javafx.fxml;
    exports org.example.dsassignment3_4.utilities;
    opens org.example.dsassignment3_4.utilities to javafx.fxml;
    exports org.example.dsassignment3_4.extras;
    opens org.example.dsassignment3_4.extras to javafx.fxml;
    exports org.example.dsassignment3_4.dao;
    opens org.example.dsassignment3_4.dao to javafx.fxml;
}