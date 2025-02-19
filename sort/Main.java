package sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        // Specify the directory containing the data files
        String dataDirectoryPath = "E:/OneDrive - University of Glasgow/Documents/AE1/AE1/data";
        File dataDirectory = new File(dataDirectoryPath);

        // Check if the directory exists
        if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
            System.err.println("Error: The directory '" + dataDirectoryPath + "' does not exist or is not a directory.");
            return;
        }

        // Get the list of files in the directory
        File[] files = dataDirectory.listFiles();

        if (files == null) {
            System.err.println("Error: Could not read files from the directory.");
            return;
        }

        // Create a single ExecutorService for all files
        ExecutorService executor = Executors.newFixedThreadPool(6); // Use a thread pool for concurrency
        final long TIMEOUT_SECONDS = 1;

        // Iterate through the files and process those starting with "int"
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith("int") && file.getName().endsWith(".txt")) {
                System.out.println("Processing file: " + file.getName());

                // Read data from the file
                List<Integer> numbers = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            int number = Integer.parseInt(line.trim());
                            numbers.add(number);
                        } catch (NumberFormatException e) {
                            System.err.println("Skipping non-integer line: " + line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                    continue; // Skip to the next file
                }

                // Convert the list to an array
                int[] arr = new int[numbers.size()];
                for (int i = 0; i < numbers.size(); i++) {
                    arr[i] = numbers.get(i);
                }

                // --- ShellSort with Timeout ---
                int[] arrShell = arr.clone();
                processSort(executor, "ShellSort", () -> ShellSort.sort(arrShell), TIMEOUT_SECONDS);

                // --- SelectionSort with Timeout ---
                int[] arrSelection = arr.clone();
                processSort(executor, "SelectionSort", () -> SelectionSort.sort(arrSelection), TIMEOUT_SECONDS);

                // --- QuickSortInsertion with Timeout ---
                int[] arrQuickInsertion = arr.clone();
                int k = 10; // Or whatever cutoff you're using
                processSort(executor, "QuickSortInsertion", () -> QuickSortInsertion.sortCutOff(arrQuickInsertion, 0, arrQuickInsertion.length - 1, k), TIMEOUT_SECONDS);

                // --- DutchFlagSort with Timeout ---
                int[] arrDutchFlag = arr.clone();
                processSort(executor, "DutchFlagSort", () -> DutchFlagSort.dutchFlagSort(arrDutchFlag), TIMEOUT_SECONDS);

                // --- MergeSort with Timeout ---
                int[] arrMerge = arr.clone();
                processSort(executor, "MergeSort", () -> MergeSort.sort(arrMerge, 0, arrMerge.length - 1), TIMEOUT_SECONDS);

                // --- QuickSort with Timeout ---
                int[] arrQuick = arr.clone();
                processSort(executor, "QuickSort", () -> QuickSort.sort(arrQuick, 0, arrQuick.length - 1),
                        TIMEOUT_SECONDS);

                // --- Median3Sort with Timeout ---
                int[] arrMedian3 = arr.clone();
                processSort(executor, "Median3Sort", () -> Median3Sort.sort(arrMedian3, 0, arrMedian3.length - 1),
                        TIMEOUT_SECONDS);

                // --- InsertionSort with Timeout ---  
                int[] arrInsertion = arr.clone();
                processSort(executor, "InsertionSort", () -> InsertionSort.sort(arrInsertion, 0, arrInsertion.length),
                        TIMEOUT_SECONDS);

                // --- BottomUpMergeSort with Timeout ---
                int[] arrBottomUpMerge = arr.clone();
                processSort(executor, "BottomUpMergeSort", () -> BottomUpMergeSort.sort(arrBottomUpMerge, 0, arrBottomUpMerge.length - 1),
                        TIMEOUT_SECONDS);
            }
        }

        // Shut down the executor after processing all files
        executor.shutdownNow();
        Thread.currentThread().interrupt();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("All files processed.");
    }

    private static void processSort(ExecutorService executor, String sortName, Runnable sortTask, long timeoutSeconds) {
        try {
            Future<?> future = executor.submit(() -> {
                long startTime = System.nanoTime();
                sortTask.run();
                long endTime = System.nanoTime();
                return endTime - startTime;
            });

            try {
                long duration = (Long) future.get(timeoutSeconds, TimeUnit.SECONDS);
                System.out.println(sortName + " took: " + duration + " nanoseconds");
            } catch (TimeoutException e) {
                System.err.println(sortName + " timed out after " + timeoutSeconds + " seconds");
                future.cancel(true); //Interrupt the task if it times out
            } catch (Exception e) {
                System.err.println(sortName + " error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error submitting " + sortName + ": " + e.getMessage());
        }
    }
}
