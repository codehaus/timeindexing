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
// Now.java

package com.timeindexing.time;

/**
 * A TimeSpecifier for now.
 */
public class Now extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * A static singleton instance.  
     * Can be used in context Now.Now
     */
    public final static Now Now = new Now();

    /**
     * Construct a TimeSpecifier for Now.
     */
    public Now() {
	setHowMany(1);
	setDirection(TimeDirection.NOTHING);
    }

    /**
     * Construct a TimeSpecifier for Now.
     */
    public Now(TimeSpecifier modifier) {
	setHowMany(1);
	setDirection(TimeDirection.NOTHING);
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

	// Now does no processing.
	// It's the Identity function.
	return timestampToUse;
    }

    /**
     * To String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	if (hasPredecessor()) {
	    buffer.append(getPredecessor().toString());
	    buffer.append(" then ");
	}

	buffer.append(" now ");

	return buffer.toString();
    }
}

