package odc.busui.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import odc.busui.models.entities.Bus;
import odc.busui.models.entities.Passenger;
import odc.busui.models.locations.Location;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class busComponentController implements Initializable {
    private final Bus bus;
    @FXML
    private TitledPane root;
    @FXML
    private Label busOrigin;
    @FXML
    private Label busDestination;
    @FXML
    private Label currentLocation;
    @FXML
    private Label status;
    @FXML
    private Slider speed;
    @FXML
    private Label speedCounter;
    @FXML
    private Slider chance;
    @FXML
    private Label chanceCounter;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView<Passenger> passengerList;
    @FXML
    private TableColumn<Passenger,Integer> idColumn;
    @FXML
    private TableColumn<Passenger, String> originColumn;
    @FXML
    private TableColumn<Passenger, String > destinationColumn;
    @FXML
    private Button start;
    @FXML
    private Button repair;

    public busComponentController(Bus bus) throws IOException {
        this.bus = bus;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setText("Bus "+bus.getID());
        busOrigin.setText(bus.getCurrentLocation().toString());
        busDestination.setText(bus.getDestination().toString());
        bus.readyState(status, currentLocation ,passengerList, root);
        start.setOnAction(event -> bus.start());
        repair.setOnAction(event -> bus.setScheduledRepair(true));
        progressBar.progressProperty().bind(bus.progressProperty());
        idColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Passenger, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Passenger, Integer> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getId());
            }
        });
        originColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Passenger, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Passenger, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getOrigin().toString());
            }
        });
        destinationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Passenger, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Passenger, String> param) {
                return new ReadOnlyObjectWrapper<>(param.getValue().getDestination().toString());
            }
        });
        passengerList.itemsProperty().bind(bus.passengersProperty());
        speed.setMax(150);
        speed.setMin(10);
        chance.setMax(1);
        chance.setMin(0);
        bus.speedProperty().bind(speed.valueProperty());
        bus.malfunctionChanceProperty().bind(chance.valueProperty());
        speedCounter.textProperty().bind(bus.speedProperty().asString());
        chanceCounter.textProperty().bind(bus.malfunctionChanceProperty().asString());
    }
}
