package uk.ti;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;

/**
 * First test of timestamps.
 */
public class Test1 {
    public static void main(String [] args) {
	Timestamp t0 = new MillisecondTimestamp();

	System.out.println("t0 = " + t0.getSeconds() + ":" + t0.getNanoSeconds());

	try {
	    Thread.sleep(147, 1);
	} catch (InterruptedException ie) {
	    ;
	}

	Timestamp t1 = new MillisecondTimestamp();

	System.out.println("t1 = " + t1.getSeconds() + ":" + t1.getNanoSeconds());
    }
}

