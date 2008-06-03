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
// MidPointIntervalSpecifier.java

package com.timeindexing.time;

import com.timeindexing.basic.Interval;
import com.timeindexing.basic.MidPointInterval;

/**
 * An IntervalSpecifier that instantiates to a MidPointInterval.
 */
public class IntervalTemplate extends AbstractIntervalSpecifier implements IntervalSpecifier {
    /**
     * A IntervalTemplate for this year.
     */
    public static final IntervalTemplate THIS_YEAR = new IntervalTemplate(new StartOfYear(), new EndOfYear());

    /**
     * A IntervalTemplate for this month.
     */
    public static final IntervalTemplate THIS_MONTH = new IntervalTemplate(new StartOfMonth(), new EndOfMonth());

    /**
     * A IntervalTemplate for this week.
     */
    public static final IntervalTemplate THIS_WEEK = new IntervalTemplate(new StartOfWeek(), new EndOfWeek());

    /**
     * A IntervalTemplate for today.
     */
    public static final IntervalTemplate THIS_DAY = new IntervalTemplate(new StartOfDay(), new EndOfDay());

    /**
     * A IntervalTemplate for this hour
     */
    public static final IntervalTemplate THIS_HOUR = new IntervalTemplate(new StartOfHour(), new EndOfHour());

    /**
     * A IntervalTemplate for this minute
     */
    public static final IntervalTemplate THIS_MINUTE = new IntervalTemplate(new StartOfMinute(), new EndOfMinute());

    /**
     * A IntervalTemplate for this second
     */
    public static final IntervalTemplate THIS_SECOND = new IntervalTemplate(new StartOfSecond(), new EndOfSecond());

    /**
     * Construct a IntervalTemplate.
     */
    public IntervalTemplate(TimeSpecifier start, TimeSpecifier end) {
	setStartSpecifier(start);
	setEndSpecifier(end);
    }

    /**
     * Instantiate the IntervalTemplate w.r.t. a Timestamp.
     */
    public Interval instantiate(AbsoluteTimestamp t) {
	Timestamp startTimestamp = getStartSpecifier().instantiate(t);
	Timestamp endTimestamp = getEndSpecifier().instantiate(t);

	Interval returnInteval = null;

	returnInteval = new MidPointInterval(t, (AbsoluteTimestamp)startTimestamp, (AbsoluteTimestamp)endTimestamp);

	return returnInteval;
    }

        /**
     * To String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append("FROM ");
	buffer.append(getStartSpecifier().toString());
	buffer.append("TO ");
	buffer.append(getEndSpecifier().toString());
	return buffer.toString();
    }

}
