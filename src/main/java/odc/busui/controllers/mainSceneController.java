package odc.busui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import odc.busui.mainApplication;
import odc.busui.models.entities.Bus;
import odc.busui.models.entities.Passenger;
import odc.busui.models.locations.City;
import odc.busui.models.locations.Location;
import odc.busui.models.locations.Road;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class mainSceneController implements Initializable{
    private final mainApplication root;
    private int passengerID;

    public mainSceneController(odc.busui.mainApplication mainApplication) {
        this.root = mainApplication;
        this.passengerID = 0;
    }

    @FXML
    private Button addPassenger;
    @FXML
    private Button addBus;
    @FXML
    private Accordion busList;
    @FXML
    private Button startAll;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBus.setOnAction(event -> {
            try {
                addBusAction();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        addPassenger.setOnAction(event -> addPassengerAction());
        startAll.setOnAction(event -> {
            for (Bus bus: root.getBusMap().values()) {
                bus.start();
            }
        });
    }


    private void addBusAction() throws IOException {
        int capacity = 50;
        float speed = 60;
        float malfunctionChance = 0.3f;
        LinkedList<Location> path = root.getNetwork().getRandomPath();
        FXMLLoader loader = new FXMLLoader(root.getClass().getResource("bus.fxml"));
        Bus bus = new Bus(path, root.getNextBusID(), speed, capacity, malfunctionChance);
        busComponentController busComponentController = new busComponentController(bus);
        loader.setController(busComponentController);
        root.getBusMap().put(root.getNextBusID(), bus);
        busList.getPanes().add(loader.load());
    }

    private void addPassengerAction(){
        Location startLocation;
        Location endLocation;
        List<LinkedList<Location>> pathList = root.getBusMap().values().stream().map(Bus::getPath).toList();
        List<Location> randomPath = pathList.get(new Random().nextInt(pathList.size())).stream().filter(City.class::isInstance).toList();
        int startIndex = new Random().nextInt(randomPath.size()-1);
        List<Location> remainingCities = randomPath.subList(startIndex+1, randomPath.size());
        startLocation = randomPath.get(startIndex);
        endLocation = remainingCities.get(new Random().nextInt(remainingCities.size()));
        Passenger passenger = new Passenger(passengerID+1, (City) startLocation, (City) endLocation);
        root.getNetwork().addPassenger(passenger);
        System.out.println(passenger);
        passengerID +=1;
    }

}
