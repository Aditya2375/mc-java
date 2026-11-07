import java.util.Random;

/** Study 5: Monty Hall. Stay wins 1/3 of the time, switch wins 2/3. Run: java src/MontyHall.java */
public class MontyHall {

    /** Play one game with the full rules: the host opens a goat door you did not pick. */
    static boolean play(boolean switchDoors, Random rng) {
        int car = rng.nextInt(3);
        int pick = rng.nextInt(3);
        int host;
        do {
            host = rng.nextInt(3);
        } while (host == pick || host == car);
        int finalPick = pick;
        if (switchDoors) {
            for (int d = 0; d < 3; d++) {
                if (d != pick && d != host) finalPick = d;
            }
        }
        return finalPick == car;
    }

    static double winRate(boolean switchDoors, int trials, Random rng) {
        int wins = 0;
        for (int t = 0; t < trials; t++) if (play(switchDoors, rng)) wins++;
        return wins / (double) trials;
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    static void agree(String name, boolean switchDoors, double exact, int trials) {
        double est = winRate(switchDoors, trials, new Random(99));
        double tol = 3 * Math.sqrt(exact * (1 - exact) / trials);
        check(name, Math.abs(est - exact) <= tol,
                String.format("exact=%.4f simulated=%.4f tolerance=%.4f", exact, est, tol));
    }

    public static void main(String[] args) {
        agree("stay wins 1/3", false, 1.0 / 3, 300_000);
        agree("switch wins 2/3", true, 2.0 / 3, 300_000);
        // Common wrong answer: "after one door opens it is 50/50". The test must reject it.
        double est = winRate(true, 300_000, new Random(99));
        double tol = 3 * Math.sqrt(0.25 / 300_000);
        check("the 50/50 belief is rejected", Math.abs(est - 0.5) > tol,
                String.format("simulated=%.4f vs 0.5000", est));
        if (failures > 0) System.exit(1);
    }
}
