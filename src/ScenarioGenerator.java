import java.util.Random;
/**
 * Deterministic test-data factory for the ExamGate platform.
 *
 * Everything flows from ONE seeded Random: the same seed produces the same
 * roster and the same upload nights, byte for byte, on every machine. That is
 * what makes your WP7 checkpoints reproducible — and gradable.
 *
 * This is a skeleton. It already builds the 800-student roster (about 3% of
 * the students carry an accommodation flag) and produces single uploads with
 * DISTINCT timestamps: the clock only moves forward, so the WP5 timeline
 * never sees a duplicate key. For WP7 you will extend it with
 * generateUploadEvents(): about 2,500 uploads in 3 bursts, the last burst
 * inside the final 2 minutes before 23:59, about 10% of them re-uploads.
 */
public class ScenarioGenerator {
    public static final int STUDENT_COUNT = 800;
    public static final long WINDOW_OPENS_MS = 79_200_000L; // 22:00:00.000
    private final Random rng; // the ONLY source of randomness
    private final boolean[] accommodation; // decided once per student, ~3%
    private long clockMs = WINDOW_OPENS_MS;
    public ScenarioGenerator(long seed) {
        this.rng = new Random(seed); // never call 'new Random()' anywhere else
        this.accommodation = new boolean[STUDENT_COUNT];
        for (int i = 0; i < STUDENT_COUNT; i++) {
            accommodation[i] = rng.nextInt(100) < 3;
        }
    }
    public String studentId(int i) { return String.format("S-%04d", i + 1); }
    public boolean hasAccommodation(int i) { return accommodation[i]; }
/**
 * One first-version upload for student i. Timestamps are distinct by
 * construction: the platform clock always moves FORWARD by at least 1 ms.
 */
public Submission nextUpload(int i) {
    clockMs += 1 + rng.nextInt(2_000); // +1 ms .. +2 s per event
    int sizeKb = 200 + rng.nextInt(4_800); // 200 KB .. about 5 MB
    String fileName = studentId(i) + "_project.pdf";
    return new Submission(studentId(i), fileName, sizeKb,
            clockMs, 1, accommodation[i]);
}
    /** Smoke test: prints five uploads. Run me first. */
    public static void main(String[] args) {
        ScenarioGenerator gen = new ScenarioGenerator(20260725L);
        for (int i = 0; i < 5; i++) {
            System.out.println(gen.nextUpload(i));
        }
    }
}