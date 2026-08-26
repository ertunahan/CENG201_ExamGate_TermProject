import java.util.NoSuchElementException;

public class VersionStack {
    private static class Node {
        VersionRecord record;
        Node next;
        Node(VersionRecord record, Node next) {
            this.record = record;
            this.next = next;
        }
    }

    private Node top;

    public void push(VersionRecord v) {
        top = new Node(v, top);
    }

    public VersionRecord pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        VersionRecord v = top.record;
        top = top.next;
        return v;
    }

    public boolean isEmpty() {
        return top == null;
    }
}