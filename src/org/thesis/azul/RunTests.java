package org.thesis.azul;

import org.thesis.azul.ConcurrencyTestThread;

/**
 * Główna klasa skupiącja metody wykonujące testy porównawcze maszyny Azul i AMD. 
 * @author Marcin Mincer
 *
 */
public class RunTests {
	
	public int ITERATIONS = 1000;

	
	
	public static Thread[] threads;
	
	/**
	 * Klasa reprezentuje pamięć współdzieloną. 
	 * Wielodostęp jest realizowany przez semfory binarne (metody synchronizowane). 
	 *
	 */
	public class sharedData {
		private double data = 0;
		public double x = 0;
		public long readCounter = 0;
		public long writeCounter = 0;
		
		/**
		 * Pętla w której zachodzi czytanie i pisanie do pamięci współdzielonej, wykonywana wewnątrz sekcji krytycznej. 
		 * Takie sposób wykonywania pętli wymusza konieczność dłuższego oczekiwania przez wątki na dostęp do sekcji krytycznej i ułatwia obserwację 
		 * optymalizacji (lub jej braku) OTC. 
		 * @param iterations liczba iteracji
		 * @param set czy ustawiać
		 * @param read czy pisać
		 * @return
		 */
		public synchronized double loop(int iterations, boolean set, boolean read) {
			int cc = 0;
			while (cc != iterations) {
				cc++;
			}
			if (set) {
				this.data = iterations;
			}
			if (read) {
				return this.data;
			}
			return 0;
		}

		/**
		 * Getter współdzielonej pamięci
		 * @return przeczytana liczba
		 */
		public synchronized double getData() {
			return this.data;
		}

		/**
		 * Setter współdzielonej pamięci
		 * @param data liczba do wpisania
		 */
		public synchronized void setData(double data) {
			this.data = data;
		}
		
		/**
		 * Metoda służy do wpisywanie znalezionego przez wątek workera rozwiązania do współdzielonej pamięci. 
		 * Wartość zapisa jest zmieniana, jeżeli znalezione rozwiązanie jest lepsze od dotychczasowego. 
		 * @param sol wartość optymalizowanej funkcji
		 * @param x argument, dla którego funkcja osiąga tę wartość
		 */
		public synchronized void trySolution(double sol, double x) {
			if (sol < this.data) {
				this.data = sol;
				this.x = x;
			}
		}
		
		/**
		 * Metda identyczna do trySolution z funkcją liczenia ilości faktycznych zapisów. 
		 * @see trySolution
		 */
		public synchronized void trySolutionWithWriteCounter(double sol, double x) {
			this.readCounter++;
			if (sol < this.data) {
				this.writeCounter++;
				this.data = sol;
				this.x = x;
			}
		}
	}
	
	
	/**
	 * Test OTC, wersja prostsza, iteracje pętli wykonywane w osobnych wątkach
	 * @param THREAD_NO liczba wątków
	 * @param write_share udział wątków piszących (co który wątek)
	 * @param read czy czytać
	 */
	public void simpleRollbackTest(int THREAD_NO, int write_share, boolean read)
			throws InterruptedException {

		sharedData sd = new sharedData();

		threads = new Thread[THREAD_NO];
		long startTime = System.currentTimeMillis();
		for (int j = 0; j < threads.length; j++) {
			boolean write = false;

			if (write_share != 0 && j % write_share == 0) 
				write = true;

			threads[j] = new Thread(new ConcurrencyTestThread(sd, ITERATIONS,
					write, read, true));
			threads[j].start();
		}

		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time simpleRollbackTest(" + THREAD_NO
				+ " " + write_share + " " + read + "): " + (endTime - startTime)
				+ "ms");

	}
	
	/**
	 * Test OTC. Wersja w której iteracje są wykonywane wewnątrz synchronizowanej metody. 
	 * @param THREAD_NO liczba wątków
	 * @param read_write_share udział wątków czytających i piszących (co który wątek)
	 */
	public void rollbackTest(int THREAD_NO, int read_write_share)
			throws InterruptedException {

		sharedData sd = new sharedData();

		threads = new Thread[THREAD_NO];
		long startTime = System.currentTimeMillis();
		for (int j = 0; j < threads.length; j++) {
			boolean read = false;
			boolean write = false;

			if (read_write_share != 0 && j % read_write_share == 0) {
				write = true;
			} else if (read_write_share != 0 && j % read_write_share == 1)
				read = true;

			threads[j] = new Thread(new ConcurrencyTestThread(sd, ITERATIONS, write,
					read, false));
			threads[j].start();
		}

		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time rollbackTest(" + THREAD_NO
				+ " " + read_write_share + " ): " + (endTime - startTime) + "ms");


	}
	
