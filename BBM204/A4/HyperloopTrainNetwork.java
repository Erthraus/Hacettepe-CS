import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperloopTrainNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageTrainSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;
    public int numTrainLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<TrainLine> lines;

    /**
     * Method with a Regular Expression to extract integer numbers from the fileContent
     * @return the result as int
     */
    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract string constants from the fileContent
     * @return the result as String
     */
    public String getStringVar(String varName, String fileContent) {
        // TODO: Your code goes here
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\"(.*?)\"");
        Matcher m = p.matcher(fileContent);
        m.find();
        return m.group(1);
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the fileContent
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */
    public Double getDoubleVar(String varName, String fileContent) {
        // TODO: Your code goes here
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+\\.?[0-9]*)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Double.parseDouble(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the fileContent
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * @return the result as a Point object
     */
    public Point getPointVar(String varName, String fileContent) {
        Point p = new Point(0, 0);
        // TODO: Your code goes here
        Pattern pattern = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=\\s*\\(\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\)");
        Matcher m = pattern.matcher(fileContent);
        m.find();
        p.x = Integer.parseInt(m.group(1));
        p.y = Integer.parseInt(m.group(2));
        return p;
    } 

    /**
     * Function to extract the train lines from the fileContent by reading train line names and their 
     * respective stations.
     * @return List of TrainLine instances
     */
    public List<TrainLine> getTrainLines(String fileContent) {
        List<TrainLine> trainLines = new ArrayList<>();

        // TODO: Your code goes here
        // Regular expression to match train line name and its stations
        Pattern linePattern = Pattern.compile(
                "train_line_name\\s*=\\s*\"(.*?)\"\\s*train_line_stations\\s*=\\s*((?:\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*)+)"
        );

        Matcher lineMatcher = linePattern.matcher(fileContent);
        while (lineMatcher.find()) {
            String lineName = lineMatcher.group(1);
            String stationsStr = lineMatcher.group(2);

            // Regular expression to match individual stations within the stations string
            Pattern stationPattern = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            Matcher stationMatcher = stationPattern.matcher(stationsStr);

            int counter = 0;
            List<Station> stationPoints = new ArrayList<>();
            while (stationMatcher.find()) {
                String description = lineName + " Line Station " + ++counter;
                int x = Integer.parseInt(stationMatcher.group(1));
                int y = Integer.parseInt(stationMatcher.group(2));
                Point point = new Point(x, y);
                stationPoints.add(new Station(point, description));
            }

            TrainLine trainLine = new TrainLine(lineName, stationPoints);
            trainLines.add(trainLine);
        }
        
        return trainLines;
    }

    /**
     * Function to populate the given instance variables of this class by calling the functions above.
     */
    public void readInput(String filename) {

        // TODO: Your code goes here
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileContent = contentBuilder.toString();

        numTrainLines = getIntVar("num_train_lines", fileContent);
        startPoint = new Station(getPointVar("starting_point", fileContent), "Starting Point");
        destinationPoint = new Station(getPointVar("destination_point", fileContent), "Final Destination");
        averageTrainSpeed = getDoubleVar("average_train_speed", fileContent) * 1000 / 60;
        lines = getTrainLines(fileContent);

        /*System.out.println("Number of Train Lines: " + numTrainLines);
        System.out.println("Start Point: " + startPoint.coordinates.x + " " + startPoint.coordinates.y);
        System.out.println("Destination Point: " + destinationPoint.coordinates.x + " " + destinationPoint.coordinates.y);
        System.out.println("Average Train Speed: " + averageTrainSpeed);

        for (TrainLine trainLine : lines) {
            System.out.println("Train Lines: " + trainLine.trainLineName);
            for (Station station : trainLine.trainLineStations) {
                System.out.println("Point: " + station.coordinates.x + " " + station.coordinates.y);
            }
        }*/     
    }
}