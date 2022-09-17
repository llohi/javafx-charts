package com.fluentt.javafxcharts;

import com.fluentt.javafxcharts.api.FMIUrl;
import com.fluentt.javafxcharts.api.ServerRequest;
import com.fluentt.javafxcharts.data.BsWfsElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrimaryController implements Initializable {

    private final static Map<String, double[]> CITIES = new HashMap<>() {{
        put("Helsinki", new double[]{60.169444, 24.935278});
        put("Espoo", new double[]{60.205, 24.651944});
        put("Vantaa", new double[]{60.293889, 25.040833});
        put("Tampere", new double[]{61.498889, 23.786944});
        put("Rovaniemi", new double[]{66.5, 25.716667});
    }};

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
    public ComboBox<String> cityComboBox;
    public CheckBox showTemperature;
    public CheckBox showWindspeed;
    public Label latitudeLabel;
    public Label longitudeLabel;

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

    /**
     * This method is called when the fetch button is clicked.
     * It checks the validity of the parameters given and fetches
     * the data from the API.
     */
    @FXML
    private void fetchData() throws IOException, XPathExpressionException,
                                    ParserConfigurationException, SAXException {

        if (!validParameters())
            return;

        // Clear all prior data from the table and line chart
        mTable.getItems().clear();
        mChart.getData().clear();

        // Format time intervals into ISO 8601
        String start = startDate.getValue().format(DateTimeFormatter.ISO_DATE) +
                       "T" + startTime.getText() + "Z";

        String end = endDate.getValue().format(DateTimeFormatter.ISO_DATE) +
                     "T" + endTime.getText() + "Z";

        // Get the coordinates for the selected city
        double[] coords = CITIES.get(cityComboBox.getValue());

        // Update coordinate labels in the ui
        latitudeLabel.setText(Double.toString(coords[0]));
        longitudeLabel.setText(Double.toString(coords[1]));

        // Get data from the API
        ObservableList<BsWfsElement> data =
                FXCollections.observableList(    // <-- Get data as ObservableList
                        ServerRequest.getData(     // <-- Connect to API
                            FMIUrl.getForecastURL(   // <-- Get the url with parameters
                                    coords[0], coords[1],
                                    start, end,
                                    Integer.parseInt(timeStep.getText()),
                                    showTemperature.isSelected(), showWindspeed.isSelected())
                ));

        // Populate ui elements with the data
        mTable.setItems(data);
        plotData(data);
    }

    /**
     * Plot the weather forecast data into the line chart in the UI.
     * @param data the data as an ObservableList
     */
    private void plotData(ObservableList<BsWfsElement> data) {

        // Create XYChart.Series for the temperature and windspeed data
        XYChart.Series<String, Double> tempSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> windSeries = new XYChart.Series<>();
        tempSeries.setName("Temperature °C");
        windSeries.setName("Windspeed m/s");

        // Populate the series
        for (BsWfsElement e : data) {
            if (e.getParameter_name().equals("temperature"))
                tempSeries.getData().add(new XYChart.Data<>(formatIsoDate(e.getTime()),
                                                            e.getParameter_value()));
            else
                windSeries.getData().add(new XYChart.Data<>(formatIsoDate(e.getTime()),
                                                            e.getParameter_value()));
        }

        // Add the series to the line chart
        if (tempSeries.getData().size() != 0)
            mChart.getData().add(tempSeries);

        if (windSeries.getData().size() != 0)
            mChart.getData().add(windSeries);
    }

    /**
     * Initialize the user interface.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configureComboBox();
        configureDatePickers();
        configureTableCells();
        configureLineChart();
    }

    /**
     * Populate the combo box with the cities.
     */
    private void configureComboBox() {
        cityComboBox.setItems(
                FXCollections.observableList(
                        new ArrayList<>(CITIES.keySet())));
    }

    /**
     * Disable past dates for the date pickers since we are only testing
     * with forecast data from the API.
     */
    private void configureDatePickers() {
        startDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        endDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    private void configureTableCells() {
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

    private void configureLineChart() {
        mChart.setTitle("Weather Forecast Data");
        mChart.setCreateSymbols(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
    }

    /**
     * Check is parameters are empty
     * @return true if valid, false if invalid parameters
     */
    private boolean validParameters() {
        return // Check for empty values
               startDate.getValue() != null &&
               !startTime.getText().equals("") &&
               endDate.getValue() != null &&
               !endTime.getText().equals("") &&
               !timeStep.getText().equals("") &&
               !cityComboBox.getValue().equals("") &&
               (showTemperature.isSelected() || showWindspeed.isSelected()) &&
               // Check for invalid values
               (Double.parseDouble(timeStep.getText()) > 0);
    }

    /**
     * Format the fetched ISO date into a readable time.
     * @param date the date in ISO format
     * @return the formatted date as a String
     */
    private String formatIsoDate(String date) {
        return date.substring(5)
                   .replace("T", "\n")
                   .replace("Z", "");
    }
}