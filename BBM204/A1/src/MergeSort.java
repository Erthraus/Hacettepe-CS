import java.util.Arrays;

public class MergeSort extends Sort {
    public MergeSort() {
        name = "Merge Sort";
    }

    @Override
    public int[] sort(int[] data){
        return merge(data.clone());
    }

    private int[] merge(int[] data) {
        int n = data.length;

        if(n <= 1) return data;
        int[] left = Arrays.copyOfRange(data, 0, n/2);
        int[] right = Arrays.copyOfRange(data, n/2, n);

        left = merge(left);
        right = merge(right);

        return mergeInside(left, right);
    }

    private int[] mergeInside(int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        int arr[] = new int[left.length + right.length];
        
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }

        while (i < left.length) {
            arr[k++] = left[i++];
        }
        
        while (j < right.length) {
            arr[k++] = right[j++];
        }

        return arr;
    }
}