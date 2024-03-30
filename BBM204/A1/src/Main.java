import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Main {
    public static void main(String args[]) throws IOException {
        /*// X axis data
        int[] inputAxis = {512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 251282};

        // Create sample data for linear runtime
        double[][] yAxis = new double[2][10];
        yAxis[0] = new double[]{512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 251282};
        yAxis[1] = new double[]{300, 800, 1800, 3000, 7000, 15000, 31000, 64000, 121000, 231000};

        // Save the char as .png and show it
        showAndSaveChart("Sample Test", inputAxis, yAxis);

        int[] deneme = {3, 5, 4, 2, 1};
        printArray(Sort.countingSort(deneme, 5));
        System.out.println(Search.linearSearch(deneme, 4));

        int[] data = readIntegersFromCSV("src/TrafficFlowDataset.csv", 250000);
        int[] sortedData = Sort.countingSort(data, Arrays.stream(data).max().getAsInt());

        printArray(data);
        printArray(sortedData);*/

        int[] sampleSizes = new int[]{500, 1000, 2000, 4000, 8000, 16_000, 32_000, 64_000, 128_000, 250_000};
        //int[] data = readIntegersFromCSV("src/TrafficFlowDataset.csv");
        int[] data = readIntegersFromCSV(args[0]);
        int[] sortedData = data.clone();
        Arrays.sort(sortedData);
        int[] reverseSortedData = sortedData.clone();
        reverseArray(reverseSortedData);
        double[][] avgTimes = new double[3][sampleSizes.length];
        Sort[] sortAlgorithms = new Sort[]{new InsertionSort(), new MergeSort(), new CountingSort()};

        System.out.println("RANDOM DATA:");
        for (int i = 0; i < sortAlgorithms.length; i++) {
            avgTimes[i] = sortAlgorithms[i].test(sampleSizes, data);
        }
        showAndSaveChart("Tests on Random Data", sampleSizes, avgTimes, 0);
        
        System.out.println("\nSORTED DATA:");
        for (int i = 0; i < sortAlgorithms.length; i++) {
            avgTimes[i] = sortAlgorithms[i].test(sampleSizes, sortedData);
        }
        showAndSaveChart("Tests on Sorted Data", sampleSizes, avgTimes, 0);

        System.out.println("\nREVERSELY SORTED DATA:");
        for (int i = 0; i < sortAlgorithms.length; i++) {
            avgTimes[i] = sortAlgorithms[i].test(sampleSizes, reverseSortedData);
        }
        showAndSaveChart("Tests on Reversely Sorted Data", sampleSizes, avgTimes, 0);

        Random random = new Random();
        long start, end;
        double[][] avgSearchTimes = new double[3][sampleSizes.length];

        System.out.println("\nSEARCH TIME (RANDOM DATA):");
        for(int i = 0; i < sampleSizes.length; i++) {
            double[] results = new double[1000];
            int[] dataCopy = Arrays.copyOfRange(data, 0, sampleSizes[i]);

            for(int j = 0; j < 1000; j++) {             
                int randomIndex = random.nextInt(sampleSizes[i]);
                //System.out.println("Size: " + sampleSizes[i] + " Index: " + randomIndex);
                int wanted = dataCopy[randomIndex];
                start = System.nanoTime();
                int location = Search.linearSearch(dataCopy, wanted);
                end = System.nanoTime();
                results[j] = end - start;
                //System.out.println("Number " + wanted + " is found on index " + location);
            }

            avgSearchTimes[0][i] = Sort.averageTime(results);
            System.out.println("Linear Search Average Time for " + sampleSizes[i] + " samples: " + avgSearchTimes[0][i] + " nanoseconds");
        }

        System.out.println("\nSEARCH TIME (SORTED):");
        for(int i = 0; i < sampleSizes.length; i++) {
            double[] results = new double[1000];
            int[] dataCopy = Arrays.copyOfRange(sortedData, 0, sampleSizes[i]);

            for(int j = 0; j < 1000; j++) {
                int randomIndex = random.nextInt(sampleSizes[i]);
                int wanted = dataCopy[randomIndex];
                start = System.nanoTime();
                int location = Search.linearSearch(dataCopy, wanted);
                end = System.nanoTime();
                results[j] = end - start;
                //System.out.println("Number " + wanted + " is found on index " + location);
            }

            avgSearchTimes[1][i] = Sort.averageTime(results);
            System.out.println("Linear Search Average Time for " + sampleSizes[i] + " samples: " + avgSearchTimes[1][i] + " nanoseconds");
        }

        for(int i = 0; i < sampleSizes.length; i++) {
            double[] results = new double[1000];
            int[] dataCopy = Arrays.copyOfRange(sortedData, 0, sampleSizes[i]);

            for(int j = 0; j < 1000; j++) {
                int randomIndex = random.nextInt(sampleSizes[i]);
                int wanted = dataCopy[randomIndex];
                start = System.nanoTime();
                int location = Search.binarySearch(dataCopy, wanted);
                end = System.nanoTime();
                results[j] = end - start;
                //System.out.println("Number " + wanted + " is found on index " + location);
            }

            avgSearchTimes[2][i] = Sort.averageTime(results);
            System.out.println("Binary Search Average Time for " + sampleSizes[i] + " samples: " + avgSearchTimes[2][i] + " nanoseconds");
        }

        showAndSaveChart("Tests on Search Algorithms", sampleSizes, avgSearchTimes, 1);
    }

    public static void showAndSaveChart(String title, int[] xAxis, double[][] yAxis, int flag) throws IOException {
        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title(title)
                .yAxisTitle("Time in Milliseconds").xAxisTitle("Input Size").build();

        // Convert x axis to double[]
        double[] doubleX = Arrays.stream(xAxis).asDoubleStream().toArray();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        // Add a plot for a sorting algorithm
        if(flag == 0) {
            chart.addSeries("Insertion Sort", doubleX, yAxis[0]);
            chart.addSeries("Merge Sort", doubleX, yAxis[1]);
            chart.addSeries("Counting Sort", doubleX, yAxis[2]);
        } else {
            chart.addSeries("Linear Search (Random Data)", doubleX, yAxis[0]);
            chart.addSeries("Linear Search (Sorted Data)", doubleX, yAxis[1]);
            chart.addSeries("Binary Search (Sorted Data)", doubleX, yAxis[2]);
        }

        // Save the chart as PNG
        BitmapEncoder.saveBitmap(chart, title + ".png", BitmapEncoder.BitmapFormat.PNG);

        // Show the chart
        new SwingWrapper<XYChart>(chart).displayChart();
    }

    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void printArray2(int[] arr) {
        for (int i = 0; i < 10; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static int[] readIntegersFromCSV(String filePath) throws IOException {
        List<Integer> integersList = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                if (parts.length >= 7) {
                    try {
                        int value = Integer.parseInt(parts[6].trim());
                        integersList.add(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid integer: " + parts[6]);
                    }
                } else {
                    System.err.println("Line does not have enough columns: " + line);
                }
            }
        }

        int[] integersArray = new int[integersList.size()];
        for (int i = 0; i < integersList.size(); i++) {
            integersArray[i] = integersList.get(i);
        }

        return integersArray;
    }

    public static void reverseArray(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
    }
}
