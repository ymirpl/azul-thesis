package org.thesis.azul;

import org.thesis.azul.ConcurrencyTestThread;

public class RunTests {
	
	public static Thread[] threads;
	
	public class sharedData {
		private double data = 0;
		public double x = 0;
		public double y = 0;

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
		
		public synchronized void trySolution(double sol, double x, double y) {
			if (sol < this.data) {
				this.data = sol;
				this.x = x;
				this.y = y;
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

			threads[j] = new Thread(new ConcurrencyTestThread(sd, 100000,
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

			threads[j] = new Thread(new ConcurrencyTestThread(sd, 100000, write,
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
		long startTime = System.currentTimeMillis();

		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 0, false);
		simpleRollbackTest(182, 2, true);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 100, true);
		simpleRollbackTest(182, 50, true);
		simpleRollbackTest(182, 10, true);
		simpleRollbackTest(182, 2, true);
		simpleRollbackTest(182, 2, true);
		simpleRollbackTest(182, 2, true);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 0, true);
		simpleRollbackTest(182, 0, false);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time OCT easier test: " + (endTime - startTime) + "ms");
		return endTime - startTime;
	}
	
	public long octTestHarder() throws InterruptedException {
		long startTime = System.currentTimeMillis();

		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 10);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 100);
		rollbackTest(182, 10);
		rollbackTest(182, 10);
		rollbackTest(182, 100);
		rollbackTest(182, 10);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		rollbackTest(182, 0);
		
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
	
	public long divideAndConquer(int THREAD_NO, double step) throws InterruptedException {
		sharedData bestS = new sharedData();
		bestS.setData(Double.MAX_VALUE);
		
		double min = -5;
		double max = 5;
		
		double shard = (max-min) / THREAD_NO; 
		
		
		threads = new Thread[THREAD_NO];
		
		long startTime = System.currentTimeMillis();
		
		for (int j = 0; j < threads.length; j++) {

			threads[j] = new Thread(new ConcurrencyTestWorker(bestS, min+j*shard, min+(j+1)*shard, step));
			threads[j].start();
		}
		
		for (int j = 0; j < threads.length; j++) {
			threads[j].join();
		}
		long endTime = System.currentTimeMillis();
		long resutl = endTime - startTime;
		System.out.println("Total execution time optimize(" + THREAD_NO + " " + step + "): " + (endTime - startTime) + "ms");
		System.out.println("Solution found:" + bestS.getData() + " for " + bestS.x + " " + bestS.y);
		return resutl;
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		RunTests tests = new RunTests();
		
		int runs = 3;
		
    	long sum = 0;
    	
		for (int i = 0; i < runs; i++) {
			System.out.println("Test run " + i);
			sum += tests.octTestMixed();
		}
		System.out.println("Mean time of octMixed" + runs + ": " + sum/runs + "\n\n\n");

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
		
		
		tests.divideAndConquer(2, 0.0001);
		tests.divideAndConquer(123, 0.0001);
		
		
		
		
	}

}
