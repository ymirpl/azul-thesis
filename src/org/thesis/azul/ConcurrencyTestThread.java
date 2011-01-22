package org.thesis.azul;

import org.thesis.azul.RunTests.sharedData;

/**
 * Obiekty tej klasy będą używane jako wątki do testowania wielowątkowych działań na maszynach.  
 * 
 * 
 */
public class ConcurrencyTestThread implements Runnable {
	
	private sharedData data; // współdzielone dane 
	private final int iterations; // liczba iteracji, jakie wykona wątek przed swoim zakończeniem
	private final boolean set; // pisanie do wspólnej pamięci
	private final boolean read; // czytanie ze wspólnej pamięci
	private final boolean selfLoop; // pętla wykonywana w wątku (true) lub wewnątrz synchronizowanej metody w obiekcie typu sharedData
	
	/**
	 * Konstruktor. 
	 * @param data współdzielone dane 
	 * @param iterations liczba iteracji, jakie wykona wątek przed swoim zakończeniem
	 * @param set czy wątek będzie pisał do wspólnej pamięci
	 * @param read czy wątek będzie czytał ze wspólnej pamięci
	 * @param selfLoop czy iteracje pętli będą wykonywane wewnątrz wątku (true), czy też wewnątrz synchronizowanej metody obiektu współdzielonego
	 */
	ConcurrencyTestThread(sharedData data, int iterations, boolean set, boolean read, boolean selfLoop) {
		this.iterations = iterations;
		this.data = data;
		this.set = set;
		this.read = read;
		this.selfLoop = selfLoop;
	}
	
	/**
	 * Metoda uruchamiająca wątek
	 */
	public void run() {

		double readData = -1;
		if (!selfLoop) {
			for (int i = 0; i < iterations; i++) {
				 readData = data.loop(1000, set, read); // metoda wewnątrz obiektu
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
