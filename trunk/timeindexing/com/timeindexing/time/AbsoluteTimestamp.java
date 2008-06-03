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
// AbsoluteTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Absolute;

/**
 * An absolute timestamp.
 * For all Timestamps that hold absolute values w.r.t. the Epoch.
 */
public interface AbsoluteTimestamp extends Absolute, Timestamp {
    /**
     * Get the Epoch for the Timestamp.
     */
    public Epoch getEpoch();

    /**
     * Set the Epoch for the Timestamp.
     * @param epoch The Epoch to use
     */
    public Timestamp setEpoch(Epoch epoch);

    /**
     * Is the time after the Epoch.
     */
    public boolean isAfterEpoch();

    /**
     * Is the time before the Epoch.
     */
    public boolean isBeforeEpoch();


}
