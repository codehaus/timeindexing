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
// SelectionProcessor.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.MidPointInterval;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.Count;
import com.timeindexing.time.Clock;
import com.timeindexing.time.*;


/**
 * This class is used by applications to do processing
 * of values in order to get selection from an Index.
 * <p>
 * The values passed to the SelectionProcessor get passed in 
 * as an IndexProperties object.
 * Types of values in the properties are:
 * <ul>
 * <li> "startpos" -> value accepted by PositionParser
 * <li> "starttime" -> value accepted by TimeDateParser
 * <li> "endpos" ->  value accepted by PositionParser
 * <li> "endtime" -> value accepted by TimeDateParser
 * <li> "count" -> value accepted by CountParser
 * <li> "for" -> value accepted by TimeDateParser
 * </ul>
 */
public class SelectionProcessor {

    /**
     * Construct a SelectionProcessor.
     */
    public SelectionProcessor() {
	;
    }

    /**
     * Process a selction given an Index and some index properties.
     * It will ignore properties not relevant to the selection.
     * It uses a Lifetime enumeration of DISCRETE.
     */
    public IndexView select(IndexView index, IndexProperties properties) {
	return select(index, properties, Lifetime.DISCRETE);
    }

    /**
     * Process a selction given an Index and some index properties.
     * It will ignore properties not relevant to the selection.
     */
    public IndexView select(IndexView index, IndexProperties properties, Lifetime lifetime) {
    	Interval interval = null;
	IndexView selection = null;
	IndexPropertiesProcessor ipp = new IndexPropertiesProcessor();

	/*
	 * Determine interval for selection.
	 */
    	interval = ipp.createInterval(index, properties);

	// select from the interval
	selection = index.select(interval, IndexTimestampSelector.DATA, Overlap.FREE, lifetime);

	System.err.println(Clock.time.time() + " " + index.getURI() + "." + ipp.prettyPrint(properties) + " Items = " + selection.getLength() + ". Thread " + Thread.currentThread().getName() );


	return selection;
    }

}
