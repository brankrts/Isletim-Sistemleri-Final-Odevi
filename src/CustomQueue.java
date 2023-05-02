import java.util.LinkedList;
import java.util.Queue;


public class CustomQueue<T> {

    private Queue<T> queue;

    public CustomQueue() {

        this.queue = new LinkedList<>();

    }

    public int length() {

        return this.queue.size();

    }

    public void push(T data) {

        this.queue.add(data);

    }

    public T pop() {
        
        if (this.queue.size() != 0) {

            return this.queue.remove();
        }
        return null;

    }

    public T peek() {
        return this.queue.peek();
    }

    public Queue<T> getQueue() {
        return this.queue;
    }

}
