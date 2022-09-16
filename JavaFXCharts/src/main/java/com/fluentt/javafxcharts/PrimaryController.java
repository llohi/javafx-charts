package com.fluentt.javafxcharts;

import com.fluentt.javafxcharts.api.FMIUrl;
import com.fluentt.javafxcharts.api.ServerRequest;
import com.fluentt.javafxcharts.data.BsWfsElement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

        // Get forecast from 17/9/2022 4:00 - 6:00 pm at coords 61.49911, 23.78712.
        List<BsWfsElement> data = ServerRequest.getData(FMIUrl.getForecastURL(
                61.49911, 23.78712,
                "2022-09-17T16:00:00Z", "2022-09-17T18:00:00Z",
                10,
                true, false));
        ObservableList<BsWfsElement> obsData = FXCollections.observableList(data);
        mTable.setItems(obsData);
        initChart(obsData);
    }

    private void initChart(ObservableList<BsWfsElement> obsData) {

        // Configure x-axis
        //xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        xAxis.setCategories(
                FXCollections.observableList(
                        obsData.stream()
                                .map(BsWfsElement::getTime)
                                .collect(Collectors.toList())));

        // Configure y-axis
        //yAxis = new NumberAxis();
        yAxis.setLabel("Temperature");

        //mChart = new LineChart(xAxis, yAxis);
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