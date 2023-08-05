package odc.busui.models.entities;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import odc.busui.models.events.Maintenance;
import odc.busui.models.events.Malfunction;
import odc.busui.models.locations.City;
import odc.busui.models.locations.Location;
import odc.busui.models.locations.Road;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class Bus extends Service<Boolean> {
    private final Integer ID;
    private FloatProperty speed;
    private final Integer capacity;
    private final float totalDistance;
    private float distanceTravelled;
    private FloatProperty malfunctionChance;
    private final ListProperty<Passenger> passengers;
    private final ObjectProperty<Location> currentLocation;
    private final ObjectProperty<Location> destination;
    private final ObjectProperty<LinkedList<Location>> path;
    private final ObjectProperty<Maintenance> maintenance;
    private final BooleanProperty isMaintaining;
    private final ObjectProperty<Malfunction> malfunction;
    private final BooleanProperty scheduledRepair;
    private Label busActionsLabel;
    private Label busCurrentLocationLabel;
    private TableView<Passenger> passengerTableView;
    private TitledPane busRootPane;

    private final ObjectProperty<Color> busBackgroundColor;



    public Bus(LinkedList<Location> path, int id , float speed, int capacity, float malfunctionChance) {
        this.ID = id;
        this.speed = new SimpleFloatProperty(speed);
        this.capacity = capacity;
        this.distanceTravelled = 0;
        this.malfunctionChance = new SimpleFloatProperty(malfunctionChance);
        this.passengers = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        this.path = new SimpleObjectProperty<>(path);
        this.currentLocation = new SimpleObjectProperty<>(this.path.get().getFirst());
        this.destination = new SimpleObjectProperty<>(this.path.get().getLast());
        this.busBackgroundColor = new SimpleObjectProperty<>(Color.WHITE);
        this.maintenance = new SimpleObjectProperty<>();
        this.malfunction = new SimpleObjectProperty<>();
        this.scheduledRepair = new SimpleBooleanProperty(false);
        this.isMaintaining = new SimpleBooleanProperty(false);
        int cityAdding = path.stream().filter(City.class::isInstance).toList().size() * 20;
        int roadAdding = path.stream().filter(Road.class::isInstance).map(location -> ((Road) location).getLength()).reduce(0, Integer::sum);
        this.totalDistance = cityAdding + roadAdding;
    }

    public ObjectProperty<Color> busBackgroundColorProperty() {
        return busBackgroundColor;
    }

    public void setBusBackgroundColor(Color color) {
        busBackgroundColor.set(color);
    }

    @Override
    public void start() {
        if(!isRunning()){
            super.start();
        }
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                while (!getCurrentLocation().equals(getDestination())) {
                    Platform.runLater(() -> busCurrentLocationLabel.setText(getCurrentLocation().toString()));
                    Platform.runLater(() -> busActionsLabel.setText("Arrived at "+ getCurrentLocation().toString()));
                    distanceTravelled += 20;
                    updateProgress(distanceTravelled, totalDistance);
                    Thread.sleep(500);
                    // let out passengers
                    Platform.runLater(() -> busActionsLabel.setText("Letting out passengers"));
                    Thread.sleep(1000);
                    letOutPassengers();
                    // Leaving City
                    if(getCurrentLocation() instanceof City){
                        // Maitenance event
                        if(isMaintaining.get()){
                            Platform.runLater(() -> busActionsLabel.setText(getMaintenance().getMessage()));
                            Thread.sleep(getMaintenance().getRepairTime());
                        }
                        /////////////
                        // Pick up passengers
                        Platform.runLater(() -> busActionsLabel.setText("Picking up passengers"));
                        Thread.sleep(2000);
                        pickUpPassengers();
                        ///////////////
                        // Advance to next road
                        advance();
                        ////////////
                    }
                    ////////////////
                    // Travelling
                    Platform.runLater(() -> busActionsLabel.setText("Travelling at "+ getCurrentLocation().toString()));
                    Thread.sleep(2000);
                    if(getCurrentLocation() instanceof Road){
                        float inBetweenCitiesDistanceTravelled = 0;
                        int malfunctionCount = 0;
                        float roadLength = ((Road) getCurrentLocation()).getLength();
                        while(inBetweenCitiesDistanceTravelled < roadLength){
                            inBetweenCitiesDistanceTravelled += getSpeed();
                            if(inBetweenCitiesDistanceTravelled > roadLength){
                                distanceTravelled += roadLength - (inBetweenCitiesDistanceTravelled - speed.getValue());
                            } else {
                                distanceTravelled += getSpeed();
                            }
                            updateProgress(distanceTravelled, totalDistance);
                            if(malfunctionCount == 0 && new Random().nextFloat() < getMalfunctionChance()){
                                Platform.runLater(()-> {
                                    busActionsLabel.setText("Malfunctioning");
                                    busRootPane.setText(busRootPane.getText()+ " Malfunctioning");
                                    busRootPane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
                                });
                                do {
                                    Thread.sleep(500);
                                } while (!isScheduledRepair());
                                Platform.runLater(() -> {
                                    busActionsLabel.setText("Repairing");
                                    busRootPane.setText("Bus "+ID);
                                });
                                Thread.sleep(2000);
                                malfunctionCount += 1;
                                setScheduledRepair(false);
                            }
                            if(busActionsLabel.getText().equals("Repairing")){
                                Platform.runLater(() -> busActionsLabel.setText("Travelling at "+getCurrentLocation().toString()));
                            }
                        }
                    }
                    // Arrived at City
                    advance();
                    // Repeat
                }
                letOutPassengers();
                distanceTravelled += 20;
                updateProgress(distanceTravelled, totalDistance);
                Platform.runLater(() -> busActionsLabel.setText("Finished"));
                return true;
            }
        };
    }

    private void pickUpPassengers(){
        if(getCurrentLocation() instanceof City){
            getPassengers().addAll(
                    ((City) getCurrentLocation()).pickUpPassengers(getPath())
            );
        }
    }

    private void letOutPassengers(){
        if(getCurrentLocation() instanceof City){
            getPassengers().removeIf(
                    passenger -> passenger.getDestination().equals(getCurrentLocation())
            );
        }
    }

    public void scheduleMaintenance(Maintenance maintenance){
        setMaintenance(maintenance);
        isMaintaining.set(true);
    }

    private void advance(){
        Objects.requireNonNull(getPath().poll()).removeBus(this);
        setCurrentLocation(getPath().peek());
        getCurrentLocation().addBus(this);
        Platform.runLater(() -> busCurrentLocationLabel.setText(getCurrentLocation().toString()));
    }

    public void readyState(Label statusLabel, Label currentLocation ,TableView<Passenger> passengerList, TitledPane root){
        this.busActionsLabel = statusLabel;
        this.busCurrentLocationLabel = currentLocation;
        this.passengerTableView = passengerList;
        this.busRootPane = root;
        statusLabel.setText("Ready");
        currentLocation.setText(getCurrentLocation().toString());
    }

    /////////////// Getters and Setters/////////////////////////////////////////////////////////////////////////


    public FloatProperty speedProperty() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed.set(speed);
    }

    public FloatProperty malfunctionChanceProperty() {
        return malfunctionChance;
    }

    public void setMalfunctionChance(float malfunctionChance) {
        this.malfunctionChance.set(malfunctionChance);
    }

    public TableView<Passenger> getPassengerTableView() {
        return passengerTableView;
    }

    public void setPassengers(ObservableList<Passenger> passengers) {
        this.passengers.set(passengers);
    }

    public Integer getID() {
        return ID;
    }

    public float getSpeed() {
        return speed.get();
    }

    public Integer getCapacity() {
        return capacity;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public float getMalfunctionChance() {
        return malfunctionChance.get();
    }

    public boolean isScheduledRepair() {
        return scheduledRepair.get();
    }

    public BooleanProperty scheduledRepairProperty() {
        return scheduledRepair;
    }

    public void setScheduledRepair(boolean scheduledRepair) {
        this.scheduledRepair.set(scheduledRepair);
    }


    public Maintenance getMaintenance() {
        return maintenance.get();
    }

    public ObjectProperty<Maintenance> maintenanceProperty() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance.set(maintenance);
    }

    public Malfunction getMalfunction() {
        return malfunction.get();
    }

    public ObjectProperty<Malfunction> malfunctionProperty() {
        return malfunction;
    }

    public void setMalfunction(Malfunction malfunction) {
        this.malfunction.set(malfunction);
    }

    public boolean isIsMaintaining() {
        return isMaintaining.get();
    }

    public BooleanProperty isMaintainingProperty() {
        return isMaintaining;
    }

    public void setIsMaintaining(boolean isMaintaining) {
        this.isMaintaining.set(isMaintaining);
    }

    public ObservableList<Passenger> getPassengers() {
        return passengers.get();
    }

    public ListProperty<Passenger> passengersProperty() {
        return passengers;
    }

    public LinkedList<Location> getPath() {
        return path.get();
    }

    public ObjectProperty<LinkedList<Location>> pathProperty() {
        return path;
    }

    public Location getCurrentLocation() {
        return currentLocation.get();
    }

    public ObjectProperty<Location> currentLocationProperty() {
        return currentLocation;
    }

    public Location getDestination() {
        return destination.get();
    }

    public ObjectProperty<Location> destinationProperty() {
        return destination;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation.set(currentLocation);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
