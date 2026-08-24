public class SubmissionRegistry {
    private static class Node {
        String key; // studentId
        Submission value;
        Node next;

        public Node(String key, Submission value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node[] table;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public SubmissionRegistry() {
        this.table = new Node[16];
        this.size = 0;
    }

    private int hash(String key) {
        int hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (7 * hashVal + key.charAt(i)) % table.length;
        }
        if (hashVal < 0) { //negatife düşerse diye yaptım.
            hashVal = hashVal + table.length;
        }
        return hashVal;
    }

    public void put(Submission s) {
        if (size >= table.length * LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(s.getStudentId());
        Node current = table[index];

        while (current != null) {
            if (current.key.equals(s.getStudentId())) {
                current.value = s;
                return;
            }
            current = current.next;
        }

        table[index] = new Node(s.getStudentId(), s, table[index]);
        size++;
    }

    public Submission lookup(String studentId) {
        int index = hash(studentId);
        Node current = table[index];

        while (current != null) {
            if (current.key.equals(studentId)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public int updateVersion(String studentId, String fileName, int sizeKb, long timestampMs) {
        Submission s = lookup(studentId); //öğrencinin .value değeri dönüyor, s ye atıyoruz.
        if (s != null) {
            s.replaceFile(fileName, sizeKb, timestampMs);
            return s.getVersion();
        }
        return -1;
    }

    public int size() {
        return size;
    }

    private void resize() {
        Node[] oldTable = table;
        table = new Node[oldTable.length * 2];
        size = 0;

        for (Node node : oldTable) { //tablodaki verileri tekrar yeni kapasitedeki tabloya ekliyoruz
            while (node != null) {
                put(node.value);
                node = node.next;
            }
        }
    }
}