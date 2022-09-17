package com.fluentt.javafxcharts;

import com.fluentt.javafxcharts.api.FMIUrl;
import com.fluentt.javafxcharts.api.ServerRequest;
import com.fluentt.javafxcharts.data.BsWfsElement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PrimaryController implements Initializable {

    @FXML
    public Button fetchButton;

    @FXML
    public LineChart<String, Double> mChart;

    public CategoryAxis xAxis;
    public NumberAxis yAxis;
    public DatePicker startDate;
    public DatePicker endDate;
    public TextField startTime;
    public TextField endTime;
    public TextField timeStep;

    @FXML
    private TableView<BsWfsElement> mTable;

    @FXML
    public TableColumn<BsWfsElement, String> time;

    @FXML
    public TableColumn<BsWfsElement, String> location;

    @FXML
    public TableColumn<BsWfsElement, String> parameter_name;

    @FXML
    public TableColumn<BsWfsElement, String> parameter_value;

    @FXML
    private void fetchData() throws IOException, XPathExpressionException,
                                    ParserConfigurationException, SAXException {

        if (startDate.getValue() == null || endDate.getValue() == null ||
            startTime.getText().equals("") || endTime.getText().equals("") ||
            timeStep.getText().equals(""))
            return;

        // TODO: 9/17/2022 Maybe create seperate function to check input parameters (string format etc)


        // Clear all prior data
        mTable.getItems().clear();
        mChart.getData().clear();

        // Format time intervals into ISO 8601
        String start = startDate.getValue().format(DateTimeFormatter.ISO_DATE) +
                       "T" + startTime.getText() + "Z";

        String end = endDate.getValue().format(DateTimeFormatter.ISO_DATE) +
                     "T" + endTime.getText() + "Z";

        // Get data from the API
        ObservableList<BsWfsElement> data =
                FXCollections.observableList(    // <-- Get data as ObservableList
                        ServerRequest.getData(     // <-- Connect to API
                            FMIUrl.getForecastURL(   // <-- Get the url with parameters
                                    61.49911, 23.78712,
                                    start, end,
                                    Integer.parseInt(timeStep.getText()),
                                    true, true)
                ));
        mTable.setItems(data);
        initCharts(data);
    }

    private void initCharts(ObservableList<BsWfsElement> obsData) {

        // Add data to the chart
        XYChart.Series<String, Double> tempSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> windSeries = new XYChart.Series<>();
        tempSeries.setName("Temperature");
        windSeries.setName("Windspeed");
        for (BsWfsElement e : obsData) {
            if (e.getParameter_name().equals("temperature"))
                tempSeries.getData().add(new XYChart.Data<>(formatIsoDate(e.getTime()),
                                                            e.getParameter_value()));
            else
                windSeries.getData().add(new XYChart.Data<>(formatIsoDate(e.getTime()),
                                                            e.getParameter_value()));
        }

        mChart.getData().addAll(tempSeries, windSeries);
    }

    /**
     * Initialize table columns.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        time.setCellValueFactory(
                m -> (m.getValue() != null) ?
                    new SimpleStringProperty(m.getValue().getTime()) :
                    new SimpleStringProperty("No time"));

        location.setCellValueFactory(
                m -> (m.getValue() != null) ?
                        new SimpleStringProperty(Arrays.toString(m.getValue().getPos())) :
                        new SimpleStringProperty("No location"));

        parameter_name.setCellValueFactory(
                m -> (m.getValue() != null) ?
                        new SimpleStringProperty(m.getValue().getParameter_name()) :
                        new SimpleStringProperty("No parameter name"));

        parameter_value.setCellValueFactory(
                m -> (m.getValue() != null) ?
                        new SimpleStringProperty(""+m.getValue().getParameter_value()) :
                        new SimpleStringProperty("No parameter value"));

        mChart.setTitle("Weather Forecast Data");
        mChart.setCreateSymbols(false);

        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
    }

    private String formatIsoDate(String date) {
        return date.substring(5)
                    .replace("T", "\n")
                   .replace("Z", "");
    }
}