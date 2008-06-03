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
// IndexAccessEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;
import java.util.EventObject;

/**
 * An OutputEvent, which is generated 
 * when an  output component actually does some output.
 */
public class OutputEvent extends IndexEvent {
    /*
     * The number of bytes output.
     */
    long bytesOutput = 0;

    /**
     * Construct an IndexAccessEvent.
     * Constructed from an index name and the number of bytes
     * that have been output.
     */
    public OutputEvent(String aName, ID anID, long bytesOutput, Object aSource) {
	super(aName, anID, aSource);
	this.bytesOutput = bytesOutput;
    }

    /**
     * Get the number of bytes output.
     */
    public long getBytesOutput() {
	return bytesOutput;
    }
}
