# mc-java

Monte Carlo simulation in plain Java, used to check classic probability results.

I am learning probability and Java together, so I picked results with known exact answers and made the code prove it agrees with them.

Every study does the same thing: compute the exact answer, simulate it, and fail loudly if the two disagree by more than the sampling error allows. No dependencies, no build tool. Each file runs alone.

## Run

Needs a JDK 11 or newer.

    sh run_all.sh             # all studies, exits non-zero on any failure
    java src/Birthday.java    # one study

All runs use fixed seeds, so the output is identical every time (checked by running twice and comparing).

## Studies

| File | Question | Exact answer used |
|---|---|---|
| Birthday | Chance two of n people share a birthday | 1 minus the product of (365-i)/365 |
| CouponCollector | Draws needed to see all n coupons | n times the harmonic sum, with exact variance |
| GamblersRuin | Chance of reaching a goal before ruin, and expected game length | Closed form for the unfair game, start x (goal - start) for the fair game |
| EstimatePi | Estimate pi from random points | Math.PI, with the 1/sqrt(n) error rule checked |
| MontyHall | Stay vs switch | 1/3 and 2/3, and the "it is 50/50" belief is rejected by the test |
| Coverage | Are the test tolerances honest? | Repeats experiments with 1,000 seeds and checks the 95% interval covers the truth about 95% of the time |
| PiVarianceReduction | Does stratified sampling beat plain sampling? | Measures root-mean-square error of both over 400 repetitions |

## How the tests work

A simulated number is never expected to equal the exact one. Each check allows 3 standard errors, where the standard error comes from the exact variance when it is known and from the sample otherwise. A single fixed seed could pass by luck, so the Coverage study repeats the experiment with many seeds and checks the error bars themselves.

I also tested the tests, so a check that passes by luck gets caught:
- Birthday and MontyHall each include a wrong simulator or a wrong belief that the test must reject.
- I injected 11 deliberate bugs into copies of the code (off-by-one in the exact formula, a simulator that never matches, an inverted ratio, a wrong step size, a host that can open the car, a sampler that is only half stratified, and others). The tests caught all 11. One got through at first (a half-stratified sampler), so I added a stronger check.

## Measured results

From a run (fixed seeds):
- Birthday, n = 23: exact 0.5073, simulated 0.5093 with 200,000 trials.
- Coupon collector, n = 52: exact 235.98 draws, simulated 235.84 with 50,000 trials.
- Pi with 10,000,000 points: 3.14103, error 0.00056.
- Stratified sampling for pi at n = 10,000 points: RMS error 0.0020 against 0.0162 for plain sampling, about 8 times smaller.
- 42 checks, 0 failures.

## Limits

- The birthday problem assumes 365 equally likely days. Real birthdays are not uniform.
- Pseudo-random numbers come from java.util.Random, which is fine for these studies and not for anything security related.
- Studies are independent files, so a few helper functions are copied between them instead of shared.
