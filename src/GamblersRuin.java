import java.util.Random;

/**
 * Study 3: gambler's ruin. Start with `start` dollars, bet $1 per round and win with
 * probability p, stop at 0 (ruin) or `goal`. Run: java src/GamblersRuin.java
 */
public class GamblersRuin {

    /** Exact chance of reaching the goal before ruin. */
    static double exactReachGoal(int start, int goal, double p) {
        if (p == 0.5) return start / (double) goal;
        double r = (1 - p) / p;
        return (1 - Math.pow(r, start)) / (1 - Math.pow(r, goal));
    }

    /** Exact expected number of rounds, fair game only: start * (goal - start). */
    static double exactDurationFair(int start, int goal) {
        return start * (double) (goal - start);
    }

    /** One game: returns rounds played, and sets reached[0] if the goal was hit. */
    static int play(int start, int goal, double p, Random rng, boolean[] reached) {
        int money = start, rounds = 0;
        while (money > 0 && money < goal) {
            money += rng.nextDouble() < p ? 1 : -1;
            rounds++;
        }
        reached[0] = money == goal;
        return rounds;
    }

    static int failures = 0;

    static void check(String name, boolean ok, String detail) {
        if (!ok) failures++;
        System.out.printf("%s %s %s%n", ok ? "PASS" : "FAIL", name, detail);
    }

    static void agree(int start, int goal, double p, int trials) {
        Random rng = new Random(7);
        boolean[] reached = new boolean[1];
        int wins = 0;
        double sum = 0, sumSq = 0;
        for (int t = 0; t < trials; t++) {
            int rounds = play(start, goal, p, rng, reached);
            if (reached[0]) wins++;
            sum += rounds;
            sumSq += (double) rounds * rounds;
        }
        double exact = exactReachGoal(start, goal, p);
        double est = wins / (double) trials;
        double tol = 3 * Math.sqrt(exact * (1 - exact) / trials);
        String tag = String.format("start=%d goal=%d p=%.2f", start, goal, p);
        check("reach goal " + tag, Math.abs(est - exact) <= tol,
                String.format("exact=%.4f simulated=%.4f tolerance=%.4f", exact, est, tol));
        if (p == 0.5) {
            double mean = sum / trials;
            double sampleVar = (sumSq - trials * mean * mean) / (trials - 1);
            double durTol = 3 * Math.sqrt(sampleVar / trials);
            double durExact = exactDurationFair(start, goal);
            check("duration " + tag, Math.abs(mean - durExact) <= durTol,
                    String.format("exact=%.2f simulated=%.2f tolerance=%.2f", durExact, mean, durTol));
        }
    }

    public static void main(String[] args) {
        check("fair game, start 5 of 10: exactly 1/2", exactReachGoal(5, 10, 0.5) == 0.5, "");
        check("start at goal is certain", Math.abs(exactReachGoal(10, 10, 0.4) - 1.0) < 1e-12, "");
        check("start at 0 is ruin", exactReachGoal(0, 10, 0.4) == 0.0, "");
        agree(5, 10, 0.5, 100_000);
        agree(3, 10, 0.5, 100_000);
        agree(5, 10, 0.45, 100_000);
        agree(8, 10, 0.55, 100_000);
        if (failures > 0) System.exit(1);
    }
}
