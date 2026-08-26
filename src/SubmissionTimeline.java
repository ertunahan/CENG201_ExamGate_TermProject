public class SubmissionTimeline {
    private class Node {
        long timestampMs;
        Submission submission;
        Node left, right;
        int height;

        Node(long timestampMs, Submission submission) {
            this.timestampMs = timestampMs;
            this.submission = submission;
            this.height = 1;
        }
    }

    private Node root;
    private Node plainRoot;

    public int height() {
        return height(root);
    }

    public int plainHeight() {
        return height(plainRoot);
    }

    private int height(Node N) {
        if (N == null) return 0;
        return N.height;
    }

    private int getBalance(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void insert(Submission s) {
        root = insertAVL(root, s.getTimestampMs(), s);
    }

    private Node insertAVL(Node node, long key, Submission s) {
        if (node == null) return new Node(key, s);

        if (key < node.timestampMs)
            node.left = insertAVL(node.left, key, s);
        else if (key > node.timestampMs)
            node.right = insertAVL(node.right, key, s);
        else
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && key < node.left.timestampMs)
            return rightRotate(node);

        if (balance < -1 && key > node.right.timestampMs)
            return leftRotate(node);

        if (balance > 1 && key > node.left.timestampMs) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key < node.right.timestampMs) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void insertPlain(Submission s) {
        plainRoot = insertBST(plainRoot, s.getTimestampMs(), s);
    }

    private Node insertBST(Node node, long key, Submission s) {
        if (node == null) return new Node(key, s);
        if (key < node.timestampMs)
            node.left = insertBST(node.left, key, s);
        else if (key > node.timestampMs)
            node.right = insertBST(node.right, key, s);

        node.height = 1 + Math.max(height(node.left), height(node.right));
        return node;
    }

    private Submission[] tempResults;
    private int resultCount;
    private int visitedNodes;

    public Submission[] submittedBetween(long t1, long t2) {
        tempResults = new Submission[10000];
        resultCount = 0;
        visitedNodes = 0;

        rangeQueryRec(root, t1, t2);

        Submission[] finalResults = new Submission[resultCount];
        for(int i = 0; i < resultCount; i++) {
            finalResults[i] = tempResults[i];
        }
        return finalResults;
    }

    private void rangeQueryRec(Node node, long t1, long t2) {
        if (node == null) return;
        visitedNodes++;

        if (node.timestampMs > t1) {
            rangeQueryRec(node.left, t1, t2);
        }

        if (node.timestampMs >= t1 && node.timestampMs <= t2) {
            tempResults[resultCount++] = node.submission;
        }

        if (node.timestampMs < t2) {
            rangeQueryRec(node.right, t1, t2);
        }
    }

    public int getVisitedNodes() {
        return visitedNodes;
    }
}