public class ExamGateEngine {
    public static void main(String[] args) {
        System.out.println("=== EXAMGATE DEADLINE NIGHT SIMULATION ===");

        ScenarioGenerator gen = new ScenarioGenerator(20260824L);
        Submission[] events = gen.generateUploadEvents();

        CircularUploadQueue intakeQueue = new CircularUploadQueue(500);
        HeapDispatcher dispatcher = new HeapDispatcher(3000);
        SubmissionRegistry registry = new SubmissionRegistry();
        RollbackService rollbackService = new RollbackService(registry);
        SubmissionTimeline timeline = new SubmissionTimeline();
        ReportService reportService = new ReportService();

        int acceptedUploads = 0;
        int rejectedUploads = 0;
        int reUploads = 0;
        int rollbacks = 0;

        for (int i = 0; i < events.length; i++) {
            Submission s = events[i];

            if (intakeQueue.enqueue(s)) {
                acceptedUploads++;
            } else {
                rejectedUploads++;
                continue;
            }

            Submission processed = intakeQueue.dequeue();
            dispatcher.submit(processed);

            Submission current = dispatcher.next();
            if (registry.lookup(current.getStudentId()) != null) {
                reUploads++;
                rollbackService.saveSnapshot(registry.lookup(current.getStudentId()));
                registry.updateVersion(current.getStudentId(), current.getFileName(), current.getSizeKb(), current.getTimestampMs());

                if (i % 25 == 0) {
                    rollbackService.rollback(current.getStudentId());
                    rollbacks++;
                }
            } else {
                registry.put(current);
            }

            timeline.insert(current);
            if (i == 999) {
                printCheckpoint("CHECKPOINT 1 (After Burst 1)", intakeQueue.size(), acceptedUploads, rejectedUploads, reUploads, rollbacks, 0);
            }
            else if (i > 0 && events[i - 1].getTimestampMs() <= Submission.DEADLINE_MS && s.getTimestampMs() > Submission.DEADLINE_MS) {
                printCheckpoint("CHECKPOINT 2 (At 23:59 Deadline)", intakeQueue.size(), acceptedUploads, rejectedUploads, reUploads, rollbacks, 0);
            }
        }
        Submission[] finalSubmissions = new Submission[registry.size()];
        int idx = 0;
        for (int i = 1; i <= ScenarioGenerator.STUDENT_COUNT; i++) {
            String studentId = String.format("S-%04d", i);
            Submission s = registry.lookup(studentId);
            if (s != null) finalSubmissions[idx++] = s;
        }

        Submission[] sortedForReport = reportService.sortByTimeFast(finalSubmissions);
        int firstLateIdx = reportService.findFirstAfter(sortedForReport, Submission.DEADLINE_MS);
        int lateCount = (firstLateIdx == -1) ? 0 : (sortedForReport.length - firstLateIdx);
        printCheckpoint("CHECKPOINT 3 (Final End-of-Night)", intakeQueue.size(), acceptedUploads, rejectedUploads, reUploads, rollbacks, lateCount);
    }

    private static void printCheckpoint(String title, int queueSize, int accepted, int rejected, int reUploads, int rollbacks, int lateCount) {
        System.out.println("\n" + title);
        System.out.println("------------------------------------------------");
        System.out.println("Kuyruk Doluluğu (Buffer Occupancy) : " + queueSize);
        System.out.println("Kabul Edilen Yüklemeler            : " + accepted);
        System.out.println("Reddedilenler (Policy Activations) : " + rejected);
        System.out.println("Görülen Re-Upload Sayısı           : " + reUploads);
        System.out.println("Yapılan Rollback Sayısı            : " + rollbacks);
        if (title.contains("Final")) {
            System.out.println("Toplam Geç Kalan (Late) Kayıt      : " + lateCount);
        }
        System.out.println("------------------------------------------------");
    }
}