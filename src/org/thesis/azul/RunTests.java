package org.thesis.azul;

import org.thesis.azul.ConcurrencyTestThread;

public class RunTests {
	
	public int ITERATIONS = 1000;

	
	
	public static Thread[] threads;
	
	public class sharedData {
		private double data = 0;
		public double x = 0;
		public long readCounter = 0;
		public long writeCounter = 0;

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

		public synchronized double getData() {
			return this.data;
		}

		public synchronized void setData(double data) {
			this.data = data;
		}
		
		public synchronized void trySolution(double sol, double x) {
			if (sol < this.data) {
				this.data = sol;
				this.x = x;
			}
		}
		
		public synchronized void trySolutionWithWriteCounter(double sol, double x) {
			this.readCounter++;
			if (sol < this.data) {
				this.writeCounter++;
				this.data = sol;
				this.x = x;
			}
		}
	}
	
	
	
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
	
	public long testSpawning() throws InterruptedException {
		long startTime = System.currentTimeMillis();

		for (int i = 1; i < 300; i++) {
			spawnThreads(i, 1000000);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time testSpawning(): " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
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
		long resutl = endTime - startTime;
		System.out.println("Total execution time optimize(" + THREAD_NO + " " + step + " " + sync +"): " + (endTime - startTime) + "ms");
		System.out.println("Solution found:" + bestS.getData() + " for " + bestS.x );
		return resutl;
	}
	
	
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
	
	
	public void divideAndConquerTest() throws InterruptedException {
/*
		divideAndConquer(3, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		
		divideAndConquer(3, 0.00000001, false);
		divideAndConquer(192, 0.00000001, false);
		
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, true);
		divideAndConquer(192, 0.00000001, false);
*/		
		divideAndConquer(193, 0.00000001, false);
		divideAndConquer(193, 0.00000001, false);
		divideAndConquer(193, 0.00000001, true);
		divideAndConquer(193, 0.00000001, true);
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
/*  	
    	tests.ITERATIONS = 1000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Mean time of octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 10000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Mean time of octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 100000;
    	sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Mean time of octMixed " + runs + " " + tests.ITERATIONS +":  "+ sum/runs + "\n\n\n");

		
		tests.ITERATIONS = 1000;
		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestEasier();
		}
		System.out.println("Mean time of octEasier" + runs + ": " + sum/runs + "\n\n\n");

		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestHarder();
		}
		System.out.println("Mean time of octHarder" + runs + ": " + sum/runs + "\n\n\n");
		
		sum = 0;
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.testSpawning();
		}
		System.out.println("Mean time of spawning" + runs + ": " + sum/runs + "\n\n\n");
*/
//		tests.divideAndConquerTest();
    	tests.divideAndConquerCountWrite(193, 0.00000001);
    	

	}

}
