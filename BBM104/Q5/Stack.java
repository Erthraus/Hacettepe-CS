import java.util.EmptyStackException;

public class Stack {
    private static final int DEFAULT_CAPACITY = 20;
    private int[] stackArray;
    private int top;

    public Stack() {
        stackArray = new int[DEFAULT_CAPACITY];
        top = -1;
    }

    public void push(int num) {
        if(isFull()) {
            throw new IllegalStateException("Stack is full");
        }

        stackArray[++top] = num;
    }

    public int pop() {
        if(isEmpty()) {
            throw new EmptyStackException();
        }

        return stackArray[top--];
    }

    public int top() {
        if(isEmpty()) {
            throw new EmptyStackException();
        }

        return stackArray[top];
    }

    public boolean isFull() {
        return top == stackArray.length - 1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }
}