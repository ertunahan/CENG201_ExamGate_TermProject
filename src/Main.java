public class Main {

    public static void main(String[] args) {

        Submission s = new Submission(
                "S-0042",
                "homework.pdf",
                2500,
                86_300_000L,
                1,
                false
        );
        Submission onTime = new Submission(
                "S-0001",
                "test.pdf",
                1000,
                Submission.DEADLINE_MS,
                1,
                false
        );

        Submission late = new Submission(
                "S-0002",
                "test.pdf",
                1000,
                Submission.DEADLINE_MS + 1,
                1,
                false
        );

        System.out.println(onTime);
        System.out.println(late);

    }
}