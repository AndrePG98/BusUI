package odc.busui.models.locations;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Road extends Location{
    private final ListProperty<City> connectingCities;
    private final IntegerProperty length;


    public Road(int id, int length, List<City> connectingCities) {
        super(id);
        this.length = new SimpleIntegerProperty(length);
        this.connectingCities = new SimpleListProperty<>(FXCollections.observableList(connectingCities));
    }

    public ObservableList<City> getConnectingCities() {
        return connectingCities.get();
    }

    public ListProperty<City> connectingCitiesProperty() {
        return connectingCities;
    }

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        return length;
    }

    @Override
    public String toString() {
        return connectingCities.get().toString();
    }
}
