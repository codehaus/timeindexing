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
// MillisecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a string using the MillisecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58.880</tt>
 */
public class MillisecondDateFormat extends AbstractDateFormat {
    /*
     * A format for milliseconds.  3 obligatory digits.
     */
    private static NumberFormat millisformat = 	millisformat =  new DecimalFormat("000");


   /**
     * Construct a MillisecondDateFormat object.
     */
    public MillisecondDateFormat() {
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(AbsoluteTimestamp t) {
	long seconds = t.getSeconds();
	int nanoseconds = t.getNanoSeconds();

	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long millisOnly = nanoseconds  / 1000000;

	return formatter.format((new Date(milliseconds))) + "." + millisformat.format(millisOnly);
    }
}
