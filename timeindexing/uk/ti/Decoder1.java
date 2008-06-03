// Decoder1.java
package uk.ti;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import com.timeindexing.time.*;
import java.lang.reflect.Constructor;

/**
 * A class that does TimestampDecoder implementation
 * using a set of tuples and a dispatcher.
 */
public class Decoder1 {

    public static void main(String[] args) {
	Decoder1 decoder1 = new Decoder1();

	//System.err.println( "maskSet = " + decoder1.maskSet);

	int COUNT = 10000000;
	Timestamp start = null;
	Timestamp elapsed = null;
	Timestamp ms1 = new MillisecondTimestamp();
	Timestamp ens1 = new ElapsedNanosecondTimestamp(49264264248L);
	Timestamp un1 = new MPEGPresentationTimestamp(9482934239L);


	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp res = decoder1.decode(ms1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " MillisecondTimestamp in " + elapsed);

	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp ens2 = decoder1.decode(ens1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " ElapsedNanosecondTimestamp in " + elapsed);


	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp un2 = decoder1.decode(un1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " MPEGPresentationTimestamp in " + elapsed);


	// no e check with bulitin TimestampDecoder
	TimestampDecoder decoder2 = new TimestampDecoder();

	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp res = decoder2.decode(ms1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " MillisecondTimestamp in " + elapsed);

	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp ens2 = decoder2.decode(ens1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " ElapsedNanosecondTimestamp in " + elapsed);

	start = new NanosecondTimestamp();
 
	for (int i=0; i< COUNT; i++) {
	    Timestamp un2 = decoder2.decode(un1.value());
	}

	elapsed = TimeCalculator.elapsedSince(start);

	System.err.println("Decoded " + COUNT + " MPEGPresentationTimestamp in " + elapsed);


    }

    TreeSet maskSet = null;
    Object[] maskArray = null;

    private static Comparator tupleComparator = new Comparator() {
	    public int compare(Object o1, Object o2) {
		DecoderTuple t1 = (DecoderTuple)o1;
		DecoderTuple t2 = (DecoderTuple)o2;

		if (t1.mask < t2.mask) {
		    return -1;
		} else if (t1.mask > t2.mask) {
		    return 1;
		} else {
		    return 0;
		}
	    }
	};

    public Decoder1() {
	setup();
    }

    private void setup() {
	maskSet = new TreeSet(tupleComparator);

	addDecoder(Timestamp.TOP_2_MASK, Timestamp.MILLISECOND, MillisecondTimestamp.class);
	addDecoder(Timestamp.TOP_2_MASK, Timestamp.MICROSECOND, MicrosecondTimestamp.class);
	addDecoder(Timestamp.TOP_2_MASK, Timestamp.NANOSECOND, NanosecondTimestamp.class);
	addDecoder(Timestamp.TOP_4_MASK, Timestamp.ELAPSED_MILLISECOND, ElapsedMillisecondTimestamp.class);
	addDecoder(Timestamp.TOP_4_MASK, Timestamp.ELAPSED_MICROSECOND, ElapsedMicrosecondTimestamp.class);
	addDecoder(Timestamp.TOP_4_MASK, Timestamp.SECOND, SecondTimestamp.class);
	addDecoder(Timestamp.TOP_6_MASK, Timestamp.ELAPSED_NANOSECOND, ElapsedNanosecondTimestamp.class);
	addDecoder(Timestamp.TOP_6_MASK, Timestamp.ELAPSED_SECOND, ElapsedSecondTimestamp.class);
	addDecoder(Timestamp.TOP_8_MASK, Timestamp.ELAPSED_UNITS, MPEGPresentationTimestamp.class);
    }

    public void addDecoder(long mask, long value, Class clazz) {
	// find the DecoderTuple for mask
	Iterator maskI = maskSet.iterator();
	DecoderTuple theTuple = null;

	while (maskI.hasNext()) {
	    DecoderTuple tuple = (DecoderTuple)maskI.next();

	    if (tuple.mask == mask) {
		// found it
		theTuple = tuple;
		break;
	    }
	}

	if (theTuple == null) {
	    // we didn't find the mask so we have to build a new decoder
	    DecoderTuple newTuple = new DecoderTuple(mask);

	    newTuple.addDecodeInfo(value, clazz);

	    // now add it to the maskSet
	    maskSet.add(newTuple);
	} else {
	    // we found the tuple
	    theTuple.addDecodeInfo(value, clazz);
	}

	// tweak the maskSet into an array
	maskArray = new ArrayList(maskSet).toArray();
    }

    /**
     * Decode a long and return the relevant Timestamp
     */
    public Timestamp decode(long value) {
	// find the DecoderTuple for mask
	DecoderTuple tuple = null;
	Class clazz = null;

	int size = maskArray.length;
	for (int t=0; t < size; t++ ){
	    tuple = (DecoderTuple)maskArray[t];

	    // get the mesked value
	    long valueT = value & tuple.mask;

	    // now check to see if it matches any of this tuples values
	    for (int i=0; i < tuple.size; i++) {
		/*
		System.err.println(Long.toHexString(value) + "\t" +
				   Long.toHexString(valueT) + "\t" +
				   Long.toHexString(tuple.values[i]) + "\t" +
				   Long.toHexString(tuple.mask) );
		*/

		if (tuple.values[i] == valueT) {
		    // we found it, so return a Timestamp
		    // get the class
		    clazz = tuple.classes[i];

		    try {
			// get the constructor
			Constructor constructor = tuple.constructors[i];
			// create object
			Timestamp obj = (Timestamp)constructor.newInstance(value);

			return obj;

		    } catch (Exception e) {
			throw new TimestampDecoderException("Instantiation error for " + clazz + " with " + value);
		    }
		}
	    }

	    // skip to next one
	}

	// we found nothing so throw an Exception
	throw new TimestampDecoderException("No Timestamp Class for " + Long.toHexString(value));
    }

    private class DecoderTuple {
	long mask;
	int size = 0;
	long[] values = new long[4];
	Class[] classes = new Class[4];
	// a place to precache the constructors
	Constructor[] constructors = new Constructor[4];

	DecoderTuple(long mask) {
	    this.mask = mask;
	    System.err.println("Add new tuple for " + Long.toHexString(mask));
	}

	void addDecodeInfo(long value, Class clazz) {
	    // first we check if it's there
	    for (int i=0; i<size; i++) {
		if (values[i] == value) {
		    // it is there
		    // but we replace the value
		    classes[i] = clazz;

		    // get the constructor
		    try {
			Constructor constructor = clazz.getConstructor(long.class);
			constructors[i] = constructor;
		    } catch (Exception e) {
			throw new RuntimeException("No constructor with (long) for " + clazz);
		    }

		    System.err.println("Replace " + Long.toHexString(value) + " => " + clazz);
		    return;
		}
	    }

	    // if we got here we don;t have that value
	    values[size] = value;
	    classes[size] = clazz;
	    // get the constructor
	    try {
		Constructor constructor = clazz.getConstructor(long.class);
		constructors[size] = constructor;
	    } catch (Exception e) {
		throw new RuntimeException("No constructor with (long) for " + clazz);
	    }

	    size++;
	    System.err.println("New " + Long.toHexString(value) + " => " + clazz);
	    return;
	}

    
	public String toString() {
	    return "" + Long.toHexString(mask);
	}
    
    }

}
