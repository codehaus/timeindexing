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

import java.util.Properties;
import com.timeindexing.index.*;
import com.timeindexing.data.*;
import com.timeindexing.time.*;

/**
 * Utility support for creating logs using timeindexes.
 */
public class TimeIndexLog {
    // a shared TimeIndexFactory for this class
    static  TimeIndexFactory factory = new TimeIndexFactory();


    /**
     * Create a log file, given a path and a name.
     */
    public static TimeIndex createIndex(String path, String name) throws TimeIndexException {
	Properties indexProperties = new Properties();
	TimeIndex index = null;

	boolean hasSlash = path.endsWith("/");
	indexProperties.setProperty("indexpath", path + 
				    (hasSlash?"":"/") + name);
	indexProperties.setProperty("name", "searchsite-" + name);

	
	index = (TimeIndex)factory.create(IndexType.INLINE, indexProperties);
	factory.close(index);

	return index; 
    }

    /**
     * Open a log file, given a path and a name.
     */
    public static TimeIndex openIndex(String path, String name) throws TimeIndexException {
	Properties indexProperties = new Properties();
	TimeIndex index = null;

	boolean hasSlash = path.endsWith("/");
	indexProperties.setProperty("indexpath", path + 
				    (hasSlash?"":"/") + name);

	index = (TimeIndex)factory.open(indexProperties);
	index.activate();

	return index;
    }

    /**
     * Close a log file, given an Index
     */
    public static boolean closeIndex(Index index) throws TimeIndexException {
	return factory.close(index);
    }

    /**
     * Add an entry to a log, given a Index and the text for the entry.
     */
    public static Index addEntry(Index index, String entry) throws TimeIndexException {
	// A chunk of data
	DataItem data  = new StringItem(entry);

	index.addItem(data);
	    
	return index;
   }

    /**
     * Add an entry to a log, given an Index, a data Timestamp, and the text for the entry.
     */
    public static Index addEntry(Index index, Timestamp dataTS, String entry) throws TimeIndexException {
	// A chunk of data
	DataItem data  = new StringItem(entry);

	index.addItem(data, dataTS);
	    
	return index;
   }

}



