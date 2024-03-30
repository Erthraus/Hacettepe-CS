public class InsertionSort extends Sort{
    public InsertionSort() {
        name = "Insertion Sort";
    }

    @Override
    public int[] sort(int[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null.");
        }

        int n = data.length;
        int[] sorteddataay = data.clone();

        for (int j = 1; j < n; j++) {
            int key = sorteddataay[j];
            int i = j - 1;

            while (i >= 0 && sorteddataay[i] > key) {
                sorteddataay[i + 1] = sorteddataay[i];
                i--;
            }
            sorteddataay[i + 1] = key;
        }
        return sorteddataay;
    }
}
