package uk.ti;

import com.timeindexing.basic.*;
import com.timeindexing.time.*;

import java.util.Date;

/**
 * First test of TimestampDecoder.
 */
public class TestTS {
    public static void main(String [] args) {
	Timestamp t0 = new MillisecondTimestamp();

	delay(20);

	Timestamp t1 = new MicrosecondTimestamp(new Date());

	delay(20);

	Timestamp t2 = new NanosecondTimestamp();


	delay(50);

	Timestamp tc = new ElapsedMillisecondTimestamp((long)(26.12245*1));

	Timestamp td = new ElapsedMicrosecondTimestamp((long)(26.12245*1000));

	Timestamp te = new SecondTimestamp();

	Timestamp t3c = new ElapsedNanosecondTimestamp((long)(26.12245*1000000));

	Timestamp t3d = new ElapsedSecondTimestamp(203);

	Interval i0 = new EndPointInterval((AbsoluteTimestamp)t0, (AbsoluteTimestamp)t2);

	Interval i1 = new EndPointInterval((AbsoluteTimestamp)t0, (Position)new AbsolutePosition(1234));

	Interval i2 = new EndPointInterval((AbsoluteTimestamp)t0, (Count)new RelativeCount(100));


	Interval i3 = new EndPointInterval((AbsoluteTimestamp)t0, (RelativeTimestamp)t3c);

	System.out.print("t0 = " + t0);
	System.out.print("\n");

	System.out.print("t1 = " + t1);
	System.out.print("\n");

	System.out.print("t2 = " + t2);
	System.out.print("\n");

	System.out.print("tc = " + tc);
	System.out.print("\n");

	System.out.print("td = " + td);
	System.out.print("\n");

	System.out.print("te = " + te);
	System.out.print("\n");

	System.out.print("t3c = " + t3c);
	System.out.print("\n");

	System.out.print("t3d = " + t3d);
	System.out.print("\n");

	System.out.print("\n");

	TimestampDecoder decoder = new TimestampDecoder();

	System.out.print(" >> t0 decoded class = " +
			 decoder.decode(t0.value()).getClass().getName());
	System.out.print("\n");
			 
	System.out.print(" >> t1 decoded class = " +
			 decoder.decode(t1.value()).getClass().getName());
	System.out.print("\n");
			 
	System.out.print(" >> t2 decoded class = " +
			 decoder.decode(t2.value()).getClass().getName());
	System.out.print("\n");
			 

	System.out.print(" >> tc decoded class = " +
			 decoder.decode(tc.value()).getClass().getName());
	System.out.print("\n");
			 
	System.out.print(" >> td decoded class = " +
			 decoder.decode(td.value()).getClass().getName());
	System.out.print("\n");
			 
	System.out.print(" >> te decoded class = " +
			 decoder.decode(te.value()).getClass().getName());
	System.out.print("\n");
			 
	System.out.print(" >> t3c decoded class = " +
			 decoder.decode(t3c.value()).getClass().getName());
	System.out.print("\n");

	System.out.print(" >> t3d decoded class = " +
			 decoder.decode(t3d.value()).getClass().getName());
	System.out.print("\n");

	System.out.print("\n");

	System.out.print("i0 = " + i0);
	System.out.print("\n");

	System.out.print("i1 = " + i1);
	System.out.print("\n");

	System.out.print("i2 = " + i2);
	System.out.print("\n");

	System.out.print("i3 = " + i3);
	System.out.print("\n");

			 

    }

    /**
     * Delay a few milliseconds.
     */
    public static void delay(int d) {
	try {
	    Thread.sleep(d);
	} catch (InterruptedException ie) {
	    ;
	}
    }
}

