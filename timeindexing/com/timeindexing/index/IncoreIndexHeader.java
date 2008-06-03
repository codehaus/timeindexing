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
// IncoreIndexHeader.java


package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;

import java.util.Map;
import java.util.HashMap;

/**
 * The implementation of an index header for use 
 * in Index Caches.
 */
public class IncoreIndexHeader extends DefaultIndexHeader implements ManagedIndexHeader {
    Index myIndex = null;

    /**
     * Create a IncoreIndexHeader object.
     */
    public IncoreIndexHeader(Index index, String name) {
	myIndex = index;
	setName(name);
    }


}
