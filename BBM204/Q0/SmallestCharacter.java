import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SmallestCharacter {
    public static void main(String[] args) {
        // First command-line argument contains the path to the input file
        // Structured as below:
        // Line 0: <query-1> <query-2> <query-3> ...
        // Line 1: <word-1> <word-2> <word-3> ...

        // Your program should print to the standard output.
        // Your output should match the given format character-by-character.
        // For example for the sample input:
        // [1]
        // If there are more than one integer to print then:
        // [1,2,3,4,5]

        String[] words, queries;
        int[] answers;
        String[] lines = readFile(args[0]);

        queries = lines[0].split(" ");
        words = lines[1].split(" ");
        answers = new int[queries.length];

        int i = 0;
        for (String query : queries) {
            int num = 0;
            
            for (String word : words) {
                if(lexicographicalFrequency(query) < lexicographicalFrequency(word)) num++;
            }

            answers[i++] = num;
        }

        System.out.print(Arrays.toString(answers));
    }

    public static String[] readFile(String fileName) {
        File file = new File(fileName);
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }
            return lines.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found: " + fileName);
            return null;
        }
    }

    public static int lexicographicalFrequency(String str) {
        char smallestChar = Character.MAX_VALUE;
        int frequency = 0;
        
        for (char ch : str.toCharArray()) {
            if (ch < smallestChar) {
                smallestChar = ch;
                frequency = 1;
            } else if (ch == smallestChar) {
                frequency++;
            }
        }
        
        return frequency;
    }
}
