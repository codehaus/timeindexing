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
// MicrosecondElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a MicrosecondElapsedFormat.
 * e.g. <tt>00:00:01.593469</tt>
 */
public class MicrosecondElapsedFormat extends AbstractElapsedFormat {
    protected final long secsPerDay = 24 * 60 * 60;

    /*
     * A format for microseconds.  6 obligatory digits.
     */
    private static NumberFormat microsformat = new DecimalFormat("000000");

    /**
     * Construct a MicrosecondElapsedFormat object.
     */
    public MicrosecondElapsedFormat() {
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public String secondsFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	int nanoseconds = Math.abs(t.getNanoSeconds());
	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(seconds);
	buffer.append(".");
	buffer.append(microsformat.format(nanoseconds/1000));

	return buffer.toString();
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public String hourFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	int nanoseconds = Math.abs(t.getNanoSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(firstHourformat.format((new Date(milliseconds))));
	buffer.append(".");
	buffer.append(microsformat.format(microsOnly));

	return buffer.toString();
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public String dayFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	int nanoseconds = Math.abs(t.getNanoSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(firstDayformat.format((new Date(milliseconds))));
	buffer.append(".");
	buffer.append(microsformat.format(microsOnly));

	return buffer.toString();
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public String yearFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	int nanoseconds = Math.abs(t.getNanoSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	long days = seconds / secsPerDay;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(daysformat.format(days));
	buffer.append(" ");
	buffer.append(firstYearformat.format((new Date(milliseconds))));
	buffer.append(".");
	buffer.append(microsformat.format(microsOnly));

	return buffer.toString();
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public String fullFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	int nanoseconds = Math.abs(t.getNanoSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(yearsformat.format(years));
	buffer.append(" ");
	buffer.append(daysformat.format(days));
	buffer.append(" ");
	buffer.append(catchAllformat.format((new Date(milliseconds))));
	buffer.append(".");
	buffer.append(microsformat.format(microsOnly));

	return buffer.toString();
    }

}
