package sort;

public class DutchFlagSort {

    public static void sort3Way(int a[], int l, int r) {
        if (r <= l) {
            return;
        }
        int v = a[r];
        int i = l - 1, j = r, p = l - 1, q = r, k;
        for (;;) {
            while (a[++i] < v);
            while (v < a[--j]) {
                if (j == l) {
                    break;
                }
            }
            if (i >= j) {
                break;
            }
            Swap.swap(a, i, j);
            if (a[i] == v) {
                p++;
                Swap.swap(a, p, i);
            }
            if (v == a[j]) {
                q--;
                Swap.swap(a, q, j);
            }
        }
        Swap.swap(a, i, r);
        j = i - 1;
        i = i + 1;
        for (k = l; k <= p; k++, j--) {
            Swap.swap(a, k, j);
        }
        for (k = r - 1; k >= q; k--, i++) {
            Swap.swap(a, k, i);

        }
        sort3Way(a, l, j);
        sort3Way(a, i, r);
    }
}
