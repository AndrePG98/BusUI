package odc.busui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import odc.busui.controllers.busComponentController;
import odc.busui.controllers.mainSceneController;
import odc.busui.models.Network;
import odc.busui.models.entities.Bus;
import odc.busui.models.locations.Location;
import odc.busui.models.locations.Road;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class mainApplication extends Application {
    private final Network network;
    private final Map<Integer, Bus> busMap;


    public mainApplication() {
        this.busMap = new HashMap<>();
        this.network = new Network();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainFxmlLoader = new FXMLLoader(mainApplication.class.getResource("main-scene.fxml"));
        mainFxmlLoader.setController(new mainSceneController(this));
        Scene scene = new Scene(mainFxmlLoader.load());
        stage.setTitle("BusUI!");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }

    public Network getNetwork() {
        return network;
    }

    public Map<Integer, Bus> getBusMap() {
        return busMap;
    }

    public int getNextBusID(){
        return busMap.size()+1;
    }
}