// Logger.java

package com.timeindexing.log;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.PrintStream;


/**
 * An object that will be a logger.
 * It keeps a set of output objects which actually do the logging,
 * and for each one there is a bitset/mask which determines which
 * log message will be accepted by which output.
 */
public class Logger implements Logging {
    /**
     * The empty BitSet;
     */
    //static BitSet empty = new BitSet();

    /**
     * A map of output object to its BitSet.
     */
    Map outputs = null;

    /**
     * Log a message using a Strng.
     */
    public void log(BitSet mask, String msg) {
	doLog(mask, msg);
    }

    /**
     * Log using a LogInput object.
     */
    public void log(BitSet mask, LogInput obj) {
        doLog(mask, obj);
    }

    /**
     * Add output to a printwriter
     */
    public PrintWriter addOutput(PrintWriter w) {
	addOutputLog(w, new BitSet());
	return w;
    }

    /**
     * Add output to a printstream.
     */
    public PrintStream addOutput(PrintStream s){
	addOutputLog(s, new BitSet());
	return s;
    }


    /**
     * Add output to an LogOutput object.
     */
    public LogOutput addOutput(LogOutput eo) {
	addOutputLog(eo, new BitSet());
	return eo;
    }

    /**
     * Remove output to a printwriter
     */
    public void removeOutput(PrintWriter w) {
	removeOutputLog(w);
    }

    /**
     * Remove output to a printstream.
     */
    public void removeOutput(PrintStream s){
	removeOutputLog(s);
    }


    /**
     * Remove output to an LogOutput object.
     */
    public void removeOutput(LogOutput eo) {
	removeOutputLog(eo);
    }

    /**
     * Get a mask for a an output object.
     */
    public BitSet getMaskForOutput(Object output) {
	if (outputs == null) {
	    return null;
	} else {
	    return (BitSet)outputs.get(output);
	}
    }

    /**
     * Set a mask for a an output object.
     */
    public void setMaskForOutput(BitSet mask, Object output) {
	if (outputs == null) {
	    return;
	} else {
	    outputs.put(output, mask);
	}
    }

	

    /**
     * Add an output to the outputs map.
     */
    protected void addOutputLog(Object output, BitSet mask) {
	if (outputs == null) {
	    outputs = new HashMap();
	}

	outputs.put(output, mask);
    }

    /**
     * Remove an output to the outputs map.
     */
    protected void removeOutputLog(Object output) {
	if (outputs == null) {
	    return;
	} else {
	    outputs.remove(output);
	}
    }



    /**
     * Visit each output object and determine if it will
     * accept a log message, and if so log it.
     */
    protected void doLog(BitSet mask, Object message) {
	if (outputs == null) {
	    return;
        }

	Iterator outputI = outputs.keySet().iterator();

	while (outputI.hasNext()) {
	    Object anOutput = outputI.next();

	    // we need to clone becasue ops on BitSets are in-place
	    // and destructive :-(
	    BitSet aMask = (BitSet)outputs.get(anOutput);
	    BitSet acceptMask = (BitSet)aMask.clone();

	    acceptMask.and(mask);

	    // if the result has more than 0 bits set then
	    // the current output will accept the current message
	    if (! (acceptMask.cardinality() == 0)) { // equals(empty)) {
		dispatch(message, anOutput);
           }
        }
    }

    /**
     * Dispatch a message to an output object.
     * @param message the message, either a String or a LogInput
     * @param anOutput the output object, 
     */
    protected void dispatch(Object message, Object anOutput) {
	// if output is a LogOutput pass on the message
        // as it knows how to deal with it
	if (anOutput instanceof LogOutput) {
	    if (message instanceof LogInput) {
		((LogOutput)anOutput).process((LogInput)message);
	    } else {
		((LogOutput)anOutput).process((String)message);
	    }
	} else if (anOutput instanceof PrintWriter) {
	    ((PrintWriter)anOutput).print(message.toString());
	} else if (anOutput instanceof PrintStream) {
	    ((PrintStream)anOutput).print(message.toString());
	} else {
	    ;
	}
    }
		    
}
