import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Sports[] sports = {new IceHockey(), new HandBall()};
        for(Sports sport : sports) {
            sport.readData(args[0]);
            sport.rankTeams();
            sport.output();
        }
    }

    public static String[] readFile(String path) {
        try{
            int i = 0;
            Path path1 = Paths.get(path);
            int length = Files.readAllLines(path1).size();
            String[] results = new String[length];
            for (String line : Files.readAllLines(path1)) {
                results[i++] = line;
            }
            return results;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
