// LogOutput.java

package com.timeindexing.log;

/**
 * An interface for objects that will do output for a logger.
 */
public interface LogOutput {
    /**
     * Process a string
     */
    public void process(String s);
	
    /**
     * Process an LogInput
     */
    public void process(LogInput logObj);
}
	

