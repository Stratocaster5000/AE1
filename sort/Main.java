package sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final int NUM_RUNS = 10; // Number of runs per file
    private static final double TIMEOUT_SECONDS = 1.0; // Timeout in seconds
    private static final Map<String, Map<String, List<Long>>> fileAlgorithmTimes = new HashMap<>();

    public static void main(String[] args) {
        // Define the path to the data directory
        String dataDirectoryPath = "E:/OneDrive - University of Glasgow/Documents/AE1/AE1/data";
        File dataDirectory = new File(dataDirectoryPath);

        // Check if the data directory exists and is a directory
        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
            System.err.println("Error: The directory '" + dataDirectoryPath + "' does not exist or is not a directory.");
            return;
        }

        // List all files in the data directory
        File[] files = dataDirectory.listFiles();

        // Check if files were successfully read from the directory
        if (files == null) {
            System.err.println("Error: Could not read files from the directory.");
            return;
        }

        // Process each file in the directory
        for (File file : files) {
            // Filter files based on their name
            if (file.isFile() && file.getName().startsWith("int") && file.getName().endsWith(".txt")) {//Change prefix to match the files you want to run
                System.out.println("\nProcessing file: " + file.getName());

                // Read numbers from the file
                List<Integer> numbers = readNumbersFromFile(file);
                if (numbers.isEmpty()) {
                    continue;
                }

                // Convert the list of numbers to an array
                int[] arr = numbers.stream().mapToInt(Integer::intValue).toArray();

                // Initialize a map to store algorithm execution times
                Map<String, List<Long>> algorithmTimes = new HashMap<>();
                for (int run = 1; run <= NUM_RUNS; run++) {
                    System.out.println("\nRun " + run + ":");
                    // Run sorting algorithms and measure their execution times
                    runSortingAlgorithms(arr, algorithmTimes);
                }

                // Store the execution times for the current file
                fileAlgorithmTimes.put(file.getName(), algorithmTimes);
            }
        }

        // Print average execution times for each file
        printAverageTimingsPerFile();
    }

    // Read numbers from a file and return them as a list of integers
    private static List<Integer> readNumbersFromFile(File file) {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    numbers.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping non-integer line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
        }
        return numbers;
    }

    // Run multiple sorting algorithms on the given array and measure their execution times
    private static void runSortingAlgorithms(int[] arr, Map<String, List<Long>> algorithmTimes) {
        runSortWithTimeout("ShellSort", () -> ShellSort.sort(arr.clone()), algorithmTimes);
        runSortWithTimeout("SelectionSort", () -> SelectionSort.sort(arr.clone()), algorithmTimes);
        runSortWithTimeout("QuickSortInsertion", () -> QuickSortInsertion.sortCutOff(arr.clone(), 0, arr.length - 1, 10), algorithmTimes);
        runSortWithTimeout("MergeSort", () -> MergeSort.sort(arr.clone(), 0, arr.length - 1), algorithmTimes);
        runSortWithTimeout("QuickSort", () -> QuickSort.sort(arr.clone(), 0, arr.length - 1), algorithmTimes);
        runSortWithTimeout("Median3Sort", () -> Median3Sort.sort(arr.clone(), 0, arr.length - 1), algorithmTimes);
        runSortWithTimeout("InsertionSort", () -> InsertionSort.sort(arr.clone(), 0, arr.length), algorithmTimes);
        runSortWithTimeout("BottomUpMergeSort", () -> BottomUpMergeSort.sort(arr.clone(), 0, arr.length - 1), algorithmTimes);
        runSortWithTimeout("DutchFlagSort", () -> DutchFlagSort.sort3Way(arr.clone(), 0, arr.length - 1), algorithmTimes);
    }

    // Run a sorting algorithm with a timeout and measure its execution time
    private static void runSortWithTimeout(String sortName, Runnable sortTask, Map<String, List<Long>> algorithmTimes) {
        System.out.println("Starting " + sortName);
        long startTime = System.nanoTime();

        Thread sortThread = new Thread(sortTask);
        sortThread.start();

        try {
            sortThread.join((long) (TIMEOUT_SECONDS * 1000));
            if (sortThread.isAlive()) {
                System.err.printf("%s timed out after %.2f seconds%n", sortName, TIMEOUT_SECONDS);
                sortThread.interrupt();
            } else {
                long duration = System.nanoTime() - startTime;
                algorithmTimes.computeIfAbsent(sortName, k -> new ArrayList<>()).add(duration);
                System.out.printf("%s took: %d nanoseconds%n", sortName, duration);
            }
        } catch (InterruptedException e) {
            System.err.printf("%s encountered an error: %s%n", sortName, e.getMessage());
        }

        System.out.println("Finished " + sortName);
    }

    // Print average execution times for each sorting algorithm per file
    private static void printAverageTimingsPerFile() {
        System.out.println("\nAverage Execution Times per File:");
        for (Map.Entry<String, Map<String, List<Long>>> fileEntry : fileAlgorithmTimes.entrySet()) {
            System.out.println("File: " + fileEntry.getKey());
            for (Map.Entry<String, List<Long>> algorithmEntry : fileEntry.getValue().entrySet()) {
                String algorithmName = algorithmEntry.getKey();
                List<Long> times = algorithmEntry.getValue();
                double average = times.stream().mapToLong(Long::longValue).average().orElse(0);
                System.out.printf("%s: %.2f nanoseconds%n", algorithmName, average);
            }
            System.out.println();
        }
    }
}
