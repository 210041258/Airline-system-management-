module ps.managmenrt.airport {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens ps.managmenrt.airport to javafx.fxml;
    exports ps.managmenrt.airport;
}