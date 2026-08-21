public class Submission {

    public static final long DEADLINE_MS = 86_340_000L;

    private final String studentId;
    private String fileName;
    private int sizeKb;
    private long timestampMs;
    private int version;
    private final boolean accommodationFlag;

    public Submission(String studentId, String fileName, int sizeKb,
                      long timestampMs, int version,
                      boolean accommodationFlag) {

        this.studentId = studentId;
        this.fileName = fileName;
        this.sizeKb = sizeKb;
        this.timestampMs = timestampMs;
        this.version = version;
        this.accommodationFlag = accommodationFlag;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSizeKb() {
        return sizeKb;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public int getVersion() {
        return version;
    }

    public boolean hasAccommodation() {
        return accommodationFlag;
    }

    public void replaceFile(String fileName, int sizeKb, long timestampMs) {
        this.fileName = fileName;
        this.sizeKb = sizeKb;
        this.timestampMs = timestampMs;
        this.version++;
    }

    public void restoreFile(String fileName, int sizeKb,
                            long timestampMs, int version) {

        this.fileName = fileName;
        this.sizeKb = sizeKb;
        this.timestampMs = timestampMs;
        this.version = version;
    }

    public boolean isLate() {
        return timestampMs > DEADLINE_MS;
    }

    public String clock() {
        return String.format(
                "%02d:%02d:%02d.%03d",
                timestampMs / 3_600_000,
                (timestampMs / 60_000) % 60,
                (timestampMs / 1_000) % 60,
                timestampMs % 1_000
        );
    }

    @Override
    public String toString() {
        return String.format(
                "%s v%d %-22s %5d KB %s%s%s",
                studentId,
                version,
                fileName,
                sizeKb,
                clock(),
                accommodationFlag ? " [ACC]" : "",
                isLate() ? " LATE" : ""
        );
    }
}