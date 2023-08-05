package odc.busui.models.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import odc.busui.models.locations.City;

public class Passenger {
    private final IntegerProperty id;
    private final ObjectProperty<City> origin;
    private final ObjectProperty<City> destination;

    public Passenger(int id, City origin, City destination){
        this.id = new SimpleIntegerProperty(id);
        this.origin = new SimpleObjectProperty<>(origin);
        this.destination = new SimpleObjectProperty<>(destination);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public City getOrigin() {
        return origin.get();
    }

    public ObjectProperty<City> originProperty() {
        return origin;
    }

    public City getDestination() {
        return destination.get();
    }

    public ObjectProperty<City> destinationProperty() {
        return destination;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + id +
                ", origin=" + origin.get() +
                ", destination=" + destination.get() +
                '}';
    }
}
