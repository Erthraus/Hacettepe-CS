public class CountingSort extends Sort {
    public CountingSort() {
        name = "Counting Sort";
    }

    @Override
    public int[] sort(int[] data) {
        int max = data[0];

        for (int i = 1; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }

        return countingSort(data.clone(), max);
    }

    private int[] countingSort(int[] arr, int k) {
        int[] count = new int[k + 1];

        for (int num : arr) {
            count[num]++;
        }

        for (int i = 1; i <= k; i++) {
            count[i] += count[i - 1];
        }

        int[] output = new int[arr.length];

        for (int i = arr.length - 1; i >= 0; i--) {
            output[--count[arr[i]]] = arr[i];
        }

        System.arraycopy(output, 0, arr, 0, arr.length);

        return arr;
    }
}