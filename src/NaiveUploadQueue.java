public class NaiveUploadQueue {
    private Submission[] queue;
    private int size;

    public NaiveUploadQueue(int capacity) {
        this.queue = new Submission[capacity];
        this.size = 0;
    }

    public boolean enqueue(Submission s) {
        if (size == queue.length)
            return false;
        queue[size++] = s;
        return true;
    }

    // her elemanda tüm diziyi sola kaydırıyoruz O(n)
    public Submission dequeue() {
        if (size == 0) return null;
        Submission front = queue[0];

        for (int i = 1; i < size; i++) {
            queue[i - 1] = queue[i];
        }
        queue[size - 1] = null;
        size--;
        return front;
    }

    public int size() {
        return size;
    }
}