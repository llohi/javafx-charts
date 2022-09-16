module com.fluentt.javafxcharts {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.fluentt.javafxcharts to javafx.fxml;
    exports com.fluentt.javafxcharts;
}