// StopWatch.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A simple stop watch that returns the time that has elapsed
 * since the StopWatch started.
 */
public class StopWatch {
    /*
     * The start time of this StopWatch.
     */
    AbsoluteTimestamp referenceTime = null;

    /*
     * The amount of elapsed time when
     * the StopWatch waslast read.
     */
    RelativeTimestamp elapsed = null;

    /*
     * The scale the StopWatch reports in.
     */
    Scale scale = null;
 
    /*
     * Is the StopWatch stopped or started.
     */
    boolean stopped = true;

    /**
     * Create a StopWatch reporting in MillisecondScale.
     */
    public StopWatch() {
	this(MillisecondScale.SCALE);
    }

    /**
     * Create a StopWatch with a specific Scale
     */
    public StopWatch(Scale theScale) {
	scale = theScale;
	setElapsedTime();
	stopped = true;
    }

    /**
     * Start the StopWatch.
     */
    public StopWatch start() {
	setStartTime();

	stopped = false;

	return this;
    }

    /**
     * Stop the StopWatch.
     */
    public StopWatch stop() {
	stopped = true;
	
	return this;
    }

    /**
     * Read a new elapsed time from the StopWatch.
     */
    public RelativeTimestamp read() {
	// only take a new reading if the StopWatch is not stopped
	if (!stopped) {
	    elapsed = TimeCalculator.elapsedSince(referenceTime);
	}

	return elapsed;
    }

    /**
     * Get the last reading.
     * This does not get a new time.
     */
    public RelativeTimestamp getLastReading() {
	return elapsed;
    }

    
    protected void setStartTime() {
	if (scale instanceof SecondScale) {
	    referenceTime = new SecondTimestamp();
	} else 	if (scale instanceof MillisecondScale) {
	    referenceTime = new MillisecondTimestamp();
	} else if (scale instanceof MicrosecondScale) {
	    referenceTime = new MicrosecondTimestamp();
	} else if (scale instanceof NanosecondScale) {
	    referenceTime = new NanosecondTimestamp();
	} else {
	    throw new Error("Unhandled type of scale for StopWatch. It is: " +
			    scale.getClass().getName());
	}
    }

    protected void setElapsedTime() {
	if (scale instanceof SecondScale) {
	    elapsed = new ElapsedSecondTimestamp();
	} else 	if (scale instanceof MillisecondScale) {
	    elapsed = new ElapsedMillisecondTimestamp();
	} else if (scale instanceof MicrosecondScale) {
	    elapsed = new ElapsedMicrosecondTimestamp();
	} else if (scale instanceof NanosecondScale) {
	    elapsed = new ElapsedNanosecondTimestamp();
	} else {
	    throw new Error("Unhandled type of scale for StopWatch. It is: " +
			    scale.getClass().getName());
	}
    }

}

