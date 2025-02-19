package sort;

public class DutchFlagSort {

    public static void dutchFlagSort(int[] arr) {
        int low = 0;
        int mid = 0;
        int high = arr.length - 1;

        while (mid <= high) {
            switch (arr[mid]) {
                case 0 -> {
                    Swap.swap(arr, low, mid);
                    low++;
                    mid++;
                }
                case 1 ->
                    mid++;
                case 2 -> {
                    Swap.swap(arr, mid, high);
                    high--;
                }

            }
        }
    }
}
