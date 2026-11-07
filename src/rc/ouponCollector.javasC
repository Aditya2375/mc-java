import java.util.Random;

/** Study 2: coupon collector. How many draws to see all n coupons? Run: java src/CouponCollector.java */
public class CouponCollector {

    /** Exact expected draws: n * (1 + 1/2 + ... + 1/n). */
    static double expected(int n) {
        double h = 0;
        for (int k = 1; k <= n; k++) h += 1.0 / k;
        return n * h;
    }

    /** Exact variance: sum over k of (1 - p_k) / p_k^2, where p_k = (n - k + 1) / n. */
    static double variance(int n) {
        double v = 0;
        for (int k = 1; k <= n; k++) {
            double p = (n - k + 1) / (double) n;
            v += (1 - p) / (p * p);
        }
        return v;
    }

    /** Draws needed in one trial. */
    static int oneRun(int n, Random rng) {
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

    static double simulateMean(int n, int trials, Random rng) {
        long total = 0;
        for (int t = 0; t < trials; t++) total += oneRun(n, rng);
        return total / (double) trials;
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    static void agree(int n, int trials) {
        double e = expected(n);
        double est = simulateMean(n, trials, new Random(2024));
        double tol = 3 * Math.sqrt(variance(n) / trials);
        check("n=" + n, Math.abs(est - e) <= tol,
                String.format("expected=%.3f simulated=%.3f tolerance=%.3f", e, est, tol));
    }

    public static void main(String[] args) {
        check("expected(1) is 1", Math.abs(expected(1) - 1.0) < 1e-12, "");
        check("expected(2) is 3", Math.abs(expected(2) - 3.0) < 1e-12, "");
        check("expected(6) is 14.7 (a die: about 14.7 rolls to see every face)",
                Math.abs(expected(6) - 14.7) < 1e-9, "");
        agree(6, 100_000);
        agree(20, 100_000);
        agree(52, 50_000);
        if (failures > 0) System.exit(1);
    }
}
