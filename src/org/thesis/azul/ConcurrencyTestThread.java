package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;

public class ConcurrencyTestThread implements Runnable {
	
	private sharedData data;
	private final int iterations;
	private final boolean set;
	private final boolean read;
	private final boolean selfLoop;
	
	ConcurrencyTestThread(sharedData data, int iterations, boolean set, boolean read, boolean selfLoop) {
		this.iterations = iterations;
		this.data = data;
		this.set = set;
		this.read = read;
		this.selfLoop = selfLoop;
	}
	
	public void run() {

		double readData = -1;
		if (!selfLoop) {
			for (int i = 0; i < iterations; i++) {
				 readData = data.loop(1000, set, read);
			}
		} else {
			for (int i = 0; i < iterations; i++) {
				if (read)
					readData = data.getData();
				if (set)
					data.setData(777);
			}
		}
		
		//System.out.println(readData);
	}
}
