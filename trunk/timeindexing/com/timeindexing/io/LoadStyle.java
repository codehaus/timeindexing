// LoadStyle.java

package com.timeindexing.io;


/**
 * An enumeration of ways to load the index.
 */
public interface LoadStyle {
    /**
     * Load all of the index and all of the data.
     */
    public final static LoadStyle ALL = new LoadStyle() {
	    public String toString() {
		return "ALL";
	    }
	};


    /**
     * Load all of the index and none of the data.
     */
    public final static LoadStyle HOLLOW = new LoadStyle() {
	    public String toString() {
		return "HOLLOW";
	    }
	};



    /**
     * Load nothing.
     */
    public final static LoadStyle NONE = new LoadStyle() {
	    public String toString() {
		return "NONE";
	    }
	};



}


    
