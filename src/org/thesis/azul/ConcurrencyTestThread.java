package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;



public class ConcurrencyTestThread implements Runnable {
	
	private sharedData data;
	private final int iterations;
	private final boolean set;
	private final boolean read;
	
	ConcurrencyTestThread(sharedData data, int iterations, boolean set, boolean read) {
		this.iterations = iterations;
		this.data = data;
		this.set = set;
		this.read = read;
	}
	
	
	public void run() {
		data.loop(iterations, set, read);
	}
}
