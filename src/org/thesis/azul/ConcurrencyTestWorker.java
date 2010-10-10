package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;
import java.lang.Math;

public class ConcurrencyTestWorker implements Runnable {
	private long iterations = 0;
	private double start = 0;
	private double stop = 0;
	private double step = 0;
	private sharedData bestSolution;
	private boolean sync = false;

	public ConcurrencyTestWorker(long iterations) {
		this.iterations = iterations;
	}

	public ConcurrencyTestWorker(sharedData sd, double start, double stop,
			double step, boolean sync) {
		this.stop = stop;
		this.start = start;
		this.step = step;
		this.bestSolution = sd;
		this.sync = sync;

	}

	private double function(double x) {
		return (x*Math.log10(Math.abs(x)+ 0.0001) + Math.max(-1*Math.pow(x+1, 3), 0));
	}

	public void run() {

		if (step == 0) { // simple iterative worker
			for (int i = 0; i < iterations; i++) {
				function(2); // pretend some computations
			}
			return;

		} else { // brute force minimum finder
			double solution = Double.MAX_VALUE;
			double solX = 0;
			for (double x = start; x < stop; x += step) {
				double out = function(x);
				if (this.sync)
					bestSolution.trySolution(out, x);
				else {
					if (out < solution) {
						solution = out;
						solX = x;
					}
				}

			}
			if (!sync)
				bestSolution.trySolution(solution, solX);
		}
	}
}
