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
        Integer[] nums = readFile(args[1]);

        Arrays.sort(nums);

        for(int i = 0; i < numOfIntegers; i++) {
            if(nums[i] != i) {
                System.out.print(i);
                System.exit(0);
            }
        }

        System.out.print(numOfIntegers);
    }

    public static Integer[] readFile(String fileName) {
        File file = new File(fileName);
        List<Integer> nums = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextInt()) {
                int num = scanner.nextInt();
                nums.add(num);
            }
            return nums.toArray(new Integer[0]);
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found: " + fileName);
            return null;
        }
    }
}