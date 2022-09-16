module com.fluentt.javafxcharts {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.fluentt.javafxcharts to javafx.fxml;
    exports com.fluentt.javafxcharts;
}