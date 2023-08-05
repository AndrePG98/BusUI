package odc.busui.models.locations;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import odc.busui.models.entities.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Location{

    private final IntegerProperty id;
    private final ListProperty<Bus> busList;


    public Location(int id) {
        this.id = new SimpleIntegerProperty(id);
        busList = new SimpleListProperty<>(
                FXCollections.observableList(
                        Collections.synchronizedList(
                                new ArrayList<>()
                        )
                )
        );
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public List<Bus> getBusList() {
        return busList;
    }

    public void addBus(Bus bus){
        synchronized (busList){
            busList.add(bus);
        }
    }

    public void removeBus(Bus bus){
        synchronized (busList){
            busList.remove(bus);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(id.get());
    }
}

