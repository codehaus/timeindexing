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



package uk.ti;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;

/**
 * First test of TimeCalculator
 */
public class TestTC {
    public static void main(String [] args) {
	Timestamp t0 = new MillisecondTimestamp();

	System.out.print("t0 = " + t0);
	System.out.print("\t");

	try {
	    Thread.sleep(20);
	} catch (InterruptedException ie) {
	    ;
	}

	Timestamp t1 = new MillisecondTimestamp();

	System.out.print("t1 = " + t1);
	System.out.print("\n");

	System.out.println("t0 == t0? " + TimeCalculator.equals(t0, t0));
	System.out.println("t0 == t1? " + TimeCalculator.equals(t0, t1));
	System.out.println("t1 == t0? " + TimeCalculator.equals(t1, t0));
	System.out.println("t1 == t1? " + TimeCalculator.equals(t1, t1));
	System.out.print("\n");

	System.out.println("t0 != t0? " + TimeCalculator.notEquals(t0, t0));
	System.out.println("t0 != t1? " + TimeCalculator.notEquals(t0, t1));
	System.out.println("t1 != t0? " + TimeCalculator.notEquals(t1, t0));
	System.out.println("t1 != t1? " + TimeCalculator.notEquals(t1, t1));
	System.out.print("\n");

	System.out.println("t0 <= t0? " + TimeCalculator.lessThanEquals(t0, t0));
	System.out.println("t0 <= t1? " + TimeCalculator.lessThanEquals(t0, t1));
	System.out.println("t1 <= t0? " + TimeCalculator.lessThanEquals(t1, t0));
	System.out.println("t1 <= t1? " + TimeCalculator.lessThanEquals(t1, t1));
	System.out.print("\n");

	System.out.println("t0 < t0? " + TimeCalculator.lessThan(t0, t0));
	System.out.println("t0 < t1? " + TimeCalculator.lessThan(t0, t1));
	System.out.println("t1 < t0? " + TimeCalculator.lessThan(t1, t0));
	System.out.println("t1 < t1? " + TimeCalculator.lessThan(t1, t1));
	System.out.print("\n");

	System.out.println("t0 >= t0? " + TimeCalculator.greaterThanEquals(t0, t0));
	System.out.println("t0 >= t1? " + TimeCalculator.greaterThanEquals(t0, t1));
	System.out.println("t1 >= t0? " + TimeCalculator.greaterThanEquals(t1, t0));
	System.out.println("t1 >= t1? " + TimeCalculator.greaterThanEquals(t1, t1));
	System.out.print("\n");

	System.out.println("t0 > t0? " + TimeCalculator.greaterThan(t0, t0));
	System.out.println("t0 > t1? " + TimeCalculator.greaterThan(t0, t1));
	System.out.println("t1 > t0? " + TimeCalculator.greaterThan(t1, t0));
	System.out.println("t1 > t1? " + TimeCalculator.greaterThan(t1, t1));
	System.out.print("\n");

    }
}

