package odc.busui.models.locations;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import odc.busui.models.entities.Passenger;

import java.util.*;

public class City extends Location {
    private final StringProperty name;
    private final ListProperty<Passenger> passengerList;


    public City(int id, String name) {
        super(id);
        this.name = new SimpleStringProperty(name);
        passengerList = new SimpleListProperty<>(
                FXCollections.observableList(
                        Collections.synchronizedList(
                                new ArrayList<>()
                        )
                )
        );
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void addPassenger(Passenger passenger){
        synchronized (passengerList){
            passengerList.add(passenger);
        }
    }

    public void removePassenger(Passenger passenger){
        synchronized (passengerList){
            passengerList.remove(passenger);
        }
    }

    public List<Passenger> pickUpPassengers(List<Location> path){
        List<Passenger>leavingPassengers = new ArrayList<>();
        synchronized (passengerList){
            for (Passenger passenger:passengerList) {
                if (path.contains(passenger.getDestination())){
                    leavingPassengers.add(passenger);
                }
            }
            passengerList.removeAll(leavingPassengers);
        }
        return leavingPassengers;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
