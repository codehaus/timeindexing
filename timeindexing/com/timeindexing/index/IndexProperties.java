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

}
