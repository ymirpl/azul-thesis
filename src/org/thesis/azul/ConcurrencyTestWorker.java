package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;
import java.lang.Math;

/**
 * Klasa reprezentująca wątek rozwiązujący zadanie optymalizacji metodą "dziel i zwyciężaj". 
 * Taki wątek może również posłużyć po prostu do symulowania działania wątku wykonującego obliczenia. 
 *
 */
public class ConcurrencyTestWorker implements Runnable {
	private long iterations = 0;
	private double start = 0;
	private double stop = 0;
	private double step = 0;
	private sharedData bestSolution;
	private boolean sync = false;
	private boolean countWrite = false;
	
	/**
	 * Konstruktor w wersji wątku symulującego obliczenia.  
	 * @param iterations ilość iteracji do zakończenia wątku, podczas których będą symulowane obliczenia matematyczne.
	 */
	public ConcurrencyTestWorker(long iterations) {
		this.iterations = iterations;
	}

	/**
	 * Konstruktor wersji workera do optymalizacji. 
	 * @param sd współdzielone dane (informacja o rozwiązaniu)
	 * @param start początek przedziału
	 * @param stop koniec przedziału
	 * @param step krok
	 * @param sync czy w wersji synchronizowanej po każdym kroku
	 */
	public ConcurrencyTestWorker(sharedData sd, double start, double stop,
			double step, boolean sync) {
		this.stop = stop;
		this.start = start;
		this.step = step;
		this.bestSolution = sd;
		this.sync = sync;

	}
	
	/**
	 * Konstruktor w wersji z licznikiem dostępu do wspólnych danych w celu ich zapisania. 
	 * @param sd współdzielone dane (informacja o rozwiązaniu)
	 * @param start początek przedziału
	 * @param stop koniec przedziału
	 * @param step krok
	 * @param sync czy w wersji synchronizowanej po każdym kroku
	 * @param countWrite czy zliczać zapisy we wspólnych danych
	 */
	public ConcurrencyTestWorker(sharedData sd, double start, double stop,
			double step, boolean sync, boolean countWrite) {
	this(sd, start, stop, step, sync);
	this.countWrite = countWrite;
	}

	/**
	 * Funkcja używana do optymalizacji i symulowania obliczeń matematycznych. 
	 * @param x argument rzeczywisty
	 * @return wartość funkcji
	 */
	private double function(double x) {
		return (x*Math.log10(Math.abs(x)+ 0.0001) + Math.max(-1*Math.pow(x+1, 3), 0));
	}

	/**
	 * Uruchomienie wątku
	 */
	public void run() {

		if (step == 0) { // wątek będzie symulował wykonywanie obliczeń matematycznych
			for (int i = 0; i < iterations; i++) {
				function(2); // udaj, że coś liczysz
			}
			return;

		} else { // szukanie minimum metodą brutalną
			double solution = Double.MAX_VALUE;
			double solX = 0;
			for (double x = start; x < stop; x += step) {
				double out = function(x);
				if (this.sync)
					if(!this.countWrite)
						bestSolution.trySolution(out, x);
					else
						bestSolution.trySolutionWithWriteCounter(out, x);
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
