import java.util.Random;

/** Study 4: estimate pi from random points in the unit square. Run: java src/EstimatePi.java */
public class EstimatePi {

    /** Fraction of random points inside the quarter circle, times 4. */
    static double estimate(int points, Random rng) {
        int inside = 0;
        for (int i = 0; i < points; i++) {
            double x = rng.nextDouble(), y = rng.nextDouble();
            if (x * x + y * y <= 1.0) inside++;
        }
        return 4.0 * inside / points;
    }

    /** Standard error of the estimate: 4 * sqrt(q(1-q)/n) with q = pi/4. */
    static double standardError(int points) {
        double q = Math.PI / 4;
        return 4.0 * Math.sqrt(q * (1 - q) / points);
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    public static void main(String[] args) {
        int[] sizes = {1_000, 100_000, 10_000_000};
        double previousSe = Double.MAX_VALUE;
        for (int n : sizes) {
            double est = estimate(n, new Random(31415));
            double se = standardError(n);
            check("n=" + n, Math.abs(est - Math.PI) <= 3 * se,
                    String.format("estimate=%.5f error=%.5f 3*SE=%.5f", est, Math.abs(est - Math.PI), 3 * se));
            check("SE shrinks as n grows (n=" + n + ")", se < previousSe, "");
            previousSe = se;
        }
        // 100x more points should cut the standard error by about 10x (the 1/sqrt(n) rule).
        double ratio = standardError(1_000) / standardError(100_000);
        check("100x points gives about 10x smaller SE", Math.abs(ratio - 10.0) < 1e-9,
                String.format("ratio=%.4f", ratio));
        if (failures > 0) System.exit(1);
    }
}
