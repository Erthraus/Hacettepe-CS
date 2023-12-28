import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Arrays;
import java.util.Random;

public class Stack<T> implements Operations{
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] stackArray;
    private int top;

    public Stack() {
        stackArray = new Object[DEFAULT_CAPACITY];
        top = -1;
    }

    public void push(T value) {
        ensureCapacity();
        stackArray[++top] = value;
    }

    public void push(int value) {
        ensureCapacity();
        stackArray[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() throws EmptyStackException{
        if (!isEmpty()) {
            return (T) stackArray[top--];
        } else {
            throw new EmptyStackException();
        }
    }

    public boolean isEmpty() {
        return top == -1;
    }

    private void ensureCapacity() {
        if (top == stackArray.length - 1) {
            stackArray = Arrays.copyOf(stackArray, stackArray.length * 2);
        }
    }

    @Override
    public String toString() {
        Object[] reverseArray = Arrays.copyOfRange(stackArray, 0, top + 1);
        Collections.reverse(Arrays.asList(reverseArray));
        String arrayString = Arrays.toString(reverseArray);
        arrayString = arrayString.substring(1, arrayString.length() - 1).replace(", ", " ");
        return arrayString;
    }

    public String normalToString() {
        String arrayString = Arrays.toString(Arrays.copyOfRange(stackArray, 0, top + 1));
        arrayString = arrayString.substring(1, arrayString.length() - 1).replace(", ", " ");
        return arrayString;
    }

    @Override
    public int calculateDistance() {
        int distance = 0;
        for(int i = 0; i < top; i++) {
            for(int j = i + 1; j <= top; j++) {
                distance += Math.abs((int)stackArray[i] - (int)stackArray[j]);
            }
        }

        return distance;
    }

    @Override
    public void addOrRemoveElements(int value) {
        if(value < 0) {
            for(int i = 0; i < -value; i++) {
                this.pop();
            }
        } else {
            Random random = new Random();
            for(int i = 0; i < value; i++) {
                this.push(Math.abs(random.nextInt() % 51));
            }
        }
    }

    public int distinctElements() {
        int count = 0;
        for(int i = 0; i <= top; i++) {
            boolean isDistinct = true;
            for(int j = 0; j < i; j++) {
                if(stackArray[i].equals(stackArray[j])) {
                    isDistinct = false;
                    break;
                }
            }
            if(isDistinct) count++;
        }

        return count;
    }

    public void removeGreater(int value) {
        int newTop = -1;
        for (int i = 0; i <= top; i++) {
            if ((int)stackArray[i] <= value) {
                stackArray[++newTop] = stackArray[i];
            }
        }
        top = newTop;
    }

    public void reverseElements(int k) {
        int[] values = new int[k];
        for(int i = 0; i < k; i++) {
            values[k - i - 1] = (int)stackArray[top - i];
        }
        for(int i = 0; i < k; i++) {
            stackArray[top - i] = values[i];
        }
    }

    public void sort() {
        int n = top + 1;
        boolean swapped;

        for(int i = 0; i < n - 1; i++) {
            swapped = false;

            for(int j = 0; j < n - 1 - i; j++) {
                if((int)stackArray[j] > (int)stackArray[j + 1]) {
                    int temp = (int)stackArray[j];
                    stackArray[j] = stackArray[j + 1];
                    stackArray[j + 1] = temp;

                    swapped = true;
                }
            }

            if(!swapped) {
                break;
            }
        }

        IOManager.writeToFile(this.normalToString(), IOManager.stackOutputPath);
    }
}
