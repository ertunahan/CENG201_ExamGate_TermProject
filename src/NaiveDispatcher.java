public class NaiveDispatcher {
    private Submission[] queue;
    private int size;

    public NaiveDispatcher(int capacity) {
        this.queue = new Submission[capacity];
        this.size = 0;
    }

    private boolean isHigherPriority(Submission a, Submission b) {
        if (a.hasAccommodation() != b.hasAccommodation()) {
            return a.hasAccommodation(); //sadece a öncelikliyse a yı döndür
        }
        return a.getTimestampMs() < b.getTimestampMs(); //ikiside öncelikli veya değilse
    }

    // o(n) insertion mantığı
    public void submit(Submission s) {
        if (size == queue.length)
            return;

        int i = size - 1;
        // yeni gelen s, i'den daha yüksek öncelikliyse, i'yi sağa kaydır
        while (i >= 0 && isHigherPriority(s, queue[i])) {
            queue[i + 1] = queue[i];
            i--;
        }
        queue[i + 1] = s;
        size++;
    }

    // o(1) hızında en öncelikli olanı al
    public Submission next() {
        if (size == 0) return null;
        Submission highest = queue[size - 1];
        queue[size - 1] = null;
        size--;
        return highest;
    }

    public int size() {
        return size;
    }
}