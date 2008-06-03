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



// ZeroTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.io.Serializable;

/**
 * A timestamp that is only zero
 */
public final class ZeroTimestamp extends MillisecondTimestamp implements Timestamp, Serializable {
    protected ZeroTimestamp() {
	super(0);
    }

    /**
     * Get the toString() version of a ZeroTimestamp.
     */
    public String toString() {
	return "ZERO";
    }
}
