// TimeCalculator.java

package com.timeindexing.time;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * TODO:  scale times to best granualarity before doing operation.
 * The nconvert time back to required scale.
 */

/**
 * A date and time calculator.
 */
public class TimeCalculator {
    public TimeCalculator() {
	;
    }

    /*
     * Adding.
     */


    /**
     * Add one timestamp to another.
     */
    public static Timestamp addTimestamp(Timestamp t0, Timestamp t1) {
	if (t0 instanceof RelativeTimestamp && t1 instanceof RelativeTimestamp) {
	    RelativeTimestamp rt0 = (RelativeTimestamp)t0;
	    RelativeTimestamp rt1 = (RelativeTimestamp)t1;

	    if (t0 instanceof NanosecondScale || t1 instanceof NanosecondScale) {
		return addTimestampN(rt0, rt1);
	    } else if (t0 instanceof MicrosecondScale || t1 instanceof MicrosecondScale) {
		return addTimestampU(rt0, rt1);
	    } else if (t0 instanceof MillisecondScale || t1 instanceof MillisecondScale) {
		return addTimestampM(rt0, rt1);
	    } else if (t0 instanceof SecondScale && t1 instanceof SecondScale) {
		return addTimestampS(rt0, rt1);
	    } else {
		throw new Error("addTimestamp implementation error for 2 RelativeTimestamps.");
	    }
	} else {

	    if (t0 instanceof NanosecondScale || t1 instanceof NanosecondScale) {
		return addTimestampN(t0, t1);
	    } else if (t0 instanceof MicrosecondScale || t1 instanceof MicrosecondScale) {
		return addTimestampU(t0, t1);
	    } else if (t0 instanceof MillisecondScale || t1 instanceof MillisecondScale) {
		return addTimestampM(t0, t1);
	    } else if (t0 instanceof SecondScale && t1 instanceof SecondScale) {
		return addTimestampS(t0, t1);
	    } else {
		throw new Error("addTimestamp implementation error for 2 Timestamps.");
	    }

	}
    }


    /*
     * Do the actual adding.
     */

