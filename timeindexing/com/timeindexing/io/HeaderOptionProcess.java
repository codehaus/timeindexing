// HeaderOptionProcess.java

package com.timeindexing.io;


/**
 * These are the processes that can be done on a header option.
 */
public interface HeaderOptionProcess {
    /**
     * Determine its size
     */
    public final static HeaderOptionProcess SIZE = new HeaderOptionProcess() {
	    public String toString() {
		return "SIZE";
	    }
	};




    /**
     * Write it out
     */
    public final static HeaderOptionProcess WRITE = new HeaderOptionProcess() {
	    public String toString() {
		return "WRITE";
	    }
	};

    /**
     * Read it in.
     */
    public final static HeaderOptionProcess READ = new HeaderOptionProcess() {
	    public String toString() {
		return "READ";
	    }
	};


}


