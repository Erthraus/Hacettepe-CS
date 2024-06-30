import java.io.Serializable;
import java.util.*;

class UrbanTransportationApp implements Serializable {
    static final long serialVersionUID = 99L;

    public HyperloopTrainNetwork readHyperloopTrainNetwork(String filename) {
        HyperloopTrainNetwork hyperloopTrainNetwork = new HyperloopTrainNetwork();
        hyperloopTrainNetwork.readInput(filename);
        return hyperloopTrainNetwork;
    }

    public List<RouteDirection> getFastestRouteDirections(HyperloopTrainNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();

        Station startPoint = network.startPoint;
        Station endPoint = network.destinationPoint;

        // Find the nearest stations to the start and end points
        Station nearestStartStation = findNearestStation(network, startPoint);
        Station nearestEndStation = findNearestStation(network, endPoint);

        // Perform Dijkstra's algorithm to find the shortest path
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, RouteDirection> previousStations = new HashMap<>();
        PriorityQueue<StationDistance> pq = new PriorityQueue<>();

        // Initialize distances and priority queue
        for (TrainLine line : network.lines) {
            for (Station station : line.trainLineStations) {
                distances.put(station, Double.MAX_VALUE);
            }
        }
        distances.put(nearestStartStation, 0.0);
        pq.add(new StationDistance(nearestStartStation, 0.0));

        while (!pq.isEmpty()) {
            StationDistance current = pq.poll();
            Station currentStation = current.station;

            if (currentStation.equals(nearestEndStation)) {
                break;
            }

            for (Station neighbor : getNeighbors(network, currentStation)) {
                boolean isTrainRide = areStationsInSameLine(network, currentStation, neighbor);
                double travelTime = isTrainRide ?
                        calculateDistance(currentStation.coordinates, neighbor.coordinates) / network.averageTrainSpeed :
                        calculateDistance(currentStation.coordinates, neighbor.coordinates) / network.averageWalkingSpeed;
                double newDist = distances.get(currentStation) + travelTime;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousStations.put(neighbor, new RouteDirection(currentStation.description, neighbor.description, travelTime, isTrainRide));
                    pq.add(new StationDistance(neighbor, newDist));
                }
            }
        }

        // Construct the shortest path
        Station step = nearestEndStation;
        while (previousStations.get(step) != null) {
            routeDirections.add(previousStations.get(step));
            step = getStationByName(network, previousStations.get(step).startStationName);
        }
        Collections.reverse(routeDirections);

        // Add walking directions to the first station
        double walkingDistance = calculateDistance(startPoint.coordinates, nearestStartStation.coordinates);
        routeDirections.add(0, new RouteDirection(startPoint.description, nearestStartStation.description, walkingDistance / network.averageWalkingSpeed, false));

        // Add walking directions from the last station to the destination
        double lastWalkingDistance = calculateDistance(nearestEndStation.coordinates, endPoint.coordinates);
        routeDirections.add(new RouteDirection(nearestEndStation.description, endPoint.description, lastWalkingDistance / network.averageWalkingSpeed, false));

        return routeDirections;
    }

    private Station findNearestStation(HyperloopTrainNetwork network, Station point) {
        Station nearestStation = null;
        double minDistance = Double.MAX_VALUE;
        for (TrainLine line : network.lines) {
            for (Station station : line.trainLineStations) {
                double distance = calculateDistance(station.coordinates, point.coordinates);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestStation = station;
                }
            }
        }
        return nearestStation;
    }

    private double calculateDistance(Point point1, Point point2) {
        double dx = point1.x - point2.x;
        double dy = point1.y - point2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<Station> getNeighbors(HyperloopTrainNetwork network, Station station) {
        List<Station> neighbors = new ArrayList<>();
        for (TrainLine line : network.lines) {
            int index = line.trainLineStations.indexOf(station);
            if (index != -1) {
                if (index > 0) {
                    neighbors.add(line.trainLineStations.get(index - 1));
                }
                if (index < line.trainLineStations.size() - 1) {
                    neighbors.add(line.trainLineStations.get(index + 1));
                }
            }
        }

        // Add potential neighbors for walking between different lines
        for (TrainLine line : network.lines) {
            if (!line.trainLineStations.contains(station)) {
                for (Station s : line.trainLineStations) {
                    if (calculateDistance(station.coordinates, s.coordinates) <= 10) { // Threshold for changing lines
                        neighbors.add(s);
                    }
                }
            }
        }

        return neighbors;
    }

    private boolean areStationsInSameLine(HyperloopTrainNetwork network, Station station1, Station station2) {
        for (TrainLine line : network.lines) {
            if (line.trainLineStations.contains(station1) && line.trainLineStations.contains(station2)) {
                return true;
            }
        }
        return false;
    }

    private Station getStationByName(HyperloopTrainNetwork network, String name) {
        for (TrainLine line : network.lines) {
            for (Station station : line.trainLineStations) {
                if (station.description.equals(name)) {
                    return station;
                }
            }
        }
        return null;
    }

    public void printRouteDirections(List<RouteDirection> directions) {
        System.out.println("The fastest route takes " + getTotalDuration(directions) + " minute(s).");
        System.out.println("Directions");
        System.out.println("----------");
        int stepCount = 1;
        for (RouteDirection direction : directions) {
            String start = direction.startStationName;
            String end = direction.endStationName;
            double duration = direction.duration;
            String transport = direction.trainRide ? "Get on the train from" : "Walk from";
            System.out.printf("%d. %s \"%s\" to \"%s\" for %.2f minutes.%n", stepCount++, transport, start, end, duration);
        }
    }

    private int getTotalDuration(List<RouteDirection> directions) {
        double totalDuration = 0.0;
        for (RouteDirection direction : directions) {
            totalDuration += direction.duration;
        }
        return (int) Math.round(totalDuration);
    }

    private static class StationDistance implements Comparable<StationDistance> {
        Station station;
        double distance;

        StationDistance(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        @Override
        public int compareTo(StationDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
