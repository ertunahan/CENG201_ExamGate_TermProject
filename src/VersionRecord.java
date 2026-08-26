public class VersionRecord {
    private final String fileName;
    private final int sizeKb;
    private final long timestampMs;
    private final int version;

    public VersionRecord(String fileName, int sizeKb, long timestampMs, int version) {
        this.fileName = fileName;
        this.sizeKb = sizeKb;
        this.timestampMs = timestampMs;
        this.version = version;
    }

    public String getFileName() { return fileName; }
    public int getSizeKb() { return sizeKb; }
    public long getTimestampMs() { return timestampMs; }
    public int getVersion() { return version; }
}