public class HeapDispatcher {
    private Submission[] heap;
    private int size;

    public HeapDispatcher(int capacity) {
        this.heap = new Submission[capacity];
        this.size = 0;
    }

    private boolean isHigherPriority(Submission a, Submission b) {
        if (a.hasAccommodation() != b.hasAccommodation()) {
            return a.hasAccommodation();
        }
        return a.getTimestampMs() < b.getTimestampMs();
    }

    private void swap(int i, int j) {
        Submission temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public void submit(Submission s) {//aşağıdan yukarıya
        if (size == heap.length) return;
        heap[size] = s;
        siftUp(size);
        size++;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            // parentından daha yüksek öncelikliyse yer değiştir
            if (isHigherPriority(heap[index], heap[parent])) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    // yukarıdan asagıya
    public Submission next() {
        if (size == 0) return null;
        Submission highest = heap[0];
        heap[0] = heap[size - 1]; // en sondakini roota al
        heap[size - 1] = null;
        size--;
        siftDown(0);
        return highest;
    }

    private void siftDown(int index) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int highestPriorityIndex = index;

            // sol çocuk daha mı öncelikli?
            if (leftChild < size && isHigherPriority(heap[leftChild], heap[highestPriorityIndex])) {
                highestPriorityIndex = leftChild;
            }
            // Sağ çocuk  daha mı öncelikli?
            if (rightChild < size && isHigherPriority(heap[rightChild], heap[highestPriorityIndex])) {
                highestPriorityIndex = rightChild;
            }

            if (highestPriorityIndex != index) {
                swap(index, highestPriorityIndex);
                index = highestPriorityIndex;
            } else {
                break;
            }
        }
    }

    // tek seferde ağaca çevirme o(n)
    public void loadBurst(Submission[] burst) {
        for (int i = 0; i < burst.length; i++) {
            if (size < heap.length) {
                heap[size++] = burst[i];
            }
        }
        // Sondan roota kadar siftDown
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    public int size() {
        return size;
    }
}