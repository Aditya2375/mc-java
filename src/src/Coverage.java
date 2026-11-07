import java.util.Random;

/**
 * Study 6: are the tolerances honest? The other studies use one fixed seed and a 3 standard
 * error band. Here we repeat an experiment with many different seeds and count how often the
 * 95% interval (estimate +/- 1.96 SE) contains the exact answer. It should be close to 95%.
 * Run: java src/Coverage.java
 */
public class Coverage {


    // Copies of the study code, kept here so this file runs on its own with `java src/Coverage.java`.
    static double birthdayExact(int n) {
        double allDifferent = 1.0;
        for (int i = 0; i < n; i++) allDifferent *= (365 - i) / 365.0;
        return 1.0 - allDifferent;
    }

    static double birthdaySimulate(int n, int trials, Random rng) {
        int matches = 0;
        for (int t = 0; t < trials; t++) {
            boolean[] taken = new boolean[365];
            for (int i = 0; i < n; i++) {
                int day = rng.nextInt(365);
                if (taken[day]) {
                    matches++;
                    break;
                }
                taken[day] = true;
            }
        }
        return matches / (double) trials;
    }

    static double couponExpected(int n) {
        double h = 0;
        for (int k = 1; k <= n; k++) h += 1.0 / k;
        return n * h;
    }

    static int couponRun(int n, Random rng) {
        boolean[] seen = new boolean[n];
        int distinct = 0, draws = 0;
        while (distinct < n) {
            int c = rng.nextInt(n);
            draws++;
            if (!seen[c]) {
                seen[c] = true;
                distinct++;
            }
        }
        return draws;
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    public static void main(String[] args) {
        int reps = 1000;
        double level = 0.95;
        // With 1000 repetitions, the observed coverage has standard deviation about 0.007,
        // so a band of +/- 3 sd is 0.929 to 0.971.
        double band = 3 * Math.sqrt(level * (1 - level) / reps);

        // Birthday, n = 23, 5,000 trials per repetition.
        double p = birthdayExact(23);
        int trials = 5_000;
        int covered = 0;
        for (int r = 0; r < reps; r++) {
            double est = birthdaySimulate(23, trials, new Random(1000L + r));
            double se = Math.sqrt(est * (1 - est) / trials);
            if (Math.abs(est - p) <= 1.96 * se) covered++;
        }
        double cov = covered / (double) reps;
        check("birthday n=23 interval coverage", Math.abs(cov - level) <= band,
                String.format("coverage=%.3f (expected 0.950 +/- %.3f)", cov, band));

        // Coupon collector, n = 20, 2,000 trials per repetition, SE from the sample.
        double e = couponExpected(20);
        int ctrials = 2_000;
        covered = 0;
        for (int r = 0; r < reps; r++) {
            Random rng = new Random(5000L + r);
            double sum = 0, sumSq = 0;
            for (int t = 0; t < ctrials; t++) {
                double x = couponRun(20, rng);
                sum += x;
                sumSq += x * x;
            }
            double mean = sum / ctrials;
            double var = (sumSq - ctrials * mean * mean) / (ctrials - 1);
            double se = Math.sqrt(var / ctrials);
            if (Math.abs(mean - e) <= 1.96 * se) covered++;
        }
        cov = covered / (double) reps;
        check("coupon collector n=20 interval coverage", Math.abs(cov - level) <= band,
                String.format("coverage=%.3f (expected 0.950 +/- %.3f)", cov, band));
        if (failures > 0) System.exit(1);
    }
}
