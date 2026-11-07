import java.util.Random;

/**
 * Study 7: variance reduction. Plain Monte Carlo for pi has error shrinking like 1/sqrt(n).
 * Stratified sampling splits the unit square into a k x k grid and draws one random point per
 * cell (n = k*k points), which spreads the points out. We measure the root-mean-square error of
 * both over many repetitions instead of trusting theory. Run: java src/PiVarianceReduction.java
 */
public class PiVarianceReduction {

    static double plain(int k, Random rng) {
        int n = k * k, inside = 0;
        for (int i = 0; i < n; i++) {
            double x = rng.nextDouble(), y = rng.nextDouble();
            if (x * x + y * y <= 1.0) inside++;
        }
        return 4.0 * inside / n;
    }

    static double stratified(int k, Random rng) {
        int inside = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                double x = (i + rng.nextDouble()) / k;
                double y = (j + rng.nextDouble()) / k;
                if (x * x + y * y <= 1.0) inside++;
            }
        }
        return 4.0 * inside / (k * k);
    }

    static double rmsError(boolean strat, int k, int reps, long seed) {
        Random rng = new Random(seed);
        double sumSq = 0;
        for (int r = 0; r < reps; r++) {
            double e = (strat ? stratified(k, rng) : plain(k, rng)) - Math.PI;
            sumSq += e * e;
        }
        return Math.sqrt(sumSq / reps);
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    public static void main(String[] args) {
        int reps = 400;
        int[] ks = {10, 30, 100};
        double[] strat = new double[ks.length];
        for (int a = 0; a < ks.length; a++) {
            int k = ks[a], n = k * k;
            double q = Math.PI / 4;
            double theory = 4.0 * Math.sqrt(q * (1 - q) / n);
            double p = rmsError(false, k, reps, 11);
            double s = rmsError(true, k, reps, 22);
            strat[a] = s;
            System.out.printf("n=%d plain RMS=%.5f (theory %.5f) stratified RMS=%.5f ratio=%.1fx%n",
                    n, p, theory, s, p / s);
            // RMS over 400 reps has relative sd about 1/sqrt(2*400) = 3.5%, so +/- 15% is wide.
            check("plain RMS matches theory at n=" + n, Math.abs(p / theory - 1) < 0.15, "");
            check("stratified beats plain at n=" + n, s < p, "");
            if (k == 100) {
                // Measured ratio is about 8x. Requiring 4x also rejects a half-stratified sampler.
                check("stratified is at least 4x better at n=" + n, p / s >= 4.0,
                        String.format("ratio=%.1fx", p / s));
            }
        }
        check("stratified error falls faster than 1/sqrt(n)", strat[2] / strat[0] < 0.1,
                String.format("error at n=10000 is %.3f of the error at n=100 (plain would be 0.100)",
                        strat[2] / strat[0]));
        if (failures > 0) System.exit(1);
    }
}
