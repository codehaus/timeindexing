// TomcatAccessValve.java

package com.timeindexing.tomcat;

import org.apache.catalina.HttpResponse;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.valves.Constants;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.timeindexing.index.*;
import com.timeindexing.data.*;
import com.timeindexing.time.*;

import java.util.Properties;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;


/**
 * A logger for Tomcat that uses timeindexing.
 * Implementation of <b>AccessValve</b> that appends log messages to a timeindex
 * named {prefix}-name in a configured directory, with an
 * optional preceding timestamp.
 *
 * <p>Implementation of the <b>Valve</b> interface that generates a web server
 * access log with the detailed line contents matching a configurable pattern.
 * The syntax of the available patterns is similar to that supported by the
 * Apache <code>mod_log_config</code> module.  As an additional feature,
 * automatic rollover of log files when the date changes is also supported.</p>
 *
 * <p>Patterns for the logged message may include constant text or any of the
 * following replacement strings, for which the corresponding information
 * from the specified Response is substituted:</p>
 * <ul>
 * <li><b>%a</b> - Remote IP address
 * <li><b>%A</b> - Local IP address
 * <li><b>%b</b> - Bytes sent, excluding HTTP headers, or '-' if no bytes
 *     were sent
 * <li><b>%B</b> - Bytes sent, excluding HTTP headers
 * <li><b>%h</b> - Remote host name
 * <li><b>%H</b> - Request protocol
 * <li><b>%l</b> - Remote logical username from identd (always returns '-')
 * <li><b>%m</b> - Request method
 * <li><b>%p</b> - Local port
 * <li><b>%q</b> - Query string (prepended with a '?' if it exists, otherwise
 *     an empty string
 * <li><b>%r</b> - First line of the request
 * <li><b>%s</b> - HTTP status code of the response
 * <li><b>%t</b> - Date and time, in Common Log Format format
 * <li><b>%u</b> - Remote user that was authenticated
 * <li><b>%U</b> - Requested URL path
 * <li><b>%v</b> - Local server name
 * </ul>
 * <p>In addition, the caller can specify one of the following aliases for
 * commonly utilized patterns:</p>
 * <ul>
 * <li><b>common</b> - <code>%h %l %u %t "%r" %s %b</code>
 * <li><b>combined</b> - 
 *   <code>%h %l %u %t "%r" %s %b "%{Referer}i" "%{User-Agent}i"</code>
 * </ul>
 */
public class TomcatAccessValve extends ValveBase implements Lifecycle {

    /**
     * The as-of date for the currently open log file, or a zero-length
     * string if there is no open log file.
     */
    private String dateStamp = "";

    /**
     * The directory in which log files are created.
     */
    private String directory = "logs";

    /**
     * The descriptive information about this implementation.
     */
    protected static final String info = "com.timeindexing.tomcat.TomcatAccessValve/1.0";

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * The set of month abbreviations for log messages.
     */
    protected static final String months[] =
    { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };


    /**
     * If the current log pattern is the same as the common access log
     * format pattern, then we'll set this variable to true and log in
     * a more optimal and hard-coded way.
     */
    private boolean common = false;


    /**
     * For the combined format (common, plus useragent and referer), we do
     * the same
     */
    private boolean combined = false;


    /**
     * The pattern used to format our access log lines.
     */
    private String pattern = null;

    /**
     * The prefix that is added to log file filenames.
     */
    private String prefix = "catalina";

    /**
     * The suffix that is added to log file filenames.
     */
    private String suffix = "";

    /**
     * Has this component been started?
     */
    private boolean started = false;



    /**
     * A date formatter to format a Date into a date in the format
     * "yyyy-MM-dd".
     */
    private SimpleDateFormat dateFormatter = null;


    /**
     * A date formatter to format Dates into a day string in the format
     * "dd".
     */
    private SimpleDateFormat dayFormatter = null;


    /**
     * A date formatter to format a Date into a month string in the format
     * "MM".
     */
    private SimpleDateFormat monthFormatter = null;


    /**
     * A date formatter to format a Date into a year string in the format
     * "yyyy".
     */
    private SimpleDateFormat yearFormatter = null;


    /**
     * A date formatter to format a Date into a time in the format
     * "kk:mm:ss" (kk is a 24-hour representation of the hour).
     */
    private SimpleDateFormat timeFormatter = null;


    /**
     * The time zone relative to GMT.
     */
    private String timeZone = null;


    /**
     * The system time when we last updated the Date that this valve
     * uses for log lines.
     */
    private Date currentDate = null;


    /**
     * When formatting log lines, we often use strings like this one (" ").
     */
    private String space = " ";


    /**
     * Resolve hosts.
     */
    private boolean resolveHosts = false;



    /**
     * The string manager for this package.
     */
    private StringManager sm = StringManager.getManager(org.apache.catalina.logger.Constants.Package);

    /**
     * The Time Index for the log.
     */
    IndexView timeindex = null;

    /**
     * A TimeIndexFactory 
     */
    TimeIndexFactory factory = null;


