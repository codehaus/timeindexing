Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// Month.java

package com.timeindexing.time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A TimeSpecifier for months.
 */
public class Month extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a Month TimeSpecifier.
     */
    public Month(long count) {
	setHowMany(count);
	setDirection(TimeDirection.FORWARD);
    }

    /**
     * Construct a Month TimeSpecifier.
     */
    public Month(long count, TimeDirection direction) {
	setHowMany(count);
	setDirection(direction);
    }

    /**
     * Construct a Month TimeSpecifier.
     */
    public Month(long count, TimeDirection direction, TimeSpecifier modifier) {
	setHowMany(count);
	setDirection(direction);
	afterDoing(modifier);
    }

    /**
     * Instantiate this TimeSpecifier w.r.t a particular Timestamp.
     * It returns a Timestamp which has been modified by the amount of the TimeSpecifier.
     */
    public Timestamp instantiate(Timestamp t) {
	Timestamp timestampToUse = null;

	// determine what timestamp to use
	if (getPredecessor() == null) {
	    // there is no sub modifier so we use t directly
	    timestampToUse = t;
	} else {
	    // there is a sub modifier, so we use the Timestamp 
	    // returned by instantiating the getPredecessor() with t
	    timestampToUse = getPredecessor().instantiate(t);
	}

	Timestamp returnTimestamp = null;

	// allocate a calendar, because it has month processing
	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
	// set the calendar to the time in timestampToUse
	calendar.setTimeInMillis( timestampToUse.getSeconds() * 1000);


	if (modificationDirection == TimeDirection.FORWARD) {
	    // add howMany months
	    calendar.add(Calendar.MONTH, (int)(howMany));

	    long millis = calendar.getTimeInMillis();
	    long seconds = millis / 1000;


	    Timestamp subSecondPart = TimeCalculator.subtractTimestamp(timestampToUse, new SecondTimestamp(timestampToUse.getSeconds()));

	    returnTimestamp = TimeCalculator.addTimestamp(new SecondTimestamp(seconds), subSecondPart);
	} else {
	    // subtract howMany months
	    calendar.add(Calendar.MONTH, (int)(-howMany));

	    long millis = calendar.getTimeInMillis();
	    long seconds = millis / 1000;

	    Timestamp subSecondPart = TimeCalculator.subtractTimestamp(timestampToUse, new SecondTimestamp(timestampToUse.getSeconds()));

	    returnTimestamp = TimeCalculator.addTimestamp(new SecondTimestamp(seconds), subSecondPart);
	}

	return returnTimestamp;
    }

    /**
     * To String
     */
    /**
     * To String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	if (hasPredecessor()) {
	    buffer.append(getPredecessor().toString());
	    buffer.append(" then ");
	}

	buffer.append(howMany);
	buffer.append(" month ");
	buffer.append(modificationDirection.toString());

	return buffer.toString();
    }

}

