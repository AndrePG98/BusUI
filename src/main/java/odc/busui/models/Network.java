package odc.busui.models;

import odc.busui.models.entities.Passenger;
import odc.busui.models.locations.City;
import odc.busui.models.locations.Location;
import odc.busui.models.locations.Road;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Stream;

public class Network {
    private final Graph<City, Road> map = new SimpleGraph<>(Road.class);
    private final List<City> cities = new ArrayList<>();
    private final List<Road> roads = new ArrayList<>();

    public Network() {
        addCities();
        addRoads();
        setUpGraph();
    }

    private void addCities() {
        cities.add(new City(1, "Lisboa")); // 0
        cities.add(new City(cities.size() + 1, "Santarém")); // 1
        cities.add(new City(cities.size()+1, "Leiria")); // 2
        cities.add(new City(cities.size()+1, "Coimbra")); // 3
        cities.add(new City(cities.size()+1, "Aveiro")); // 4
        cities.add(new City(cities.size()+1, "Porto")); // 5
        cities.add(new City(cities.size()+1, "Braga")); // 6
        cities.add(new City(cities.size()+1, "Viana do Castelo")); // 7
        cities.add(new City(cities.size()+1, "Setúbal")); // 8
        cities.add(new City(cities.size()+1, "Évora")); // 9
        cities.add(new City(cities.size()+1, "Beja")); // 10
        cities.add(new City(cities.size()+1, "Grândola")); // 11
        cities.add(new City(cities.size()+1, "Faro")); // 12
        cities.add(new City(cities.size()+1, "Castelo Branco")); // 13
        cities.add(new City(cities.size()+1, "Guarda")); // 14
    }

    private void addRoads(){
        roads.add(new Road(1, 81, cities.subList(0,2))); // Lisboa Santarem 0
        roads.add(new Road(roads.size()+1, 77, cities.subList(1,3))); // Santarem Leiria 1
        roads.add(new Road(roads.size()+1, 75, cities.subList(2,4))); // Leiria Coimbra 2
        roads.add(new Road(roads.size()+1, 65, cities.subList(3,5))); // Coimbra Aveiro 3
        roads.add(new Road(roads.size()+1, 56, cities.subList(4,6))); // Aveiro Porto 4
        roads.add(new Road(roads.size()+1, 56, cities.subList(5,7))); // Porto Braga 5
        roads.add(new Road(roads.size()+1, 62, cities.subList(6,8))); // Braga Viana 6
        roads.add(new Road(roads.size()+1, 159, List.of(cities.get(1), cities.get(13)))); // Santarem CasteloBranco 7
        roads.add(new Road(roads.size()+1, 96, List.of(cities.get(13), cities.get(14)))); // CasteloBranco Guarda 8
        roads.add(new Road(roads.size()+1, 154, List.of(cities.get(3), cities.get(14)))); // Coimbra Guarda 9
        roads.add(new Road(roads.size()+1, 50, List.of(cities.get(0), cities.get(8)))); // Lisboa Setubal 10
        roads.add(new Road(roads.size()+1, 100, List.of(cities.get(8), cities.get(9)))); // Setubal Evora 11
        roads.add(new Road(roads.size()+1, 61, List.of(cities.get(9), cities.get(10)))); // Evora Beja 12
        roads.add(new Road(roads.size()+1, 84, List.of(cities.get(8), cities.get(11)))); // Setubal Grandola 13
        roads.add(new Road(roads.size()+1, 174, List.of(cities.get(11), cities.get(12)))); // Grandola Faro 14
    }

    private void setUpGraph(){
        cities.forEach(map::addVertex);
        roads.forEach(road -> map.addEdge(road.getConnectingCities().get(0),road.getConnectingCities().get(1), road));
    }

    private City getCity(int id){
        Optional<City> citySearch = map.vertexSet().stream().filter(city -> city.getId() == id).findFirst();
        return citySearch.orElse(null);
    }

    private Road getRoad(int id){
        Optional<Road> roadSearch = map.edgeSet().stream().filter(road -> road.getId() == id).findFirst();
        return roadSearch.orElse(null);
    }

    public List<Passenger> getAllPassengers(){
        List<Passenger> passengerList = new ArrayList<>();
        cities.forEach(city -> passengerList.addAll(city.getPassengerList()));
        return passengerList;
    }

    public LinkedList<Location> getRandomPath(){
        LinkedList<Location> path = new LinkedList<>();
        City startingCity;
        City endingCity;
        do {
            startingCity = cities.get(new Random().nextInt(cities.size()));
            endingCity = cities.get(new Random().nextInt(cities.size()));
        } while (startingCity.equals(endingCity));
        GraphPath<City , Road> roadGraphPath = DijkstraShortestPath.findPathBetween(map, startingCity, endingCity);
        Iterator<City> cityIterator = roadGraphPath.getVertexList().iterator();
        Iterator<Road> roadIterator = roadGraphPath.getEdgeList().iterator();
        while(cityIterator.hasNext() && roadIterator.hasNext()){
            path.add(cityIterator.next());
            path.add(roadIterator.next());
        }
        path.add(cityIterator.next());
        return path;
    }

    public void addPassenger(Passenger passenger){
        Optional<City> cityOptional = cities.stream().filter(city -> city.equals(passenger.getOrigin())).findFirst();
        cityOptional.ifPresent(city -> city.addPassenger(passenger));
    }
}
