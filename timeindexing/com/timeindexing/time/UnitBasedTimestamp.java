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



// UnitBasedTimestamp.java

package com.timeindexing.time;

/**
 * A unit based timestamp.
 * For all Timestamps that are based on elpased units
 * rather than elapsed time.
 * We need to know the no. of units per second.
 */
public interface UnitBasedTimestamp extends Timestamp {
     /**
     * Get the no of units that have elapsed.
     */
    public long getUnits();

    /**
     * Get the no of units per second, for this kind of timestamp.
     */
    public long getUnitsPerSecond();
}
