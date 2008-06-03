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
// AbstractDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.DecimalFormat;

/**
 * This abstact class has the base methods for objects that 
 * format timestamps as absolute dates.
 */
public abstract class AbstractDateFormat implements TimestampFormatting {
    /*
     * A format for whole seconds
     */
    protected final static DateFormatter defaultFormatter = new DateFormatter("yyyy/MM/dd HH:mm:ss");

    /*
     * The DateFormatter
     */
    DateFormatter formatter = null;

    /**
     * The default constructor.
     */
    public AbstractDateFormat() {
	formatter = defaultFormatter;
    }
	
    /**
     * A constructor which takes a new DateFormatter.
     */
    public AbstractDateFormat(DateFormatter aFormatter) {
	formatter = aFormatter;
    }

    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t) {
	return format((AbsoluteTimestamp)t);
    }

    /**
     * Format a date.
     * This calls on the DateFormatter to do some formatting.
     */
    protected final String format(Date date) {
	return formatter.format(date);
    }

    /**
     * Format a AbsoluteTimestamp.
     */
    public abstract String format(AbsoluteTimestamp t);
}
