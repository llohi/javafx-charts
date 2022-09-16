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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
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

        List<BsWfsElement> data = ServerRequest.getData(FMIUrl.getForecastURL(
                61.49911, 23.78712,
                "2022-09-17T16:00:00Z", "2022-09-18T16:00:00Z",
                60,
                true, false));
        ObservableList<BsWfsElement> obsData = FXCollections.observableList(data);
        mTable.setItems(obsData);
        initChart(obsData);
    }

    private void initChart(ObservableList<BsWfsElement> obsData) {

        mChart.setTitle("Temperature Forecast Data");
        mChart.setLegendVisible(false);
        mChart.setCreateSymbols(false);

        // Configure x-axis
        xAxis.setLabel("Time");

        xAxis.setCategories(
                FXCollections.observableList(
                        obsData.stream()
                                .map(BsWfsElement::getTime)
                                .collect(Collectors.toList())));

        // Configure y-axis
        yAxis.setLabel("Temperature");

        // Add data to the chart
        XYChart.Series<String, Double> tempSeries = new XYChart.Series<>();
        tempSeries.setName("Temperature");
        for (BsWfsElement e : obsData)
            tempSeries.getData().add(new XYChart.Data<>(e.getTime(), e.getParameter_value()));

        // TODO: 9/16/2022 Make it so that these values are in relation to the min/max fetched parameter values.
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(15);
        yAxis.setTickUnit(3);
        mChart.autosize();

        mChart.getData().add(tempSeries);
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
    }
}