    /**
     * Construct a TomcatAccessValve.
     */
    public TomcatAccessValve() {
	factory = new TimeIndexFactory();
	setPattern("common");
	System.err.println("In TomcatAccessValve constructor");
    }


    /**
     * Log a message summarizing the specified request and response, according
     * to the format specified by the <code>pattern</code> property.
     *
     * @param request Request being processed
     * @param response Response being processed
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error has occurred
     * @exception ServletException if a servlet error has occurred
     */
    public void invoke(Request request, Response response,
                       ValveContext context)
        throws IOException, ServletException {

        // Pass this request on to the next valve in our pipeline
        context.invokeNext(request, response);

        Date date = getDate();
        StringBuffer result = new StringBuffer();

        // Check to see if we should log using the "common" access log pattern
        if (common || combined) {
            String value = null;

            ServletRequest req = request.getRequest();
            HttpServletRequest hreq = null;
            if (req instanceof HttpServletRequest)
                hreq = (HttpServletRequest) req;

            if (isResolveHosts())
                result.append(req.getRemoteHost());
            else
                result.append(req.getRemoteAddr());

            result.append(" - ");

            if (hreq != null)
                value = hreq.getRemoteUser();
            if (value == null)
                result.append("- ");
            else {
                result.append(value);
                result.append(space);
            }

            result.append("[");
            result.append(dayFormatter.format(date));            // Day
            result.append('/');
            result.append(lookup(monthFormatter.format(date))); // Month
            result.append('/');
            result.append(yearFormatter.format(date));            // Year
            result.append(':');
            result.append(timeFormatter.format(date));        // Time
            result.append(space);
            result.append(timeZone);                            // Time Zone
            result.append("] \"");

            result.append(hreq.getMethod());
            result.append(space);
            result.append(hreq.getRequestURI());
            if (hreq.getQueryString() != null) {
                result.append('?');
                result.append(hreq.getQueryString());
            }
            result.append(space);
            result.append(hreq.getProtocol());
            result.append("\" ");

            result.append(((HttpResponse) response).getStatus());

            result.append(space);

            int length = response.getContentCount();

            if (length <= 0)
                value = "-";
            else
                value = "" + length;
            result.append(value);

            if (combined) {
                result.append(space);
                result.append("\"");
                String referer = hreq.getHeader("referer");
                if(referer != null)
                    result.append(referer);
                else
                    result.append("-");
                result.append("\"");

                result.append(space);
                result.append("\"");
                String ua = hreq.getHeader("user-agent");
                if(ua != null)
                    result.append(ua);
                else
                    result.append("-");
                result.append("\"");
            }

        } else {
            // Generate a message based on the defined pattern
            boolean replace = false;
            for (int i = 0; i < pattern.length(); i++) {
                char ch = pattern.charAt(i);
                if (replace) {
                    result.append(replace(ch, date, request, response));
                    replace = false;
                } else if (ch == '%') {
                    replace = true;
                } else {
                    result.append(ch);
                }
            }
        }

	result.append('\n');

	// now log this
        log(result.toString());

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
     * Writes the specified message to a Time Indexing log.
     * The name and type of the log is specific to the
     * servlet container.
     *
     * @param msg A <code>String</code> specifying the message to be written
     *  to the log file
     */
    public void log(String msg) {
	DataItem data  = new StringItem(msg);

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



    /**
     * Return the month abbreviation for the specified month, which must
     * be a two-digit String.
     *
     * @param month Month number ("01" .. "12").
     */
    private String lookup(String month) {

        int index;
        try {
            index = Integer.parseInt(month) - 1;
        } catch (Throwable t) {
            index = 0;  // Can not happen, in theory
        }
        return (months[index]);

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
    }

    /**
     * Return descriptive information about this implementation.
     */
    public String getInfo() {

        return (this.info);

    }


    /**
     * Return the format pattern.
     */
    public String getPattern() {

        return (this.pattern);

    }


    /**
     * Set the format pattern, first translating any recognized alias.
     *
     * @param pattern The new pattern
     */
    public void setPattern(String pattern) {

        if (pattern == null)
            pattern = "";
        if (pattern.equals(Constants.AccessLog.COMMON_ALIAS))
            pattern = Constants.AccessLog.COMMON_PATTERN;
        if (pattern.equals(Constants.AccessLog.COMBINED_ALIAS))
            pattern = Constants.AccessLog.COMBINED_PATTERN;
        this.pattern = pattern;

        if (this.pattern.equals(Constants.AccessLog.COMMON_PATTERN))
            common = true;
        else
            common = false;

        if (this.pattern.equals(Constants.AccessLog.COMBINED_PATTERN))
            combined = true;
        else
            combined = false;

    }

    /**
     * Set the resolve hosts flag.
     *
     * @param resolveHosts The new resolve hosts value
     */
    public void setResolveHosts(boolean resolveHosts) {

        this.resolveHosts = resolveHosts;

    }


    /**
     * Get the value of the resolve hosts flag.
     */
    public boolean isResolveHosts() {

        return resolveHosts;

    }



    /**
     * Return the replacement text for the specified pattern character.
     *
     * @param pattern Pattern character identifying the desired text
     * @param date the current Date so that this method doesn't need to
     *        create one
     * @param request Request being processed
     * @param response Response being processed
     */
    private String replace(char pattern, Date date, Request request,
                           Response response) {

        String value = null;

        ServletRequest req = request.getRequest();
        HttpServletRequest hreq = null;
        if (req instanceof HttpServletRequest)
            hreq = (HttpServletRequest) req;
        ServletResponse res = response.getResponse();
        HttpServletResponse hres = null;
        if (res instanceof HttpServletResponse)
            hres = (HttpServletResponse) res;

        if (pattern == 'a') {
            value = req.getRemoteAddr();
        } else if (pattern == 'A') {
            value = "127.0.0.1";        // FIXME
        } else if (pattern == 'b') {
            int length = response.getContentCount();
            if (length <= 0)
                value = "-";
            else
                value = "" + length;
        } else if (pattern == 'B') {
            value = "" + response.getContentLength();
        } else if (pattern == 'h') {
            value = req.getRemoteHost();
        } else if (pattern == 'H') {
            value = req.getProtocol();
        } else if (pattern == 'l') {
            value = "-";
        } else if (pattern == 'm') {
            if (hreq != null)
                value = hreq.getMethod();
            else
                value = "";
        } else if (pattern == 'p') {
            value = "" + req.getServerPort();
        } else if (pattern == 'q') {
            String query = null;
            if (hreq != null)
                query = hreq.getQueryString();
            if (query != null)
                value = "?" + query;
            else
                value = "";
        } else if (pattern == 'r') {
            StringBuffer sb = new StringBuffer();
            if (hreq != null) {
                sb.append(hreq.getMethod());
                sb.append(space);
                sb.append(hreq.getRequestURI());
                if (hreq.getQueryString() != null) {
                    sb.append('?');
                    sb.append(hreq.getQueryString());
                }
                sb.append(space);
                sb.append(hreq.getProtocol());
            } else {
                sb.append("- - ");
                sb.append(req.getProtocol());
            }
            value = sb.toString();
        } else if (pattern == 's') {
            if (hres != null)
                value = "" + ((HttpResponse) response).getStatus();
            else
                value = "-";
        } else if (pattern == 't') {
            StringBuffer temp = new StringBuffer("[");
            temp.append(dayFormatter.format(date));             // Day
            temp.append('/');
            temp.append(lookup(monthFormatter.format(date)));   // Month
            temp.append('/');
            temp.append(yearFormatter.format(date));            // Year
            temp.append(':');
            temp.append(timeFormatter.format(date));            // Time
            temp.append(' ');
            temp.append(timeZone);                              // Timezone
            temp.append(']');
            value = temp.toString();
        } else if (pattern == 'u') {
            if (hreq != null)
                value = hreq.getRemoteUser();
            if (value == null)
                value = "-";
        } else if (pattern == 'U') {
            if (hreq != null)
                value = hreq.getRequestURI();
            else
                value = "-";
        } else if (pattern == 'v') {
            value = req.getServerName();
        } else {
            value = "???" + pattern + "???";
        }

        if (value == null)
            return ("");
        else
            return (value);

    }

    /**
     * This method returns a Date object that is accurate to within one
     * second.  If a thread calls this method to get a Date and it's been
     * less than 1 second since a new Date was created, this method
     * simply gives out the same Date again so that the system doesn't
     * spend time creating Date objects unnecessarily.
     */
    private Date getDate() {

        // Only create a new Date once per second, max.
        long systime = System.currentTimeMillis();
        if ((systime - currentDate.getTime()) > 1000) {
            currentDate = new Date(systime);
        }

        return currentDate;

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
	System.err.println("TomcatAccessValve: start()");

        // Validate and update our current component state
        if (started)
            throw new LifecycleException
                (sm.getString("fileAccessValve.alreadyStarted"));

        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

        // Initialize the timeZone, Date formatters, and currentDate
        TimeZone tz = TimeZone.getDefault();
        timeZone = "" + (tz.getRawOffset() / (60 * 60 * 10));
        if (timeZone.length() < 5)
            timeZone = timeZone.substring(0, 1) + "0" +
                timeZone.substring(1, timeZone.length());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setTimeZone(tz);
        dayFormatter = new SimpleDateFormat("dd");
        dayFormatter.setTimeZone(tz);
        monthFormatter = new SimpleDateFormat("MM");
        monthFormatter.setTimeZone(tz);
        yearFormatter = new SimpleDateFormat("yyyy");
        yearFormatter.setTimeZone(tz);
        timeFormatter = new SimpleDateFormat("HH:mm:ss");
        timeFormatter.setTimeZone(tz);
        currentDate = new Date();
        dateStamp = dateFormatter.format(currentDate);

	open();

	System.err.println("TomcatAccessValve: start() open complete");


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
                (sm.getString("fileAccessValve.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        close();

    }


}
