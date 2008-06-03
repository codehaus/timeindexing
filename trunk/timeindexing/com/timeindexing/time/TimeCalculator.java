/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// TimeCalculator.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * TODO:  scale times to best granualarity before doing operation.
 * Then convert time back to required scale.
 * TODO: ensure addTimestamp() and subtractTimestamp()
 * do right thing with AbsoluteTimestamp.  Is a cast needed?
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
     * Returns a value in the Scale with the greatest resolution. 
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
		throw new Error("addTimestamp implementation error for 2 RelativeTimestamps. Can't determine Scale.");
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
		throw new Error("addTimestamp implementation error for 2 Timestamps. Can't determine Scale.");
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
     * Returns a value in the Scale with the greatest resolution. 
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
		throw new Error("subtractTimestamp implementation error for 2 RelativeTimestamps. Can't determine Scale.");
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
		throw new Error("subtractTimestamp implementation error for 2 Timestamps. Can't determine Scale.");
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
     * Changes.  Scale or Type.
     */

    /**
     * Convert an AbsoluteTimestamp to a RelativeTimestamp.
     * Converts number of seconds and number of nanoseconds,
     * without any processing.
     * The AbsoluteTimestamp Epoch maps to RelativeTimestamp 0.
     */
    public static RelativeTimestamp toRelative(AbsoluteTimestamp timestamp) {
	RelativeTimestamp relative = asRelativeTimestamp(timestamp.getSeconds(), timestamp.getNanoSeconds());

	return relative;
    }

    /**
     * Convert an RelativeTimestamp to an AbsoluteTimestamp.
     * Converts number of seconds and number of nanoseconds,
     * without any processing.
     * The  RelativeTimestamp 0 maps to AbsoluteTimestamp Epoch.
     */
    public static AbsoluteTimestamp toAbsolute(RelativeTimestamp timestamp) {
	AbsoluteTimestamp absolute = asAbsoluteTimestamp(timestamp.getSeconds(), timestamp.getNanoSeconds());

	return absolute;
    }

    /**
     * Take some seconds and some nanoseconds, and return
     * a AbsoluteTimestamp in the best scale.
     */
    public static AbsoluteTimestamp asAbsoluteTimestamp(long seconds, int nanos) {
	int  nanoseconds = Math.abs(nanos);

	if (nanoseconds % 1000000000 == 0) {
	    return new SecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds % 1000000 == 0) {
	    return new MillisecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds % 1000 == 0) {
	    return new MicrosecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds < 1000000000) {
	    return new NanosecondTimestamp(seconds, nanoseconds);
	} else {
	    return new NanosecondTimestamp(seconds, nanoseconds);
	}
    }

    /**
     * Take some seconds and some nanoseconds, and return
     * a RelativeTimestamp in the best scale.
     */
    public static RelativeTimestamp asRelativeTimestamp(long seconds, int nanos) {
	int nanoseconds = Math.abs(nanos);

	if (nanoseconds % 1000000000 == 0) {
	    return new ElapsedSecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds % 1000000 == 0) {
	    return new ElapsedMillisecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds % 1000 == 0) {
	    return new ElapsedMicrosecondTimestamp(seconds, nanoseconds);
	} else 	if (nanoseconds < 1000000000) {
	    return new ElapsedNanosecondTimestamp(seconds, nanoseconds);
	} else {
	    return new ElapsedNanosecondTimestamp(seconds, nanoseconds);
	}
    }

    /**
     * Convert a Timestamp to a specific Scale.
     */
    public static Timestamp asScale(Timestamp t, Scale scale) {
	if (scale instanceof SecondScale) {
	    return toSeconds(t);
	} else 	if (scale instanceof MillisecondScale) {
	    return toMillis(t);
	} else if (scale instanceof MicrosecondScale) {
	    return toMicros(t);
	} else if (scale instanceof NanosecondScale) {
	    return toNanos(t);
	} else {
	    throw new Error("Unhandled type of scale for argument to asScale(). It is: " +
			    scale.getClass().getName());
	}
    }

    /**
     * Convert a Timestamp to SecondScale Timestamp.
     * May lose some data.
     */
    public static  Timestamp toSeconds(Timestamp ts) {
	if (ts instanceof AbsoluteTimestamp) {
	    return toSeconds((AbsoluteTimestamp)ts);
	} else if (ts instanceof RelativeTimestamp) {
	    return toSeconds((RelativeTimestamp)ts);
	}  else {
	    throw new Error("Unhandled type for argument to toSeconds(). It is: " +
				ts.getClass().getName());
	}
    }

    /**
     * Convert AbsoluteTimestamp to SecondTimestamp.
     * May lose some data.
     */
    public static  SecondTimestamp toSeconds(AbsoluteTimestamp ts) {
	return new SecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert RelativeTimestamp to ElapsedSecondTimestamp.
     * May lose some data.
    */
    public static  ElapsedSecondTimestamp toSeconds(RelativeTimestamp ts) {
	return new ElapsedSecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }

    /**
     * Convert a Timestamp to MillisecondScale Timestamp.
     * May lose some data.
     */
    public static Timestamp toMillis(Timestamp ts) {
	if (ts instanceof AbsoluteTimestamp) {
	    return toMillis((AbsoluteTimestamp)ts);
	} else if (ts instanceof RelativeTimestamp) {
	    return toMillis((RelativeTimestamp)ts);
	}  else {
	    throw new Error("Unhandled type for argument to toMillis(). It is: " +
				ts.getClass().getName());
	}
    }


    /**
     * Convert AbsoluteTimestamp to MillisecondScale.
     * May lose some data.
     */
    public static  MillisecondTimestamp toMillis(AbsoluteTimestamp ts) {
	return new MillisecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert RelativeTimestamp to ElapsedMillisecondTimestamp.
     * May lose some data.
    */
    public static  ElapsedMillisecondTimestamp toMillis(RelativeTimestamp ts) {
	return new ElapsedMillisecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert a Timestamp to MicrosecondScale Timestamp.
     * May lose some data.
     */
    public static Timestamp toMicros(Timestamp ts) {
	if (ts instanceof AbsoluteTimestamp) {
	    return toMicros((AbsoluteTimestamp)ts);
	} else if (ts instanceof RelativeTimestamp) {
	    return toMicros((RelativeTimestamp)ts);
	}  else {
	    throw new Error("Unhandled type for argument to toMicros(). It is: " +
				ts.getClass().getName());
	}
    }


    /**
     * Convert AbsoluteTimestamp to MicrosecondScale.
     * May lose some data.
     */
    public static  MicrosecondTimestamp toMicros(AbsoluteTimestamp ts) {
	return new MicrosecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert RelativeTimestamp to ElapsedMicrosecondTimestamp.
     * May lose some data.
    */
    public static  ElapsedMicrosecondTimestamp toMicros(RelativeTimestamp ts) {
	return new ElapsedMicrosecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert a Timestamp to NanosecondScale Timestamp.
     * May lose some data.
     */
    public static  Timestamp toNanos(Timestamp ts) {
	if (ts instanceof AbsoluteTimestamp) {
	    return toNanos((AbsoluteTimestamp)ts);
	} else if (ts instanceof RelativeTimestamp) {
	    return toNanos((RelativeTimestamp)ts);
	}  else {
	    throw new Error("Unhandled type for argument to toNanos(). It is: " +
				ts.getClass().getName());
	}
    }


    /**
     * Convert AbsoluteTimestamp to NanosecondScale.
     * May lose some data.
     */
    public static  NanosecondTimestamp toNanos(AbsoluteTimestamp ts) {
	return new NanosecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }


    /**
     * Convert RelativeTimestamp to ElapsedNanosecondTimestamp.
     * May lose some data.
    */
    public static  ElapsedNanosecondTimestamp toNanos(RelativeTimestamp ts) {
	return new ElapsedNanosecondTimestamp(ts.getSeconds(), ts.getNanoSeconds());
    }

    /**
     * Convert a Timestamp to a java.util.Date.
     */
    public static Date toDate(Timestamp ts) {
	long milliseconds = (ts.getSeconds() * 1000) + (ts.getNanoSeconds() / 1000000);

	return new Date(milliseconds);
    }


    /**
     * Convert a java.util.Date to a Timestamp.
     */
    public static AbsoluteTimestamp fromDate(Date date) {
	return new MillisecondTimestamp(date);
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
