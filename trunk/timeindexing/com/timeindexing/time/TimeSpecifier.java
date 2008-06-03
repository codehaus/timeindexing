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
// TimeSpecifier.java

package com.timeindexing.time;

import com.timeindexing.basic.Value;

/**
 * An interface for objects that specify some specifier for a Timestamp.
 */
public interface  TimeSpecifier extends Cloneable, Value {
    /**
     * Do this TimeSpecifier, then do another TimeSpecifier.
     * This gets more modification.
     * N.B.  spec1.then(spec2) == spec2.afterDoing(spec1)
     *       spec1.then(spec2).then(spec3) == spec3.afterDoing(spec2.afterDoing(spec1))
     */
    public TimeSpecifier then(TimeSpecifier specifier);

    /**
     * After doing the specfied TimeSpecifier, do this TimeSpecifier.
     * This gets more modification.
     * N.B.  spec1.then(spec2) == spec2.afterDoing(spec1)
     *       spec1.then(spec2).then(spec3) == spec3.afterDoing(spec2.afterDoing(spec1))
     */
    public TimeSpecifier afterDoing(TimeSpecifier specifier);

    /**
     * Instantiate this TimeSpecifier w.r.t a particular Timestamp.
     * It returns a Timestamp which has been modified by the amount of the TimeSpecifier.
     */
    public Timestamp instantiate(Timestamp t);

    /**
     * Get the value.
     * When TimeDirection is FORWARD, then the value is positive,
     * and when TimeDirection is BACKWARD, then the value is negative.
     */
    public long value();
}
