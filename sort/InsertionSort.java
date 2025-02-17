package sort;

public class InsertionSort {

    public static void sort(int a[]) {
        int n = a.length;
        for (int j = 1; j < n; j++) {
            int key = a[j];
            int i = j - 1;
            while ((i >= 0) && (a[i] > key)) {
                a[i + 1] = a[i];
                i--;
            }
            a[i + 1] = key;
        }
    }
}
