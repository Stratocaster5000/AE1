package sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final int NUM_RUNS = 10; // Number of runs per file
    private static final double TIMEOUT_SECONDS = 3.0; // Timeout in seconds
    private static final Map<String, Map<String, List<Long>>> fileAlgorithmTimes = new HashMap<>();

    public static void main(String[] args) {
        String dataDirectoryPath = "E:/OneDrive - University of Glasgow/Documents/AE1/AE1/data";
        File dataDirectory = new File(dataDirectoryPath);

        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
            System.err.println("Error: The directory '" + dataDirectoryPath + "' does not exist or is not a directory.");
            return;
        }

        File[] files = dataDirectory.listFiles();

        if (files == null) {
            System.err.println("Error: Could not read files from the directory.");
            return;
        }

        // Run each sorting algorithm one at a time
        runSortingAlgorithm(files, "ShellSort", arr -> ShellSort.sort(arr));
        runSortingAlgorithm(files, "SelectionSort", arr -> SelectionSort.sort(arr));
        runSortingAlgorithm(files, "QuickSortInsertion", arr -> QuickSortInsertion.sortCutOff(arr, 0, arr.length - 1, 10));
        runSortingAlgorithm(files, "MergeSort", arr -> MergeSort.sort(arr, 0, arr.length - 1));
        runSortingAlgorithm(files, "QuickSort", arr -> QuickSort.sort(arr, 0, arr.length - 1));
        runSortingAlgorithm(files, "Median3Sort", arr -> Median3Sort.sort(arr, 0, arr.length - 1));
        runSortingAlgorithm(files, "InsertionSort", arr -> InsertionSort.sort(arr, 0, arr.length));
        runSortingAlgorithm(files, "BottomUpMergeSort", arr -> BottomUpMergeSort.sort(arr, 0, arr.length - 1));
        runSortingAlgorithm(files, "DutchFlagSort", arr -> DutchFlagSort.sort3Way(arr, 0, arr.length - 1));

        printAverageTimingsPerFile();
    }

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

    private static void runSortingAlgorithm(File[] files, String algorithmName, SortingAlgorithm algorithm) {
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith("int") && file.getName().endsWith(".txt")) {
                System.out.println("\nProcessing file: " + file.getName());

                List<Integer> numbers = readNumbersFromFile(file);
                if (numbers.isEmpty()) {
                    continue;
                }

                int[] arr = numbers.stream().mapToInt(Integer::intValue).toArray();

                Map<String, List<Long>> algorithmTimes = fileAlgorithmTimes.computeIfAbsent(file.getName(), k -> new HashMap<>());

                for (int run = 1; run <= NUM_RUNS; run++) {
                    System.out.println("\nRun " + run + ":");
                    runSortWithTimeout(algorithmName, () -> algorithm.sort(arr.clone()), algorithmTimes);
                }
            }
        }
    }

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

    @FunctionalInterface
    private interface SortingAlgorithm {

        void sort(int[] arr);
    }
}
