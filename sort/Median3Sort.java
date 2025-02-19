package sort;

public class Median3Sort {

    public static void sort(int[] a, int p, int r) {
        if (r <= p) {
            return;
        }
        Swap.swap(a, (p + r) / 2, r - 1);
        if (a[r - 1] < a[p]) {
            Swap.swap(a, p, r - 1);
        }
        if (a[r] < a[p]) {
            Swap.swap(a, p, r);
        }
        if (a[r] < a[r - 1]) {
            Swap.swap(a, r - 1, r);
        }
        int q = QuickSort.partition(a, p + 1, r - 1);
        sort(a, p, q - 1);
        sort(a, q + 1, r);
    }
}
