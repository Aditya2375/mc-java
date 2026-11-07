import java.util.Random;

/** Study 1: the birthday problem. Run: java src/Birthday.java */
public class Birthday {

    static final int DAYS = 365;

    /** Exact chance that at least two of n people share a birthday. */
    static double exact(int n) {
        double allDifferent = 1.0;
        for (int i = 0; i < n; i++) {
            allDifferent *= (DAYS - i) / (double) DAYS;
        }
        return 1.0 - allDifferent;
    }

    /** Simulated chance: fraction of trials in which some birthday repeats. */
    static double simulate(int n, int trials, Random rng) {
        int matches = 0;
        for (int t = 0; t < trials; t++) {
            boolean[] taken = new boolean[DAYS];
            for (int i = 0; i < n; i++) {
                int day = rng.nextInt(DAYS);
                if (taken[day]) {
                    matches++;
                    break;
                }
                taken[day] = true;
            }
        }
        return matches / (double) trials;
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    static void agree(int n, int trials) {
        double p = exact(n);
        double est = simulate(n, trials, new Random(12345));
        double tol = 3 * Math.sqrt(p * (1 - p) / trials);
        check("n=" + n, Math.abs(est - p) <= tol,
                String.format("exact=%.4f simulated=%.4f tolerance=%.4f", p, est, tol));
    }

    public static void main(String[] args) {
        check("exact(1) is 0", exact(1) == 0.0, "");
        check("exact(23) is about 0.5073", Math.abs(exact(23) - 0.5073) < 0.0005, "");
        check("exact(366) is 1 (pigeonhole)", Math.abs(exact(366) - 1.0) < 1e-12, "");
        agree(10, 200_000);
        agree(23, 200_000);
        agree(40, 200_000);
        // The test must be able to fail. A plausible bug: only comparing each person with the
        // one before them. The same tolerance should reject it.
        double buggy = simulateAdjacentOnly(23, 200_000, new Random(12345));
        double p = exact(23);
        check("test catches an adjacent-only simulator", Math.abs(buggy - p) > 3 * Math.sqrt(p * (1 - p) / 200_000),
                String.format("buggy=%.4f exact=%.4f", buggy, p));
        if (failures > 0) System.exit(1);
    }

    /** Deliberately wrong: only checks whether neighbours share a birthday. */
    static double simulateAdjacentOnly(int n, int trials, Random rng) {
        int matches = 0;
        for (int t = 0; t < trials; t++) {
            int previous = rng.nextInt(DAYS);
            for (int i = 1; i < n; i++) {
                int day = rng.nextInt(DAYS);
                if (day == previous) {
                    matches++;
                    break;
                }
                previous = day;
            }
        }
        return matches / (double) trials;
    }
}
