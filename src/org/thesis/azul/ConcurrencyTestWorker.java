package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;
import java.lang.Math;

public class ConcurrencyTestWorker implements Runnable {
	private long iterations = 0;
	private double start = 0;
	private double stop = 0;
	private double step = 0;
	private sharedData bestSolution;

	public ConcurrencyTestWorker(long iterations) {
		this.iterations = iterations;
	}

	public ConcurrencyTestWorker(sharedData sd, double start, double stop,
			double step) {
		this.stop = stop;
		this.start = start;
		this.step = step;
		bestSolution = sd;

	}

	private double himmelblau(double x, double y) {
		return (Math.pow((x * x + y - 11), 2) + Math.pow((x + y * y - 7), 2));
	}

	public void run() {

		if (step == 0) { // simple iterative worker
			for (int i = 0; i < iterations; i++) {
				himmelblau(2, 2); // pretend some computations
			}
			return;

		} else { // brute force minimum finder
			for (double x = start; x < stop; x += step) {
				for (double y = start; y < stop; y += step) {
					double sol = himmelblau(x, y);
					bestSolution.trySolution(sol, x, y);
				}
			}
		}
	}
}