	/**
	 * Test OTC wykorzystujący sformułowane wcześniej testy.
	 * 
	 * @see rollbackTest
	 * @see simpleRollbackTest
	 * @return czas wykonania [ms]
	 */
	public long octTestMixed() throws InterruptedException {
		long startTime = System.currentTimeMillis();
		
		System.out.println("Mixed test iters: "+ ITERATIONS);
		
		simpleRollbackTest(300, 0, false);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, false);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 2, true);
		simpleRollbackTest(300, 2, true);
		simpleRollbackTest(300, 2, true);
		simpleRollbackTest(300, 10, true);
		simpleRollbackTest(300, 100, true);
		simpleRollbackTest(300, 50, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, true);
		simpleRollbackTest(300, 0, false);
		rollbackTest(300, 0 );
		rollbackTest(300, 0 );
		rollbackTest(300, 0 );
		rollbackTest(300, 0 );
		rollbackTest(300, 0 );
		rollbackTest(300, 10 );
		rollbackTest(300, 10 );
		rollbackTest(300, 100 );
		rollbackTest(300, 10 );
		rollbackTest(300, 0 );
		rollbackTest(300, 0 );
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time OCT mixed test: " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
	/**
	 * Test OTC wykorzystujący jedynie simpleRollbackTest z różnymi parametrami.
	 * @see simpleRollbackTest 
	 * @return czas wykonania [ms]
	 */
	public long octTestEasier() throws InterruptedException {
		System.out.println("Mixed easier iters: "+ ITERATIONS);
		long startTime = System.currentTimeMillis();

		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 0, false);
		simpleRollbackTest(192, 2, true);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 100, true);
		simpleRollbackTest(192, 50, true);
		simpleRollbackTest(192, 10, true);
		simpleRollbackTest(192, 2, true);
		simpleRollbackTest(192, 2, true);
		simpleRollbackTest(192, 2, true);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 0, true);
		simpleRollbackTest(192, 0, false);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time OCT easier test: " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
	/**
	 * Test OTC wykorzystujący jedynie rollbackTest z różnymi parametrami.
	 * @see rollbackTest 
	 * @return czas wykonania [ms]
	 */
	public long octTestHarder() throws InterruptedException {
		System.out.println("Mixed harder iters: "+ ITERATIONS);
		long startTime = System.currentTimeMillis();

		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 10);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 100);
		rollbackTest(192, 10);
		rollbackTest(192, 10);
		rollbackTest(192, 100);
		rollbackTest(192, 10);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		rollbackTest(192, 0);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time OCT harder test: " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
	/**
	 * Metoda uruchamia współbieżnie zadaną liczbę wątków symulujących wykonywanie obliczeń. 
	 * @param THREAD_NO liczba wątków
	 * @param iterations liczba iteracji w wątku
	 * @throws InterruptedException
	 */
	public void spawnThreads(int THREAD_NO, long iterations) throws InterruptedException {
		threads = new Thread[THREAD_NO];

		long startTime = System.currentTimeMillis();
		for (int j = 0; j < threads.length; j++) {

			threads[j] = new Thread(new ConcurrencyTestWorker(iterations));
			threads[j].start();
		}

		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time spawnThreadTest(" + THREAD_NO
				+ " " + iterations + " ): " + (endTime - startTime) + "ms");


	}
	
	/**
	 * Test współbieżności dużej ilości wątków. 
	 * @return czas wykonania [ms]
	 * @throws InterruptedException
	 */
	public long testSpawning() throws InterruptedException {
		long startTime = System.currentTimeMillis();

		for (int i = 1; i < 300; i++) {
			spawnThreads(i, 1000000);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time testSpawning(): " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
	/**
	 * Test "dziel i zwyciężaj"
	 * @param THREAD_NO liczba wątków
	 * @param step wielkość kroku
	 * @param sync wersja (a)synchroniczna
	 * @return czas wykonania [ms]
	 * @throws InterruptedException
	 */
	public long divideAndConquer(int THREAD_NO, double step, boolean sync) throws InterruptedException {
		sharedData bestS = new sharedData();
		bestS.setData(Double.MAX_VALUE);
		
		double min = -6;
		double max = 6;
		
		double shard = (max-min) / THREAD_NO; 
		
		
		threads = new Thread[THREAD_NO];
		
		long startTime = System.currentTimeMillis();
		
		for (int j = 0; j < threads.length; j++) {

			threads[j] = new Thread(new ConcurrencyTestWorker(bestS, min+j*shard, min+(j+1)*shard, step, sync));
			threads[j].start();
		}
		
		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		long result = endTime - startTime;
		System.out.println("Total execution time optimize(" + THREAD_NO + " " + step + " " + sync +"): " + (endTime - startTime) + "ms");
		System.out.println("Solution found:" + bestS.getData() + " for " + bestS.x );
		return result;
	}
	
	
	/**
	 * Wersja testu "dziel i zwyciężaj" z liczeniem faktycznych zapisów do pamięci współdzielonej
	 * @see divideAndConquer
	 */
	public long divideAndConquerCountWrite(int THREAD_NO, double step) throws InterruptedException {
		sharedData bestS = new sharedData();
		bestS.setData(Double.MAX_VALUE);
		
		double min = -6;
		double max = 6;
		
		double shard = (max-min) / THREAD_NO; 
		
		
		threads = new Thread[THREAD_NO];
		
		long startTime = System.currentTimeMillis();
		
		for (int j = 0; j < threads.length; j++) {

			threads[j] = new Thread(new ConcurrencyTestWorker(bestS, min+j*shard, min+(j+1)*shard, step, true, true));
			threads[j].start();
		}
		
		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		long resutl = endTime - startTime;
		System.out.println("Total execution time optimize(" + THREAD_NO + " " + step + " true, countWrite): " + (endTime - startTime) + "ms");
		System.out.println("Solution found:" + bestS.getData() + " for " + bestS.x );
		double writesPercent = ((double) bestS.writeCounter/ (double) bestS.readCounter)*100.0;
		System.out.println("Writes : " + writesPercent);
		return resutl;
	}
	
	/**
	 * Test wykorzystania maszyny Azul do faktycznych celów badawczych (minimalizacja metodą brutalną). 
	 *
	 */
	public void divideAndConquerTest() throws InterruptedException {

		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		
		divideAndConquer(3, 0.00000001, false);
		divideAndConquer(192, 0.00000001, false);
		
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, false);
		
		divideAndConquer(193, 0.00000001, false);
		divideAndConquer(193, 0.00000001, false);
		divideAndConquer(193, 0.00000001, true);
		divideAndConquer(193, 0.00000001, true);
	}
	
	/**
	 * Poszukiwanie optymalnej liczby wątków wykonujących zadanie na maszynie Azul.
	 * Za optymalną uznaje się liczbę wątków, którym wykonanie zadania zajmuje najkrócej.  
	 * @throws InterruptedException
	 */
	public void divideAndConquerMinimumSearchTest() throws InterruptedException {
		for (int i = 3; i <= 193; i += 10)
			divideAndConquer(i, 0.00000001, true);
		
	}
	
	public void divideAndConquerTestAthlon() throws InterruptedException {
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		
		divideAndConquer(3, 0.00000001, false);
		divideAndConquer(192, 0.00000001, false);
		
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(3, 0.00000001, false);
		divideAndConquer(3, 0.00000001, false);
	}
	
	public static void main(String[] args) throws InterruptedException {

		RunTests tests = new RunTests();
						
		int runs = 3;
		
    	long sum = 0;
  	
    	tests.ITERATIONS = 1000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Średni czas wykonania octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 10000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Średni czas wykonania octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 100000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Średni czas wykonania octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 1000;
		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestEasier();
		}
		System.out.println("Średni czas wykonania octEasier" + runs + ": " + sum/runs + "\n\n\n");

		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestHarder();
		}
		System.out.println("Średni czas wykonania octHarder" + runs + ": " + sum/runs + "\n\n\n");
		
		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.testSpawning();
		}
		System.out.println("Średni czas wykonania spawning" + runs + ": " + sum/runs + "\n\n\n");

		tests.divideAndConquerTest();
    	tests.divideAndConquerCountWrite(193, 0.00000001);
    	tests.divideAndConquerMinimumSearchTest();
    	

	}

}
