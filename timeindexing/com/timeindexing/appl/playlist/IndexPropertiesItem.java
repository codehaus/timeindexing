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



// IndexPropertiesItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.IndexPropertiesProcessor;

/**
 * A PlayListItem which is constructed from an Index
 * some IndexProperties.
 */
public class IndexPropertiesItem extends AbstractPlayListItem {
    IndexProperties properties = null;

    IndexPropertiesProcessor proc = null;

    /**
     * Construct the IndexPropertiesItem
     * @param index the Index 
     * @param properties the IndexProperties
     */
    public IndexPropertiesItem(Index index, IndexProperties properties) {
	this.proc = new IndexPropertiesProcessor();
	this.properties = properties;

	setIndex(index);
	setInterval(proc.createInterval(index, properties));
    }

    /**
     * Create a String view of this PlayListItem.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append(getIndex().getURI().toString());
	buffer.append(" => ");
	buffer.append(proc.prettyPrint(properties));

	return buffer.toString();
    }
}
