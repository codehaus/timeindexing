// TomcatLogger.java

package com.timeindexing.tomcat;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.logger.LoggerBase;

import com.timeindexing.index.*;
import com.timeindexing.data.*;
import com.timeindexing.time.*;

import java.util.Properties;
import java.io.File;

/**
 * A logger for Tomcat that uses timeindexing.
 * Implementation of <b>Logger</b> that appends log messages to a timeindex
 * named {prefix}-name in a configured directory, with an
 * optional preceding timestamp.
 */
public class TomcatLogger extends LoggerBase implements Lifecycle {
    /**
     * The directory in which log files are created.
     */
    private String directory = "logs";

    /**
     * The prefix that is added to log file filenames.
     */
    private String prefix = "catalina";

    /**
     * The suffix that is added to log file filenames.
     */
    private String suffix = ".log";


    /**
     * Should logged messages be date/time stamped?
     */
    private boolean timestamp = false;

    /**
     * Has this component been started?
     */
    private boolean started = false;

    /**
     * The descriptive information about this implementation.
     */
    protected static final String info = "com.timeindexing.tomcat.TomcatLogger/1.0";

    /**
     * The string manager for this package.
     */
    private StringManager sm = StringManager.getManager(org.apache.catalina.logger.Constants.Package);

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * The Time Index for the log.
     */
    IndexView timeindex = null;

    /**
     * A TimeIndexFactory 
     */
    TimeIndexFactory factory = null;


    /**
     * Construct a TomcatLogger.
     */
    public TomcatLogger() {
	factory = new TimeIndexFactory();
    }

    /**
     * Open the new log file for the date specified by <code>date</code>.
     */
    private void open() {
	IndexView index = null;

	Properties indexProperties = new Properties();

	File dir = new File(directory);
	if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("catalina.base"), directory);
	}
        dir.mkdirs();

	String indexpath = dir.getAbsolutePath() + File.separator + getPrefix();
	indexProperties.setProperty("indexpath", indexpath);

	try {
	    index = factory.open(indexProperties);

	    // it's been opened successfully
	    timeindex = index;


	    // now activate it
	    timeindex.activate();
	    
	} catch (IndexOpenException ioe) {
	    // it wasn;t opened so we need to create it
	    // set it's name
	    indexProperties.setProperty("name", getPrefix());
	    // set the data path
	    String datapath = dir.getAbsolutePath() + File.separator + getPrefix() + getSuffix();
	    indexProperties.setProperty("datapath", datapath);


	    try {
		index = factory.create(IndexType.EXTERNAL_DT, indexProperties);

		timeindex = index;

	    } catch (TimeIndexException tie) {
		// a creation error
		System.err.println("Failed to create index " + indexpath);
		System.err.println("---- Reason ----");
		tie.printStackTrace(System.err);
	    }
	} catch (TimeIndexException tie) {
	    // some other error
	    System.err.println("Failed to open index " + indexpath);
	    System.err.println("---- Reason ----");
	    tie.printStackTrace(System.err);
	}
    }

    /**
     * Close the currently open log file (if any)
     */
    private void close() {
	try {
	    factory.close(timeindex);
	} catch (TimeIndexException ice) {
	    System.err.println("Failed to close Time Index " + timeindex.getURI());
	    System.err.println("---- Reason ----");
	    ice.printStackTrace(System.err);
	}
    }


    /**
     * Writes the specified message to a servlet log file, usually an event
     * log.  The name and type of the servlet log is specific to the
     * servlet container.
     *
     * @param msg A <code>String</code> specifying the message to be written
     *  to the log file
     */
    public void log(String msg) {
	StringBuffer msgBuffer = new StringBuffer(msg.length() + 2);
	msgBuffer.append(msg);
	msgBuffer.append('\n');

	DataItem data  = new StringItem(msgBuffer.toString());

	try {
	    if (timeindex == null) {
		open();
	    }

	    timeindex.addItem(data);

	    // force a flush
	    timeindex.flush();
	}  catch (TimeIndexException tie) {
	    // an error adding an item
	    System.err.println("Failed to add an Index Item to index " + timeindex.getURI());
	    System.err.println("---- Reason ----");
	    tie.printStackTrace(System.err);
	}
    }



    // ------------------------------------------------------ Attributes

    /**
     * Return the log file prefix.
     */
    public String getPrefix() {

        return (prefix);

    }


    /**
     * Set the log file prefix.
     *
     * @param prefix The new log file prefix
     */
    public void setPrefix(String prefix) {

        String oldPrefix = this.prefix;
        this.prefix = prefix;
        support.firePropertyChange("prefix", oldPrefix, this.prefix);

    }



    /**
     * Return the log file suffix.
     */
    public String getSuffix() {

        return (suffix);

    }


    /**
     * Set the log file suffix.
     *
     * @param suffix The new log file suffix
     */
    public void setSuffix(String suffix) {

        String oldSuffix = this.suffix;
        this.suffix = suffix;
        support.firePropertyChange("suffix", oldSuffix, this.suffix);

    }


    /**
     * Return the timestamp flag.
     */
    public boolean getTimestamp() {

        return (timestamp);

    }


    /**
     * Set the timestamp flag.
     *
     * @param timestamp The new timestamp flag
     */
    public void setTimestamp(boolean timestamp) {

        boolean oldTimestamp = this.timestamp;
        this.timestamp = timestamp;
        support.firePropertyChange("timestamp", new Boolean(oldTimestamp),
                                   new Boolean(this.timestamp));

    }

    /**
     * Return the directory in which we create log files.
     */
    public String getDirectory() {

        return (directory);

    }

    /**
     * Set the directory in which we create log files.
     *
     * @param directory The new log file directory
     */
    public void setDirectory(String directory) {

        String oldDirectory = this.directory;
        this.directory = directory;
        support.firePropertyChange("directory", oldDirectory, this.directory);

    }

    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {

        lifecycle.addLifecycleListener(listener);

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to add
     */
    public void removeLifecycleListener(LifecycleListener listener) {

        lifecycle.removeLifecycleListener(listener);

    }

    /**
     * Get an array of all the LifecycleListeners.
     */
    public LifecycleListener[] findLifecycleListeners() {
	return lifecycle.findLifecycleListeners();
    }

    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.
     *
     * @exception IllegalStateException if this component has already been
     *  started
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        // Validate and update our current component state
        if (started)
            throw new LifecycleException
                (sm.getString("fileLogger.alreadyStarted"));
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

    }


    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.
     *
     * @exception IllegalStateException if this component has not been started
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {

        // Validate and update our current component state
        if (!started)
            throw new LifecycleException
                (sm.getString("fileLogger.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        close();

    }


}
