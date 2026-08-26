import java.util.Random;

public class ScenarioGenerator {
    public static final int STUDENT_COUNT = 800;
    public static final long WINDOW_OPENS_MS = 79_200_000L; // 22:00:00.000
    private final Random rng;
    private final boolean[] accommodation;
    private long clockMs = WINDOW_OPENS_MS;
    public ScenarioGenerator(long seed) {
        this.rng = new Random(seed);
        this.accommodation = new boolean[STUDENT_COUNT];
        for (int i = 0; i < STUDENT_COUNT; i++) {
            accommodation[i] = rng.nextInt(100) < 3;
        }
    }
    public String studentId(int i) {
        return String.format("S-%04d", i + 1);
    }
    public boolean hasAccommodation(int i) {
        return accommodation[i];
    }

public Submission nextUpload(int i) {
    clockMs += 1 + rng.nextInt(2_000);
    int sizeKb = 200 + rng.nextInt(4_800);
    String fileName = studentId(i) + "_project.pdf";
    return new Submission(studentId(i), fileName, sizeKb,
            clockMs, 1, accommodation[i]);
}

    public Submission[] generateUploadEvents() {
        Submission[] events = new Submission[2500];
        for (int i = 0; i < 2500; i++) {
            if (i == 1000) {
                clockMs = Math.max(clockMs, WINDOW_OPENS_MS + 3_600_000L);
            } else if (i == 2000) {
                clockMs = Math.max(clockMs, 86_220_000L);
            }

            int studentIndex;
            if (i > 0 && rng.nextInt(100) < 10) {
                studentIndex = rng.nextInt(STUDENT_COUNT);
            } else {
                studentIndex = i % STUDENT_COUNT;
            }
            events[i] = nextUpload(studentIndex);
        }
        return events;
    }

    public static void main(String[] args) {
        ScenarioGenerator gen = new ScenarioGenerator(20260725L);
        for (int i = 0; i < 5; i++) {
            System.out.println(gen.nextUpload(i));
        }
    }
}