    /**
     * Add one second timestamp to another.
     */
    public static SecondTimestamp addTimestampS(Timestamp t0, Timestamp t1) {
	return new SecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one elapsed timestamp to another.
     */
    public static ElapsedSecondTimestamp addTimestampS(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedSecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one millisecond timestamp to another.
     */
    public static MillisecondTimestamp addTimestampM(Timestamp t0, Timestamp t1) {
	return new MillisecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one elapsed timestamp to another elapsed timestamp.
     */
    public static ElapsedMillisecondTimestamp addTimestampM(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedMillisecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one microsecond timestamp to another.
     */
    public static MicrosecondTimestamp addTimestampU(Timestamp t0, Timestamp t1) {
	return new MicrosecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }


    /**
     * Add one elapsed timestamp to another.
     */
    public static ElapsedMicrosecondTimestamp addTimestampU(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedMicrosecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one nanosecond timestamp to another.
     */
    public static NanosecondTimestamp addTimestampN(Timestamp t0, Timestamp t1) {
	return new NanosecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }

    /**
     * Add one elapsed timestamp to another.
     */
    public static ElapsedNanosecondTimestamp addTimestampN(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedNanosecondTimestamp(t0.getSeconds() + t1.getSeconds(),
					t0.getNanoSeconds() + t1.getNanoSeconds());
    }


    /*
     * Subtraction.
     */


    /**
     * Substract one  timestamp from another.
     */
    public static Timestamp subtractTimestamp(Timestamp t0, Timestamp t1) {
	if (t0 instanceof RelativeTimestamp && t1 instanceof AbsoluteTimestamp) {
	    throw new Error("subtractTimestamp Illegal to subtract an AbsoluteTimestamp from a RelativeTimestamp");
	} else if (t0 instanceof RelativeTimestamp && t1 instanceof RelativeTimestamp) {
	    RelativeTimestamp rt0 = (RelativeTimestamp)t0;
	    RelativeTimestamp rt1 = (RelativeTimestamp)t1;

	    if (t0 instanceof NanosecondScale || t1 instanceof NanosecondScale) {
		return subtractTimestampN(rt0, rt1);
	    } else if (t0 instanceof MicrosecondScale || t1 instanceof MicrosecondScale) {
		return subtractTimestampU(rt0, rt1);
	    } else if (t0 instanceof MillisecondScale || t1 instanceof MillisecondScale) {
		return subtractTimestampM(rt0, rt1);
	    } else if (t0 instanceof SecondScale && t1 instanceof SecondScale) {
		return subtractTimestampS(rt0, rt1);
	    } else {
		throw new Error("subtractTimestamp implementation error for 2 RelativeTimestamps.");
	    }
	} else {

	    if (t0 instanceof NanosecondScale || t1 instanceof NanosecondScale) {
		return subtractTimestampN(t0, t1);
	    } else if (t0 instanceof MicrosecondScale || t1 instanceof MicrosecondScale) {
		return subtractTimestampU(t0, t1);
	    } else if (t0 instanceof MillisecondScale || t1 instanceof MillisecondScale) {
		return subtractTimestampM(t0, t1);
	    } else if (t0 instanceof SecondScale && t1 instanceof SecondScale) {
		return subtractTimestampS(t0, t1);
	    } else {
		throw new Error("subtractTimestamp implementation error for 2 Timestamps.");
	    }

	}
    }


    /*
     * Do the actual subtraction.
     */

    /**
     * Subtract one second timestamp from another.
     */
    public static SecondTimestamp subtractTimestampS(Timestamp t0, Timestamp t1) {
	return new SecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one elapsed timestamp from another.
     */
    public static ElapsedSecondTimestamp subtractTimestampS(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedSecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one millisecond timestamp from another.
     */
    public static MillisecondTimestamp subtractTimestampM(Timestamp t0, Timestamp t1) {
	return new MillisecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one elapsed timestamp from another elapsed timestamp.
     */
    public static ElapsedMillisecondTimestamp subtractTimestampM(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedMillisecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one microsecond timestamp from another.
     */
    public static MicrosecondTimestamp subtractTimestampU(Timestamp t0, Timestamp t1) {
	return new MicrosecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }


    /**
     * Subtract one elapsed timestamp from another.
     */
    public static ElapsedMicrosecondTimestamp subtractTimestampU(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedMicrosecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one nanosecond timestamp from another.
     */
    public static NanosecondTimestamp subtractTimestampN(Timestamp t0, Timestamp t1) {
	return new NanosecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }

    /**
     * Subtract one elapsed timestamp from another.
     */
    public static ElapsedNanosecondTimestamp subtractTimestampN(RelativeTimestamp t0, RelativeTimestamp t1) {
	return new ElapsedNanosecondTimestamp(t0.getSeconds() - t1.getSeconds(),
					t0.getNanoSeconds() - t1.getNanoSeconds());
    }


    /*
     * Elapsed since methods.
     */

    /**
     * Time that has elasped since the specified time.
     * The reuslt is in the same Timestamp Scale as the argument. 
     */
    public static RelativeTimestamp elapsedSince(Timestamp since) {
	if (since instanceof AbsoluteTimestamp) {
	    if (since instanceof SecondTimestamp) {
		return elapsedAsSecondsSince((AbsoluteTimestamp)since);
	    } else if (since instanceof MillisecondTimestamp) {
		return elapsedAsMillisSince((AbsoluteTimestamp)since);
	    } else if (since instanceof MicrosecondTimestamp) {
		return elapsedAsMicrosSince((AbsoluteTimestamp)since);
	    } else if (since instanceof NanosecondTimestamp) {
		return elapsedAsNanosSince((AbsoluteTimestamp)since);
	    } else {
		throw new Error("Unhandled type for argument to elapsedSince(). It is: " +
				since.getClass().getName());
	    }
	} else {
	    throw new Error("Can't determine the elapsed time since a Relative Timestamp");
	}
    }

    /**
     * Seconds that have elasped since the specific time.
     */
    public static ElapsedSecondTimestamp elapsedAsSecondsSince(AbsoluteTimestamp since) {
	Timestamp diff = subtractTimestamp(Clock.time.asNanos(), since);
	return new ElapsedSecondTimestamp(diff.getSeconds(), diff.getNanoSeconds());
    }


    /**
     * Milliseconds that have elasped since the specific time.
     */
    public static ElapsedMillisecondTimestamp elapsedAsMillisSince(AbsoluteTimestamp since) {
	Timestamp diff = subtractTimestamp(Clock.time.asNanos(), since);
	return new ElapsedMillisecondTimestamp(diff.getSeconds(), diff.getNanoSeconds());
    }

    /**
     * Microseconds that have elasped since the specific time.
     */
    public static ElapsedMicrosecondTimestamp elapsedAsMicrosSince(AbsoluteTimestamp since) {
	Timestamp diff = subtractTimestamp(Clock.time.asNanos(), since);
	return new ElapsedMicrosecondTimestamp(diff.getSeconds(), diff.getNanoSeconds());
    }

    /**
     * Nanoseconds that have elasped since the specific time.
     */
    public static ElapsedNanosecondTimestamp elapsedAsNanosSince(AbsoluteTimestamp since) {
	Timestamp diff = subtractTimestamp(Clock.time.asNanos(), since);
	return new ElapsedNanosecondTimestamp(diff.getSeconds(), diff.getNanoSeconds());
    }



    /*
     * Change scale.
     */

    /**
     * Convert MillisecondTimestamp to SecondTimestamp.
     * Loses some data.
    */
    public static  SecondTimestamp toSeconds(MillisecondTimestamp mTS) {
	return new SecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert MicrosecondTimestamp to SecondTimestamp.
     * Loses some data.
     */
    public static SecondTimestamp toSeconds(MicrosecondTimestamp uTS) {
	return new SecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert NanosecondTimestamp to MicrosecondTimestamp.
     * Loses some data.
     */
    public static SecondTimestamp toSeconds(NanosecondTimestamp nTS) {
	return new SecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert SecondTimestamp to MicrosecondTimestamp.
     * A NO OP.
     */
    public static SecondTimestamp toSeconds(SecondTimestamp sTS) {
	return sTS;
    }

    /**
     * Convert MillisecondTimestamp to MillisecondTimestamp.
     * A NO OP.
     */
    public static  MillisecondTimestamp toMillis(MillisecondTimestamp mTS) {
	return mTS;
    }

    /**
     * Convert MicrosecondTimestamp to MillisecondTimestamp.
     * Loses some data.
     */
    public static MillisecondTimestamp toMillis(MicrosecondTimestamp uTS) {
	return new MillisecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert NanosecondTimestamp to MillisecondTimestamp.
     * Loses some data.
     */
    public static MillisecondTimestamp toMillis(NanosecondTimestamp nTS) {
	return new MillisecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert SecondTimestamp to MillisecondTimestamp.
     */
    public static MillisecondTimestamp toMillis(SecondTimestamp sTS) {
	return new MillisecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }

    /**
     * Convert MillisecondTimestamp to MicrosecondTimestamp.
    */
    public static  MicrosecondTimestamp toMicros(MillisecondTimestamp mTS) {
	return new MicrosecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert MicrosecondTimestamp to MicrosecondTimestamp.
     * A NO OP.
     */
    public static MicrosecondTimestamp toMicros(MicrosecondTimestamp uTS) {
	return uTS;
    }

    /**
     * Convert NanosecondTimestamp to MicrosecondTimestamp.
     * Loses some data.
     */
    public static MicrosecondTimestamp toMicros(NanosecondTimestamp nTS) {
	return new MicrosecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert SecondTimestamp to MicrosecondTimestamp.
     */
    public static MicrosecondTimestamp toMicros(SecondTimestamp sTS) {
	return new MicrosecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }

    /**
     * Convert MilliecondTimestamp to NanosecondTimestamp.
     */
    public static  NanosecondTimestamp toNanos(MillisecondTimestamp mTS) {
	return new NanosecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert MicrosecondTimestamp to NanosecondTimestamp.
     */
    public static NanosecondTimestamp toNanos(MicrosecondTimestamp uTS) {
	return new NanosecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert NanosecondTimestamp to NanosecondTimestamp.
     * A NO OP.
     */
    public static NanosecondTimestamp toNanos(NanosecondTimestamp nTS) {
	return nTS;
    }

    /**
     * Convert SecondTimestamp to NanosecondTimestamp.
     */
    public static NanosecondTimestamp toNanos(SecondTimestamp sTS) {
	return new NanosecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }


    /**
     * Convert ElapsedMillisecondTimestamp to ElapsedSecondTimestamp.
     * Loses some data.
    */
    public static  ElapsedSecondTimestamp toSeconds(ElapsedMillisecondTimestamp mTS) {
	return new ElapsedSecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedMicrosecondTimestamp to ElapsedSecondTimestamp.
     * Loses some data.
     */
    public static ElapsedSecondTimestamp toSeconds(ElapsedMicrosecondTimestamp uTS) {
	return new ElapsedSecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedNanosecondTimestamp to ElapsedMicrosecondTimestamp.
     * Loses some data.
     */
    public static ElapsedSecondTimestamp toSeconds(ElapsedNanosecondTimestamp nTS) {
	return new ElapsedSecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedSecondTimestamp to ElapsedMicrosecondTimestamp.
     * A NO OP.
     */
    public static ElapsedSecondTimestamp toSeconds(ElapsedSecondTimestamp sTS) {
	return sTS;
    }

    /**
     * Convert ElapsedMillisecondTimestamp to ElapsedMillisecondTimestamp.
     * A NO OP.
     */
    public static  ElapsedMillisecondTimestamp toMillis(ElapsedMillisecondTimestamp mTS) {
	return mTS;
    }

    /**
     * Convert ElapsedMicrosecondTimestamp to ElapsedMillisecondTimestamp.
     * Loses some data.
     */
    public static ElapsedMillisecondTimestamp toMillis(ElapsedMicrosecondTimestamp uTS) {
	return new ElapsedMillisecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedNanosecondTimestamp to ElapsedMillisecondTimestamp.
     * Loses some data.
     */
    public static ElapsedMillisecondTimestamp toMillis(ElapsedNanosecondTimestamp nTS) {
	return new ElapsedMillisecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedSecondTimestamp to ElapsedMillisecondTimestamp.
     */
    public static ElapsedMillisecondTimestamp toMillis(ElapsedSecondTimestamp sTS) {
	return new ElapsedMillisecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedMillisecondTimestamp to ElapsedMicrosecondTimestamp.
    */
    public static  ElapsedMicrosecondTimestamp toMicros(ElapsedMillisecondTimestamp mTS) {
	return new ElapsedMicrosecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedMicrosecondTimestamp to ElapsedMicrosecondTimestamp.
     * A NO OP.
     */
    public static ElapsedMicrosecondTimestamp toMicros(ElapsedMicrosecondTimestamp uTS) {
	return uTS;
    }

    /**
     * Convert ElapsedNanosecondTimestamp to ElapsedMicrosecondTimestamp.
     * Loses some data.
     */
    public static ElapsedMicrosecondTimestamp toMicros(ElapsedNanosecondTimestamp nTS) {
	return new ElapsedMicrosecondTimestamp(nTS.getSeconds(), nTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedSecondTimestamp to ElapsedMicrosecondTimestamp.
     */
    public static ElapsedMicrosecondTimestamp toMicros(ElapsedSecondTimestamp sTS) {
	return new ElapsedMicrosecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedMilliecondTimestamp to ElapsedNanosecondTimestamp.
     */
    public static  ElapsedNanosecondTimestamp toNanos(ElapsedMillisecondTimestamp mTS) {
	return new ElapsedNanosecondTimestamp(mTS.getSeconds(), mTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedMicrosecondTimestamp to ElapsedNanosecondTimestamp.
     */
    public static ElapsedNanosecondTimestamp toNanos(ElapsedMicrosecondTimestamp uTS) {
	return new ElapsedNanosecondTimestamp(uTS.getSeconds(), uTS.getNanoSeconds());
    }

    /**
     * Convert ElapsedNanosecondTimestamp to ElapsedNanosecondTimestamp.
     * A NO OP.
     */
    public static ElapsedNanosecondTimestamp toNanos(ElapsedNanosecondTimestamp nTS) {
	return nTS;
    }

    /**
     * Convert ElapsedSecondTimestamp to ElapsedNanosecondTimestamp.
     */
    public static ElapsedNanosecondTimestamp toNanos(ElapsedSecondTimestamp sTS) {
	return new ElapsedNanosecondTimestamp(sTS.getSeconds(), sTS.getNanoSeconds());
    }



    /*
     * Ordinals: ==, != , <, <=, >, >=
     */

    /**
     * Equals
     */
    public static boolean equals(Timestamp t0, Timestamp t1) {
	if (t0.getSeconds() == t1.getSeconds() &&
	    t0.getNanoSeconds() == t1.getNanoSeconds()) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Not Equals
     */
    public static boolean notEquals(Timestamp t0, Timestamp t1) {
	return ! equals(t0, t1);
    }

    /**
     * LessThanEquals
     */
    public static boolean lessThanEquals(Timestamp t0, Timestamp t1) {
	if (t0.getSeconds() < t1.getSeconds()) {
	    return true;
	} else if (t0.getSeconds() == t1.getSeconds()) {
	    if (t0.getNanoSeconds() <= t1.getNanoSeconds()) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }


    /**
     * Less Than
     */
    public static boolean lessThan(Timestamp t0, Timestamp t1) {
	return ! greaterThanEquals(t0, t1);
    }



    /**
     * GreaterThanEquals
     */
    public static boolean greaterThanEquals(Timestamp t0, Timestamp t1) {
	if (t0.getSeconds() > t1.getSeconds()) {
	    return true;
	} else if (t0.getSeconds() == t1.getSeconds()) {
	    if (t0.getNanoSeconds() >= t1.getNanoSeconds()) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    /**
     * Greater Than
     */
    public static boolean greaterThan(Timestamp t0, Timestamp t1) {
	return ! lessThanEquals(t0, t1);
    }


}
