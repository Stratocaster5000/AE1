package sort;

public class ShellSort{ 
 
    public static void sort(int a[]){ 
      int n = a.length; 
      int h = 1; 
       
      while (h < n/3) h = 3*h + 1; 
   
      while (h >= 1) { 
        for (int i = h; i < n; i++){ 
          for (int j = i; j >= h && a[j] < a[j-h]; j -= h){ 
            Swap.swap(a, j, j-h); 
          } 
        } 
        h /= 3; 
      } 
    }
    

}
