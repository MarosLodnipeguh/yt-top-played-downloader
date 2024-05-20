module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens app to javafx.fxml;
    exports app;
    exports app.Workers;
    opens app.Workers to javafx.fxml;
    exports app.DataStructures;
    opens app.DataStructures to javafx.fxml;
}