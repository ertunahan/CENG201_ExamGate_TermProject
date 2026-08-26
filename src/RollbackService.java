public class RollbackService {
    private SubmissionRegistry registry;

    private static class StackNode {
        String studentId;
        VersionStack stack;
        StackNode next;
        StackNode(String id, VersionStack st, StackNode nxt) {
            this.studentId = id; this.stack = st; this.next = nxt;
        }
    }
    private StackNode[] stacks;

    public RollbackService(SubmissionRegistry registry) {
        this.registry = registry;
        this.stacks = new StackNode[128];
    }

    private int hash(String id) {
        int hashVal = 0;
        for (int i = 0; i < id.length(); i++) {
            hashVal = (7 * hashVal + id.charAt(i)) % stacks.length;
        }
        return hashVal < 0 ? hashVal + stacks.length : hashVal;
    }

    // öğrenciye özel stacki bulur veya yenisini oluşturur
    private VersionStack getStudentStack(String studentId) {
        int idx = hash(studentId);
        StackNode curr = stacks[idx];
        while (curr != null) {
            if (curr.studentId.equals(studentId)) return curr.stack;
            curr = curr.next;
        }
        VersionStack newStack = new VersionStack();
        stacks[idx] = new StackNode(studentId, newStack, stacks[idx]);
        return newStack;
    }

    // eski dosyayı kaydetmek yeni dosya yüklenmeden önce çağrılmalı
    public void saveSnapshot(Submission s) {
        VersionStack stack = getStudentStack(s.getStudentId());
        stack.push(new VersionRecord(s.getFileName(), s.getSizeKb(), s.getTimestampMs(), s.getVersion()));
    }

    // son kaydedilen versiyonu geri yükler
    public void rollback(String studentId) {
        VersionStack stack = getStudentStack(studentId);
        if (stack.isEmpty()) {
            System.out.println("Rollback error: No old version for " + studentId);
            return;
        }

        VersionRecord prev = stack.pop();
        Submission s = registry.lookup(studentId);

        if (s != null) {
            // submission daki metodu kullanarak eski haline döndürüyoruz
            s.restoreFile(prev.getFileName(), prev.getSizeKb(), prev.getTimestampMs(), prev.getVersion());
        }
    }
}