import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class MissingNumber {

    public static void main(String[] args) {

        // First command-line argument refers to the number of integers
        // Second command-line argument contains the path to the input file
        // Your program should only print a single integer to the standard output
        // For the sample input, your output should be:
        // 2

        int numOfIntegers = Integer.parseInt(args[0]);
        String[] lines = readFile(args[1]);
        int[] nums = new int[numOfIntegers];
        String[] parts = lines[0].split(" ");

        for(int i = 0; i < numOfIntegers; i++) {
            nums[i] = Integer.parseInt(parts[i]);
        }

        Arrays.sort(nums);

        for(int i = 0; i < numOfIntegers; i++) {
            if(nums[i] != i) {
                System.out.print(i);
                System.exit(0);
            }
        }

        System.out.print(numOfIntegers);
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
}