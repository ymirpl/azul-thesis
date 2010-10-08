package org.thesis.azul;

import org.thesis.azul.ConcurrencyTestThread;

public class RunTests {
	
	public static Thread[] threads;
	
	public class sharedData {
		
		private int counter = 0;
		
		public synchronized void loop(int iterations, boolean set, boolean read) {

			int cc = 0;
			while (cc != iterations) {
				cc++;
			}

			if (set) {
				this.counter = iterations;
			}

			if (read) {
				System.out.println("Shared data is now  " + this.counter);
			}
			return;
		}
	}
	
	public void rollbackTest(int THREAD_NO, int read_write_share) throws InterruptedException {
		
		sharedData sd = new sharedData();

		for (int i = 0; i < 100; i++) {
			long startTime = System.currentTimeMillis();

			threads = new Thread[THREAD_NO];

			for (int j = 0; j < threads.length; j++) {
				boolean read = false;
				boolean write = false;

				if (read_write_share != 0 && j % read_write_share == 0) {
					write = true; }
				else if (read_write_share != 0 && j % read_write_share == 1)
					read = true;

				threads[j] = new Thread(new ConcurrencyTestThread(sd, 10000000,
						write, read));
				threads[j].start();
			}

			for (int j = 0; j < threads.length; j++) {
				threads[j].join();
			}

			long endTime = System.currentTimeMillis();
			System.out.println("Step execution time rollbackTest("+ THREAD_NO +" "+ read_write_share +"): "
					+ (endTime - startTime) + "ms");
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		
		RunTests tests = new RunTests();
		tests.rollbackTest(30, 0);
		tests.rollbackTest(30, 5);
	}

}
