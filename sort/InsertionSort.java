package sort;

public class InsertionSort {

    public static void sort(int a[], int p, int r) {
        for (int i = p + 1; i < r; i++) {
            for (int j = i; j > p && a[j] < a[j - 1]; j--) {
                Swap.swap(a, j, j - 1);
            }
        }
    }
}
