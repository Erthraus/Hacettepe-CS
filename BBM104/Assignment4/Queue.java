import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Random;

public class Queue<T> implements Operations{
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] queueArray;
    private int front;
    private int rear;

    public Queue() {
        queueArray = new Object[DEFAULT_CAPACITY];
        front = 0;
        rear = -1;
    }

    public void enqueue(T value) {
        ensureCapacity();
        queueArray[++rear] = value;
    }

    public void enqueue(int value) {
        ensureCapacity();
        queueArray[++rear] = value;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() throws EmptyStackException{
        if(!isEmpty()) {
            return (T) queueArray[front++];
        } else {
            throw new EmptyStackException();
        }
    }

    public boolean isEmpty() {
        return front > rear;
    }

    private void ensureCapacity() {
        if(rear == queueArray.length - 1) {
            queueArray = Arrays.copyOf(queueArray, queueArray.length * 2);
        }
    }

    @Override
    public String toString() {
        String arrayString = Arrays.toString(Arrays.copyOfRange(queueArray, front, rear + 1));
        arrayString = arrayString.substring(1, arrayString.length() - 1).replace(", ", " ");
        return arrayString;
    }

    @Override
    public int calculateDistance() {
        int distance = 0;
        for(int i = front; i < rear; i++) {
            for(int j = i + 1; j <= rear; j++) {
                distance += Math.abs((int)queueArray[i] - (int)queueArray[j]);
            }
        }

        return distance;
    }

    @Override
    public void addOrRemoveElements(int value) {
        if(value < 0) {
            for(int i = 0; i < -value; i++) {
                this.dequeue();
            }
        } else {
            Random random = new Random();
            for(int i = 0; i < value; i++) {
                this.enqueue(Math.abs(random.nextInt() % 51));
            }
        }
    }

    @Override
    public int distinctElements() {
        int count = 0;
        for(int i = front; i <= rear; i++) {
            boolean isDistinct = true;
            for(int j = front; j < i; j++) {
                if(queueArray[i].equals(queueArray[j])) {
                    isDistinct = false;
                    break;
                }
            }
            if(isDistinct) count++;
        }

        return count;
    }

    @Override
    public void removeGreater(int value) {
        int newFront = 0;
        int newRear = -1;
        for(int i = front; i <= rear; i++) {
            if((int)queueArray[i] <= value) queueArray[++newRear] = queueArray[i];
        }

        front = newFront;
        rear = newRear;
    }

    @Override
    public void reverseElements(int k) {
        int[] values = new int[k];

        int j = 1;
        for(int i = front; i < k && i <= rear; i++) {
            values[k - j++] = (int)queueArray[i];
        }
        for(int i = front; i < k && i <= rear; i++) {
            queueArray[i] = values[i];
        }
    }

    @Override
    public void sort() {
        if(isEmpty()) return;

        int n = rear - front + 1;
        boolean swapped;

        for(int i = 0; i < n - 1; i++) {
            swapped = false;

            for(int j = front; j < n - 1 - i; j++) {
                if((int)queueArray[j] > (int)queueArray[j + 1]) {
                    int temp = (int)queueArray[j];
                    queueArray[j] = queueArray[j + 1];
                    queueArray[j + 1] = temp;

                    swapped = true;
                }
            }

            if(!swapped) {
                break;
            }
        }

        IOManager.writeToFile(this.toString(), IOManager.queueOutputPath);
    }
}
