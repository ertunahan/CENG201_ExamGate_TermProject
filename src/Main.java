public class Main {
    public static void main(String[] args) {

        // --------------------------------------------------------------
        // ÖNEMLİ
        // CLASSLARI TEST ETMEK İÇİN AŞAĞIDA BULUNAN YORUM SATIRLARINI ADIM ADIM SİLEREK TEST EDEBİLİRSİNİZ..
        // --------------------------------------------------------------

        //testWP1_Registry();
        //testWP2_Intake();
        //testWP3_Dispatcher();
        //testWP4_Rollback();
        //testWP5_SubmissionTimeline();
        //testWP6_ReportService();
    }

    public static void testWP1_Registry() {
        System.out.println("=== WP1: HASH TABLE TESTİ ===");
        SubmissionRegistry registry = new SubmissionRegistry();
        ScenarioGenerator gen = new ScenarioGenerator(12345L);

        for (int i = 0; i < 8; i++) {
            registry.put(gen.nextUpload(i));
        }
        System.out.println("Tablodaki eleman sayısı: " + registry.size());
        System.out.println("S-0001 aranıyor: " + registry.lookup("S-0001").getFileName());

        System.out.println("İlk versiyon güncelleniyor...");
        registry.updateVersion("S-0001", "v2_dosya.pdf", 2500, 81_000_000L);
        int yeniVersiyon = registry.updateVersion("S-0001", "v3_dosya.pdf", 2600, 82_000_000L);
        System.out.println("S-0001 yeni versiyon numarası (3 olmalı): " + yeniVersiyon);

        System.out.println("S-9999 aranıyor (null dönmeli): " + registry.lookup("S-9999"));

        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            registry.lookup(gen.studentId(i % 8));
        }
        long endTime = System.nanoTime();
        System.out.println("100.000 arama süresi: " + (endTime - startTime) / 1_000_000.0 + " ms\n");
    }

    public static void testWP2_Intake() {
        System.out.println("=== WP2: CIRCULAR QUEUE TESTİ ===");
        ScenarioGenerator gen = new ScenarioGenerator(54321L);
        CircularUploadQueue circularQueue = new CircularUploadQueue(5);

        for (int i = 0; i < 5; i++) {
            circularQueue.enqueue(gen.nextUpload(i));
        }
        System.out.println("5 eleman eklendi. Mevcut Boyut: " + circularQueue.size());
        System.out.println("Çıkarılan 1: " + circularQueue.dequeue().getStudentId());
        System.out.println("Çıkarılan 2: " + circularQueue.dequeue().getStudentId());

        circularQueue.enqueue(gen.nextUpload(5));
        circularQueue.enqueue(gen.nextUpload(6));
        System.out.println("Tail başa sardıktan sonra 3. ekleme başarılı mı?: " + circularQueue.enqueue(gen.nextUpload(7)));
        System.out.println("Kapasite doluyken yeni ekleme denemesi (False olmalı): " + circularQueue.enqueue(gen.nextUpload(8)));

        int testSize = 10000;
        NaiveUploadQueue naiveQueue = new NaiveUploadQueue(testSize);
        CircularUploadQueue fastQueue = new CircularUploadQueue(testSize);

        for (int i = 0; i < testSize; i++) {
            Submission s = gen.nextUpload(i % ScenarioGenerator.STUDENT_COUNT);
            naiveQueue.enqueue(s);
            fastQueue.enqueue(s);
        }

        long startNaive = System.nanoTime();
        while(naiveQueue.size() > 0) naiveQueue.dequeue();
        long endNaive = System.nanoTime();

        long startFast = System.nanoTime();
        while(fastQueue.size() > 0) fastQueue.dequeue();
        long endFast = System.nanoTime();

        System.out.println("Naive Queue Hızı: " + (endNaive - startNaive) / 1_000_000.0 + " ms");
        System.out.println("Circular Queue Hızı: " + (endFast - startFast) / 1_000_000.0 + " ms\n");
    }

    public static void testWP3_Dispatcher() {
        System.out.println("=== WP3: DISPATCHER (HEAP) TESTİ ===");
        Submission[] burst = new Submission[8];
        burst[0] = new Submission("S-01", "f1.pdf", 100, 1000L, 1, false);
        burst[1] = new Submission("S-02", "f2.pdf", 100, 5000L, 1, true);
        burst[2] = new Submission("S-03", "f3.pdf", 100, 2000L, 1, false);
        burst[3] = new Submission("S-04", "f4.pdf", 100, 500L,  1, false);
        burst[4] = new Submission("S-05", "f5.pdf", 100, 4000L, 1, true);
        burst[5] = new Submission("S-06", "f6.pdf", 100, 3000L, 1, false);
        burst[6] = new Submission("S-07", "f7.pdf", 100, 1500L, 1, false);
        burst[7] = new Submission("S-08", "f8.pdf", 100, 6000L, 1, false);

        HeapDispatcher heapDispatcher = new HeapDispatcher(10);
        NaiveDispatcher naiveDispatcher = new NaiveDispatcher(10);

        heapDispatcher.loadBurst(burst);
        for (Submission s : burst) naiveDispatcher.submit(s);

        System.out.println("Beklenen Sıra: S-05 -> S-02 -> S-04 -> S-01 -> S-07 -> S-03 -> S-06 -> S-08");

        System.out.print("Heap Çıktısı:  ");
        while (heapDispatcher.size() > 0) System.out.print(heapDispatcher.next().getStudentId() + " -> ");

        System.out.print("\nNaive Çıktısı:  ");
        while (naiveDispatcher.size() > 0) System.out.print(naiveDispatcher.next().getStudentId() + " -> ");
        System.out.println("\n");
    }

    public static void testWP4_Rollback() {
        System.out.println("=== WP4: ROLLBACK (STACK) TESTİ ===");
        SubmissionRegistry registry = new SubmissionRegistry();
        RollbackService rollbackService = new RollbackService(registry);

        Submission sub = new Submission("S-0042", "hw3.pdf", 250, 80_000_000L, 1, false);
        registry.put(sub);
        System.out.println("v1 Yükleme:\t" + registry.lookup("S-0042"));

        rollbackService.saveSnapshot(registry.lookup("S-0042"));
        registry.updateVersion("S-0042", "hw3_final.pdf", 300, 82_000_000L);
        System.out.println("v2 Yükleme:\t" + registry.lookup("S-0042"));

        rollbackService.saveSnapshot(registry.lookup("S-0042"));
        registry.updateVersion("S-0042", "chemistry_lab.pdf", 1000, 86_300_000L);
        System.out.println("v3 Panik:\t" + registry.lookup("S-0042"));

        rollbackService.rollback("S-0042");
        System.out.println("1. Rollback:\t" + registry.lookup("S-0042"));

        rollbackService.rollback("S-0042");
        System.out.println("2. Rollback:\t" + registry.lookup("S-0042"));

        System.out.print("3. Rollback:\t");
        rollbackService.rollback("S-0042");
    }

    public static void testWP5_SubmissionTimeline() {
        System.out.println("=== WP5: AVL AĞACI VE ARALIK SORGUSU TESTİ ===");
        ScenarioGenerator gen = new ScenarioGenerator(777L);
        SubmissionTimeline timeline = new SubmissionTimeline();

        for (int i = 0; i < 10000; i++) {
            Submission s = gen.nextUpload(i % ScenarioGenerator.STUDENT_COUNT);
            timeline.insertPlain(s);
            timeline.insert(s);
        }

        System.out.println("Düz BST Yüksekliği: " + timeline.plainHeight());
        System.out.println("AVL Ağacı Yüksekliği: " + timeline.height());

        long startWindow = ScenarioGenerator.WINDOW_OPENS_MS;
        long endWindow = startWindow + 10000;

        Submission[] results = timeline.submittedBetween(startWindow, endWindow);
        System.out.println("\nİlk 10 saniyede yüklenen ödev sayısı: " + results.length);
        System.out.println("Ziyaret edilen düğüm sayısı (10.000 içinden): " + timeline.getVisitedNodes());
    }

    public static void testWP6_ReportService(){
        ReportService reportService = new ReportService();
        System.out.println("=== WP6: DEADLINE REPORT TESTİ ===");

        Submission[] data = new Submission[9];
        data[0] = new Submission("S-01", "file1.pdf", 1200, 80_000_000L, 1, false);
        data[1] = new Submission("S-02", "file2.pdf", 5000, 81_000_000L, 1, false);
        data[2] = new Submission("S-03", "file3.pdf", 300,  85_000_000L, 1, false);
        data[3] = new Submission("S-04", "huge.pdf",  9999, 82_000_000L, 1, false);
        data[4] = new Submission("S-05", "file5.pdf", 4000, 79_000_000L, 1, false);
        data[5] = new Submission("S-06", "file6.pdf", 1500, 84_000_000L, 1, false);
        data[6] = new Submission("S-07", "on_time.pdf",1000, Submission.DEADLINE_MS, 1, false);
        data[7] = new Submission("S-08", "late1.pdf", 2000, Submission.DEADLINE_MS + 1000, 1, false);
        data[8] = new Submission("S-09", "late2.pdf", 3000, Submission.DEADLINE_MS + 5000, 1, false);

        System.out.println("\n--- En Büyük 3 Dosya (Top-K) ---");
        Submission[] top3 = reportService.topKLargest(data, 3);
        for (Submission s : top3) {
            System.out.println(s.getStudentId() + " -> " + s.getSizeKb() + " KB");
        }

        System.out.println("\n--- Fast Sort (Merge Sort) Sonucu (Zaman Sıralı) ---");
        Submission[] sortedSheet = reportService.sortByTimeFast(data);
        reportService.printSheet(sortedSheet);

        System.out.println("\n--- Geç Kalanlar Listesi (Binary Search) ---");
        int firstLateIndex = reportService.findFirstAfter(sortedSheet, Submission.DEADLINE_MS);

        if (firstLateIndex != -1) {
            System.out.println("Geç kalanların başladığı indeks: " + firstLateIndex);
            for (int i = firstLateIndex; i < sortedSheet.length; i++) {
                System.out.println(sortedSheet[i].getStudentId() + " -> " + sortedSheet[i].clock());
            }
        } else {
            System.out.println("Geç kalan kimse yok!");
        }

        System.out.println("\n--- Hız Testi (10.000 Kayıt) ---");
        ScenarioGenerator gen = new ScenarioGenerator(999L);
        Submission[] perfData = new Submission[10000];
        for (int i = 0; i < 10000; i++) {
            perfData[i] = gen.nextUpload(i % ScenarioGenerator.STUDENT_COUNT);
        }

        long start = System.nanoTime();
        reportService.sortByTimeInsertion(perfData);
        long end = System.nanoTime();
        System.out.println("Insertion Sort (O(N^2)) süresi: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        reportService.sortByTimeFast(perfData);
        end = System.nanoTime();
        System.out.println("Merge Sort (O(N log N)) süresi: " + (end - start) / 1_000_000.0 + " ms");
    }
}