package uk.ti;

import com.timeindexing.time.*;

/**
 * First test of TimeCalculator addition
 */
public class TestTCA {
    public static void main(String [] args) {
	SecondTimestamp t0 = (SecondTimestamp)Clock.time.asSeconds();

	MillisecondTimestamp t0m = TimeCalculator.toMillis(t0);
	MicrosecondTimestamp t0u = TimeCalculator.toMicros(t0m);
	NanosecondTimestamp t0n = TimeCalculator.toNanos(t0m);
	SecondTimestamp t0s = TimeCalculator.toSeconds(t0m);

	System.out.print("t0m = " + t0m);
	System.out.print(" t0u = " + t0u);
	System.out.print(" t0n = " + t0n);
	System.out.print(" t0s = " + t0s);
	System.out.print("\n");


	Timestamp t1 = new ElapsedMillisecondTimestamp(333);
	Timestamp t2 = new ElapsedMicrosecondTimestamp(333444);
	Timestamp t3 = new ElapsedNanosecondTimestamp(333444555);
	Timestamp t4 = new ElapsedSecondTimestamp(30);

	System.out.print("t1 = " + t1);
	System.out.print("\n");

	System.out.println("t0s + t1 " + TimeCalculator.addTimestamp(t0s, t1));
	System.out.println("t0m + t1 " + TimeCalculator.addTimestamp(t0m, t1));
	System.out.println("t0u + t1 " + TimeCalculator.addTimestamp(t0u, t1));
	System.out.println("t0n + t1 " + TimeCalculator.addTimestamp(t0n, t1));
	System.out.print("\n");

	System.out.print("t2 = " + t2);
	System.out.print("\n");

	System.out.println("t0s + t2 " + TimeCalculator.addTimestamp(t0s, t2));
	System.out.println("t0m + t2 " + TimeCalculator.addTimestamp(t0m, t2));
	System.out.println("t0u + t2 " + TimeCalculator.addTimestamp(t0u, t2));
	System.out.println("t0n + t2 " + TimeCalculator.addTimestamp(t0n, t2));
	System.out.print("\n");

	System.out.print("t3 = " + t3);
	System.out.print("\n");

	System.out.println("t0s + t3 " + TimeCalculator.addTimestamp(t0s, t3));
	System.out.println("t0m + t3 " + TimeCalculator.addTimestamp(t0m, t3));
	System.out.println("t0u + t3 " + TimeCalculator.addTimestamp(t0u, t3));
	System.out.println("t0n + t3 " + TimeCalculator.addTimestamp(t0n, t3));
	System.out.print("\n");

	System.out.print("t4 = " + t4);
	System.out.print("\n");

	System.out.println("t0s + t4 " + TimeCalculator.addTimestamp(t0s, t4));
	System.out.println("t0m + t4 " + TimeCalculator.addTimestamp(t0m, t4));
	System.out.println("t0u + t4 " + TimeCalculator.addTimestamp(t0u, t4));
	System.out.println("t0n + t4 " + TimeCalculator.addTimestamp(t0n, t4));
	System.out.print("\n");

	System.out.println("t1 + t1 " + TimeCalculator.addTimestamp(t1, t1));
	System.out.println("t2 + t1 " + TimeCalculator.addTimestamp(t2, t1));
	System.out.println("t3 + t1 " + TimeCalculator.addTimestamp(t3, t1));
	System.out.println("t4 + t1 " + TimeCalculator.addTimestamp(t4, t1));
	System.out.print("\n");

	System.out.println("t1 + t2 " + TimeCalculator.addTimestamp(t1, t2));
	System.out.println("t2 + t2 " + TimeCalculator.addTimestamp(t2, t2));
	System.out.println("t3 + t2 " + TimeCalculator.addTimestamp(t3, t2));
	System.out.println("t4 + t2 " + TimeCalculator.addTimestamp(t4, t2));
	System.out.print("\n");

	System.out.println("t1 + t3 " + TimeCalculator.addTimestamp(t1, t3));
	System.out.println("t2 + t3 " + TimeCalculator.addTimestamp(t2, t3));
	System.out.println("t3 + t3 " + TimeCalculator.addTimestamp(t3, t3));
	System.out.println("t4 + t3 " + TimeCalculator.addTimestamp(t4, t3));
	System.out.print("\n");

	System.out.println("t1 + t4 " + TimeCalculator.addTimestamp(t1, t4));
	System.out.println("t2 + t4 " + TimeCalculator.addTimestamp(t2, t4));
	System.out.println("t3 + t4 " + TimeCalculator.addTimestamp(t3, t4));
	System.out.println("t4 + t4 " + TimeCalculator.addTimestamp(t4, t4));
	System.out.print("\n");




	System.out.print("\n");

    }
}

