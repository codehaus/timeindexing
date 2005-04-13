// IndexProperties.java

package com.timeindexing.index;

import com.timeindexing.util.CascadingMap;
import java.util.Properties;

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
