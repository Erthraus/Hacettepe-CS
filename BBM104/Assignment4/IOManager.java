import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOManager {
    public static final String queueInputPath = "queue.txt";
    public static final String stackInputPath = "stack.txt";
    public static final String queueOutputPath = "queueOut.txt";
    public static final String stackOutputPath = "stackOut.txt";

    public static String[] readInputFile(String path) {
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

    public static void writeToFile(String[] lines, String outputFilePath) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for(String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String line, String outputFilePath) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, true))) {
                writer.write(line);
                writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFile(String outputFilePath) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
