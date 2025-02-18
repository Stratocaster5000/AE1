
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import sort.QuickSortInsertion;
import sort.SelectionSort;
import sort.ShellSort;

public class Main {

    public static void main(String[] args) {
        // Specify the directory containing the data files
        String dataDirectoryPath = "data";
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

                // Now you have an integer array 'arr' with the data from the file.
                // Perform sorting with timeouts
                final long TIMEOUT_SECONDS = 5;
                ExecutorService executor = Executors.newSingleThreadExecutor();

                // --- ShellSort with Timeout ---
                int[] arrShell = arr.clone();
                try {
                    Future<Long> future = executor.submit(() -> {
                        long startTimeShell = System.nanoTime();
                        ShellSort.sort(arrShell);
                        long endTimeShell = System.nanoTime();
                        return endTimeShell - startTimeShell;
                    });

                    long durationShell = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    System.out.println("ShellSort took: " + durationShell + " nanoseconds");

                } catch (TimeoutException e) {
                    System.err.println("ShellSort timed out after " + TIMEOUT_SECONDS + " seconds");
                } catch (Exception e) {
                    System.err.println("ShellSort error: " + e.getMessage());
                }

                // --- SelectionSort with Timeout ---
                int[] arrSelection = arr.clone();
                try {
                    Future<Long> future = executor.submit(() -> {
                        long startTimeSelection = System.nanoTime();
                        SelectionSort.sort(arrSelection);
                        long endTimeSelection = System.nanoTime();
                        return endTimeSelection - startTimeSelection;
                    });

                    long durationSelection = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    System.out.println("SelectionSort took: " + durationSelection + " nanoseconds");

                } catch (TimeoutException e) {
                    System.err.println("SelectionSort timed out after " + TIMEOUT_SECONDS + " seconds");
                } catch (Exception e) {
                    System.err.println("SelectionSort error: " + e.getMessage());
                }

                // --- QuickSortInsertion with Timeout ---
                int[] arrQuickInsertion = arr.clone();
                int k = 10; // Or whatever cutoff you're using
                try {
                    Future<Long> future = executor.submit(() -> {
                        long startTimeQuickInsertion = System.nanoTime();
                        QuickSortInsertion.sortCutOff(arrQuickInsertion, 0, arrQuickInsertion.length - 1, k);
                        long endTimeQuickInsertion = System.nanoTime();
                        return endTimeQuickInsertion - startTimeQuickInsertion;
                    });

                    long durationQuickInsertion = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    System.out.println("QuickSortInsertion took: " + durationQuickInsertion + " nanoseconds");

                } catch (TimeoutException e) {
                    System.err.println("QuickSortInsertion timed out after " + TIMEOUT_SECONDS + " seconds");
                } catch (Exception e) {
                    System.err.println("QuickSortInsertion error: " + e.getMessage());
                } finally {
                    executor.shutdownNow(); // Immediately shut down the executor
                }
            }
        }
    }
}
