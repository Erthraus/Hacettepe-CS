public class Search {
    public static int linearSearch(int[] data, int val) {
        int size = data.length;
        
        for (int i = 0; i < size-1; i++) {
            if(data[i] == val) {
                return i;
            }
        }

        return -1;
    }

    public static int binarySearch(int[] data, int val) {
        int low = 0;
        int high = data.length - 1;

        while(high - low > 1) {
            int mid = (high + low) / 2;
            if(data[mid] < val) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        if(data[low] == val) return low;
        else if(data[high] == val) return high;

        return -1;
    }
}
