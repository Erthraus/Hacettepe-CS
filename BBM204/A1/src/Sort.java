import java.util.Arrays;

public abstract class Sort {
    public String name;

    public abstract int[] sort(int[] data);

    public double[] test(int[] sampleSizes, int[] data) {
        long start, end;
        double avgTimes[] = new double[sampleSizes.length];
        for (int i = 0; i < sampleSizes.length; i++) {
            double[] results = new double[10];
            int[] dataCopy = Arrays.copyOfRange(data, 0, sampleSizes[i]);

            for(int j = 0; j < 10; j++) {
                start = System.currentTimeMillis();
                //Main.printArray(dataCopy);
                int[] sorted = sort(dataCopy);
                //Main.printArray(sorted);
                end = System.currentTimeMillis();
                results[j] = end - start;
                //System.out.println(isSortedAscending(sorted));
            }

            avgTimes[i] = averageTime(results);
            System.out.println(name + " Average Time for " + sampleSizes[i] + " samples: " + avgTimes[i] + " Miliseconds");
        }
        
        return avgTimes;
    }

    public static double averageTime(double[] results) {
        double sum = 0;

        for (double i : results) {
            sum += i;
        }

        return sum/results.length;
    }

    public static boolean isSortedAscending(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return true; // Already sorted or empty array
        }

        int n = arr.length;
        for (int i = 1; i < n; i++) {
            if (arr[i] < arr[i - 1]) {
                return false; // Not sorted in ascending order
            }
        }
        return true; // Array is sorted in ascending order
    }
}
