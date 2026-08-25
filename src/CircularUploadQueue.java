public class CircularUploadQueue {
    private Submission[] queue;
    private int head;
    private int tail;
    private int size;

    public CircularUploadQueue(int capacity) {
        this.queue = new Submission[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public boolean enqueue(Submission s) {
        if (size == queue.length) {
            return false;
        }
        queue[tail] = s;
        tail = (tail + 1) % queue.length; // dizinin sonuna gelince başa sarması için
        size++;
        return true;
    }

    public Submission dequeue() {
        if (size == 0) return null;
        Submission front = queue[head];
        queue[head] = null;
        head = (head + 1) % queue.length; // başa sararak ilerlesin, kaydırma yapmasın
        size--;
        return front;
    }

    public int size() {
        return size;
    }
}