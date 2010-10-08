package org.thesis.azul;

import org.thesis.azul.ConcurrencyTestThread;

public class RunTests {
	
	public static Thread[] threads;
	
	public class sharedData {
		
		private int counter = 0;
		
		public synchronized void loop(int iterations, boolean set, boolean read) {
			int counter = 0;
			while (!(counter == iterations)) {
				counter++;
				try {
					Thread.sleep(0, 1);
				} catch (InterruptedException e) {
					return;
				}
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
	
	
	public void iterationTest() throws InterruptedException {
		
		sharedData sd = new sharedData();
		
		threads = new  Thread[2];
		threads[0] = new Thread(new ConcurrencyTestThread(sd, 2000, false, false));
		threads[1] = new Thread(new ConcurrencyTestThread(sd, 2000, false, true));
		
		threads[0].start();
		threads[1].start();
		
		threads[1].join();
		threads[0].join();
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
		RunTests tests = new RunTests();
		
		long startTime = System.currentTimeMillis();
		tests.iterationTest();
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time of iterationTest(): " + (endTime-startTime) + "ms");


	}

}
