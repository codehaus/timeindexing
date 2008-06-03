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



// IndexProperties.java

package com.timeindexing.index;

import com.timeindexing.util.CascadingMap;
import java.util.Properties;
import java.util.Map;

/**
 * The index properties are a map of names to objects.
 */
public class IndexProperties extends CascadingMap {
   /**
     * A constructor.
     */
    public IndexProperties() {
    }

   /**
     * Construct some IndexProperties using a Properties object.
     * These mappings will have String objects. 
     */
    public IndexProperties(Properties properties) {
	super(properties);
    }

   /**
     * Construct some IndexProperties using a Map object.
     */
    public IndexProperties(Map map) {
	super(map);
    }

    /**
     * A constructor which specifies a submap
     * @param aSubMap an existing IndexProperties which will be a submap
     * to this IndexProperties
     */
    public IndexProperties(IndexProperties aSubMap) {
	super((CascadingMap)aSubMap);
    }

    /**
     * Get an entry from the IndexProperties.
     */
    public Object getProperty(Object key) {
	return super.get(key);
    }

    /**
     * Put an entry in the IndexProperties.
     * This version returns the IndexProperties object,
     * so it can be composed.
     * @return the IndexProperties object
     */
    public IndexProperties putProperty(Object key, Object value) {
	super.put(key, value);

	return this;
    }

}
