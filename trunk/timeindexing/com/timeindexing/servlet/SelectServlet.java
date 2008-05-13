// SelectServlet.java

package com.timeindexing.servlet;

import java.io.*;
import java.net.SocketException;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Enumeration;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.Selecter;
import com.timeindexing.appl.SelectionCodeEvaluator;
import com.timeindexing.appl.IndexPropertiesProcessor;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.time.Clock;
import com.timeindexing.event.IndexPrimaryEvent;
import com.timeindexing.event.IndexPrimaryEventListener;

/**
 * This servlet selects data from an index.
 * Data is specified for an index,
 * given a start time and an end time.
 * <p>
 * If an error occurs when checking the servlet setup or 
 * in the arguments are incorrect the response goes through
 * various jsp files.
 * The following values are currently defined as web.xml context-params,
 * shown here with their default values.
 * <table>
 * <tr><td>param-name</td><td>param-value</td></tr>
 * <tr><td>norepositorypage</td><td>/error/no_repository.jsp</td></tr>
 * <tr><td>noindexspecifiedpage</td><td>/error/no_index.jsp</td></tr>
 * <tr><td>nosecuritycodepage</td><td>/error/no_code.jsp</td></tr>
 * <tr><td>badsecuritycodepage</td><td>/error/bad_code.jsp</td></tr>
 * <tr><td>selectexceptionpage</td><td>//error/select_exception.jsp</td></tr>
 * </table>
 * <p>
 * If an excpetion occurs, the exception is placed in the request 
 * attribute "exception".
 * <br>
 * The response is presented through <tt>/error/select_exception.jsp</tt>.
 */
public class SelectServlet extends HttpServlet implements IndexPrimaryEventListener {
    private final static int GOOD = 0;

    private final static int NO_REPOSIORY = 1;
    private final static int NO_INDEX = 2;

    private final static int NO_CODE = 101;
    private final static int BAD_CODE = 102;
    private final static int CODE_EXCEPTION = 103;

    private final static int SELECT_EXCEPTION = 201;

    String repositioryName = null;
    String downloadLimitString = null;
    String securityCodeString = null;
    boolean securityCodeOn = false;
    String noRepositoryPage = null;
    String noIndexSpecifiedPage = null;
    String noSecurityCodePage = null;
    String badSecurityCodePage = null;
    String selectExceptionPage = null;

    /**
     * Init the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	/*
	 * Get the initialisation stuff
	 */
	ServletContext context = getServletContext();

	repositioryName = (String)context.getInitParameter("repository");

	if (empty(repositioryName)) {
	    throw new ServletException("SelectServlet: parameter \"repositiory\" not defined");
	}


	securityCodeString = (String)context.getInitParameter("security");
	downloadLimitString = (String)context.getInitParameter("downloadlimit");

	// get names of pages to forward to if an error occurs
	noRepositoryPage = (String)context.getInitParameter("norepositorypage");
	noIndexSpecifiedPage = (String)context.getInitParameter("noindexspecifiedpage");
	noSecurityCodePage = (String)context.getInitParameter("nosecuritycodepage");
	badSecurityCodePage = (String)context.getInitParameter("badsecuritycodepage");
	selectExceptionPage = (String)context.getInitParameter("selectexceptionpage");

	// set up values
	securityCodeOn = new Boolean(securityCodeString).booleanValue();

	if (noRepositoryPage == null) {
	    noRepositoryPage = "/error/no_repository.jsp";
	}

	if (noIndexSpecifiedPage == null) {
	    noIndexSpecifiedPage = "/error/no_index.jsp";
	}

	if (noSecurityCodePage == null) {
	    noSecurityCodePage = "/error/no_code.jsp";
	}

	if (badSecurityCodePage == null) {
	    badSecurityCodePage = "/error/bad_code.jsp";
	}
	
	if (selectExceptionPage == null) {
	    selectExceptionPage = "/error/select_exception.jsp";
	}


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
    {
	//System.err.println(Clock.time.time() + " SelectServlet: request = " + request + " response = " + response + ". Thread " + Thread.currentThread().getName());

	int status = GOOD;
	boolean allFine = false;
	long evaluatedCode = 0;

	/*
	 * Process the request
	 */
	IndexProperties properties = new IndexProperties();

	// indexpath
	String indexName = request.getParameter("file");

	if (empty(indexName)) {
	    status = NO_INDEX;
	} else {
	    properties.putProperty("file", indexName);
	}
	

	if (!empty(repositioryName) && !empty(indexName)) {
	    properties.putProperty("indexpath", repositioryName + "/" + indexName);
	}

	// start position
	String startpos = request.getParameter("sp");

	if (!empty(startpos)) {
	    properties.putProperty("startpos", startpos);
	}

	// start time
	String starttime = request.getParameter("st");

	if (!empty(starttime)) {
	    properties.putProperty("starttime", starttime);
	}

	// end position
	String endpos = request.getParameter("ep");

	if (!empty(endpos)) {
	    properties.putProperty("endpos", endpos);
	}


	// end time
	String endtime = request.getParameter("et");

	if (!empty(endtime)) {
	    properties.putProperty("endtime", endtime);
	}

	// count
	String count = request.getParameter("c");

	if (!empty(count)) {
	    properties.putProperty("count", count);
	}

	// for
	String elapsed = request.getParameter("f");

	if (!empty(elapsed)) {
	    properties.putProperty("for", elapsed);
	}

	// newline
	String newline =  request.getParameter("newline");

	if (!empty(newline)) {
	    properties.putProperty("newline", newline);
	}

	// code
	String code =  request.getParameter("code");

	if (!empty(code)) {
	    properties.putProperty("code", code);
	}

	// downloadlimit, from appl context
	if (!empty(downloadLimitString)) {
	    properties.putProperty("volumelimit", downloadLimitString);
	}

	/*
	 * check the output style
	 */
	String style = request.getParameter("style");

	
	if (style == null || style.equals("") || ! style.equals("stream")) {
	    // if style is NOT stream, then download

	    // set the filename for the download
	    setFilename(request, response, properties);
	} else {
	    // style is stream
	}

	

	/*
	 * Check if enough parameters
	 */
	
	if (!empty(repositioryName) && !empty(indexName)) {	// have indexName and repositioryName
	    allFine = true;

	    // check start of interval
	    if ((empty(startpos) && empty(starttime)) ||
		(!empty(startpos) && empty(starttime)) ||
		(empty(startpos) && !empty(starttime))) {
		// have one start time
		allFine = true;
	    } else {
		allFine = false;
	    }


	    // check end of interval
	    if ((empty(endpos)  && empty(endtime)  && empty(count)  && empty(elapsed)) ||
		(!empty(endpos) && empty(endtime)  && empty(count)  && empty(elapsed)) ||
		(empty(endpos)  && !empty(endtime) && empty(count)  && empty(elapsed)) ||
		(empty(endpos)  && empty(endtime)  && !empty(count) && empty(elapsed)) ||
		(empty(endpos)  && empty(endtime)  && empty(count)  && !empty(elapsed))) {
		// have one end time
		allFine = true;
	    } else {
		allFine = false;
	    }


	} else {
	    allFine = false;
	}

	    
	// everything is fine so far
	// let's check if there's security on
	if (allFine) {
	    // try securityCodes
	    if (isSecurityCodeOn()) {
		try {
		    String filename = (String)properties.get("indexpath");
		    SelectionCodeEvaluator codeChecker = new SelectionCodeEvaluator(filename);

		    evaluatedCode = codeChecker.evaluate(properties);

		    if (code != null && code.equals(String.valueOf(evaluatedCode))) {
			// the same code, so carry on
			System.err.println("Securty code OK " + code);
		    } else {
			// different codes, so complain
			if (code == null) {
			    System.err.println("Securty code Failed. No code");
			    status = NO_CODE;
			} else {
			    System.err.println("Securty code Failed. Wrong code.  Expected " + evaluatedCode + ". Got " + code);
			    System.err.println("Properties = " + properties);
			    status = BAD_CODE;
			}
			allFine = false;
		    }
		} catch (TimeIndexException tie) {
		    status = CODE_EXCEPTION;
		    request.setAttribute("exception", tie);
		}
	    }
	}


	// done all the checks
	// final stage
	if (! allFine) {
	    // everything was not all fine
	    // so call prePlaybackError()
	    prePlaybackError(request, response, status);
	} else {
	    // everything was fine, so
	    // do the playback

	    status = doPlayBack(request, response, properties, status);

	    // when we get here check to see the status
	    // we hope it is still GOOD;
	    if (status != GOOD) {
		// something went wrong, so
		// call postPlaybackError()
		postPlaybackError(request, response, status);
	    }
	}


    }

    /**
     * Playback the data.
     * @return the status of doing the playback
     */
    protected int doPlayBack(HttpServletRequest request, HttpServletResponse response, IndexProperties properties, int passedInStatus) throws IOException {
	int status = passedInStatus;

	// newline
	String newline =  request.getParameter("newline");

	OutputStream out = null;

	
	setContentType(request, response, getContentType());

	// if the output needs newlines
	// then assume we are writing to some text stream
	// and use the writer
	if (!empty(newline)) {
	    Writer writer = response.getWriter();

	    out = new WriterOutputStream(writer);
	} else {
	    out = response.getOutputStream();
	}


	// Lets open the index and select the relevant interval
	//
	// GO
	System.err.println(Clock.time.time() + " index://" + (String)properties.get("indexpath") + ". IP: " + request.getRemoteAddr() + " User-Agent: " + request.getHeader("User-Agent")  + ". Thread " + Thread.currentThread().getName() );


	Selecter selecter = null;

	try {
	    String filename = (String)properties.get("indexpath");

	    // get a Selecter
	    selecter = allocateSelecter(filename, out);

	    // add the servlet as an event listener
	    selecter.addPrimaryEventListener(this);

	    // select the data
	    selecter.select(properties);

	} catch (SocketException sex) {
	    // SocketException, client probably disconnected

	    // the selection failed
	    status = SELECT_EXCEPTION;
	    request.setAttribute("exception", sex);

	    System.err.println(Clock.time.time() + " index://" + (String)properties.get("indexpath") + ". Client disconnect: Message = " + sex.getMessage() + " after " + selecter.getBytesOutput() + " bytes"+ ". Thread " + Thread.currentThread().getName() );

	    try {
		selecter.removePrimaryEventListener(this);

		selecter.close();
	    } catch (TimeIndexException tie) {
		System.err.println("SelectServlet:close failed for " + 
			       (String)properties.get("indexpath") );
	    }
	} catch (IOException ioe) {
	    // IOException, client probably disconnected

	    // the selection failed
	    status = SELECT_EXCEPTION;
	    request.setAttribute("exception", ioe);

	    System.err.println(Clock.time.time() + " index://" + (String)properties.get("indexpath") + ". Client disconnect: Message = " + ioe.getMessage() + " after " + selecter.getBytesOutput() + " bytes"+ ". Thread " + Thread.currentThread().getName() );

	    try {
		selecter.close();
	    } catch (TimeIndexException tie) {
		System.err.println("SelectServlet:close failed for " + 
			       (String)properties.get("indexpath") );
	    }
	} catch (Exception ex) {

	    System.err.println(Clock.time.time() + " index://" + (String)properties.get("indexpath") + ". Failure: Message = " + ex.getMessage() + " after " + selecter.getBytesOutput() + " bytes"+ ". Thread " + Thread.currentThread().getName() );

	    ex.printStackTrace(System.err);

	    // the selection failed
	    status = SELECT_EXCEPTION;
	    request.setAttribute("exception", ex);

	    try {
		selecter.close();
	    } catch (TimeIndexException tie) {
		System.err.println("SelectServlet:close failed for " + 
			       (String)properties.get("indexpath") );
	    }
	} 

	return status;
    }

    /**
     * This is called if there is an error prior to playback.
     */
    protected void prePlaybackError(HttpServletRequest request, HttpServletResponse response, int status) throws IOException {

	PrintWriter out = new PrintWriter(response.getWriter());

	boolean browser = isBrowser(request);

	/*
	System.err.println("prePlaybackError: status = " + status);

	System.err.println("User-Agent: " + request.getHeader("User-Agent"));
	System.err.println("Is browser: " + browser);
	*/

	if ( ! browser) {
	    if (status == NO_REPOSIORY) {
		response.sendError(response.SC_NOT_FOUND, "The administrator needs to set the 'repositiory' for this application");
	    } else if (status == NO_INDEX) {
		response.sendError(response.SC_NOT_FOUND, "Can't find the requested index");
	    } else if (status == NO_CODE) {
		response.sendError(response.SC_NOT_FOUND, "No security code was passed in");
	    } else if (status == BAD_CODE) {
		response.sendError(response.SC_NOT_FOUND, "Bad security code was passed in");
	    } else {
		response.sendError(response.SC_NOT_FOUND, "Missing parameters");
	    }
	} else {
	    setContentType(request, response, "text/html");

	    ServletContext context = getServletContext();
	    RequestDispatcher dispatcher = null;


	    if (status == NO_REPOSIORY) {
		dispatcher = context.getRequestDispatcher(noRepositoryPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>The administrator needs to set the 'repositiory'");
		    out.println("init-param for the SelectServlet in web.xml</p>");

		    out.println("repositioryName = " + getServletContext().getInitParameter("repository"));
		}

	    } else if (status == NO_INDEX) {
		dispatcher = context.getRequestDispatcher(noIndexSpecifiedPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		    // couldnt include so have some default message
		    out.println("<h2>No index specified</h2>");

		    out.println("<p>No index has been specified for playback");
		    out.println(".</p>");
		    out.println("<p>No playback can be done.</p>");
		}
	    } else if (status == NO_CODE) {
		dispatcher = context.getRequestDispatcher(noSecurityCodePage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		    // couldnt include so have some default message
		    out.println("<h2>Security Code Failure</h2>");

		    out.println("<p>No code passed in.");
		    out.println(" Playback expects a code.</p>");
		    out.println("<p>No playback can be done.</p>");
		}
	    } else if (status == BAD_CODE) {
		dispatcher = context.getRequestDispatcher(badSecurityCodePage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		    // couldnt include so have some default message
		    out.println("<h2>Security Code Failure</h2>");

		    out.println("<p>Passed in code " + request.getParameter("code") + " does not match ");
		    out.println(" the expected code.</p>");
		    out.println("<p>No playback can be done.</p>");
		}
	    } else {
		out.println("<h2>Missing parameters</h2>");
		
		out.println("<p>Passed parameters are:</p>");
		out.println("<ol type=\"1\">");

		Enumeration e = request.getParameterNames(); 
		while (e.hasMoreElements()) {
		    String name = (String)e.nextElement();
		    String value = request.getParameter(name);
		    out.println("<li>" + name + " = " + value + "</li>");
		}
		out.println("</ol>");
	    }
	}
    }

    /**
     * This is called if there is an error during to playback.
     * 
     */
    protected void postPlaybackError(HttpServletRequest request, HttpServletResponse response, int status) throws IOException {

	PrintWriter out = new PrintWriter(response.getWriter());

	boolean browser = isBrowser(request);


	ServletContext context = getServletContext();
	RequestDispatcher dispatcher = null;


	if ( !browser ) {
	    if (status == SELECT_EXCEPTION) {
		response.sendError(response.SC_NOT_FOUND, "Playback failed");
	    } else {
		response.sendError(response.SC_NOT_FOUND, "Unknown playback error");
	    }

	} else {
	    setContentType(request, response, "text/html");

	    if (status == SELECT_EXCEPTION) {
		dispatcher = context.getRequestDispatcher(selectExceptionPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		    // couldnt include so have some default message
		
		    out.println("<h2>Playback failure</h2>");
		    out.println("<p>Cant select from the index");
		    out.println(".</p>");
		    out.println("<p>No playback can be done.</p>");	
		}
	    } else {
		out.println("<h2>Playback failure</h2>");
		out.println("<p>Unkown error. No playback can be done.</p>");	
	    }
	}
    }

    /**
     * Is a value empty.
     */
    protected boolean empty(String name) {
	if (name == null || name.equals("")) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Is the client a browser.
     */
    protected boolean isBrowser(HttpServletRequest request) {
	String ua = request.getHeader("User-Agent").toLowerCase();
	if(ua.indexOf("mozilla") >= 0 ||
	   ua.indexOf("msie") >= 0  ||
	   ua.indexOf("opera") >= 0 ||
           ua.indexOf("konqueror") >= 0 ||
	   ua.indexOf("icab") >= 0 ||
	   ua.indexOf("compatible") >= 0 ||
	   ua.indexOf("omniweb") >= 0) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Is security on.
     */
    protected boolean isSecurityCodeOn() {
	return securityCodeOn;
    }

    /**
     * allocate a Selecter
     */
    protected Selecter allocateSelecter(String filename,  OutputStream out) {
	return new Selecter(filename, out);
    }

    /**
     * Set the content type.
     */
    protected void setContentType(HttpServletRequest request, HttpServletResponse response, String type) {
	response.setContentType(type);
    }

    /**
     * Get the content type for this response.
     */
    protected String getContentType() {
	return "application/octet-stream";
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename(HttpServletRequest request, HttpServletResponse response, IndexProperties properties) {
	response.setHeader("Content-Disposition", "attachment; filename=" + fileNameGenerator(properties));;
    }

    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	return new IndexPropertiesProcessor().fileNameGenerator((String)properties.get("file"), properties);

    }

    /**
     * A notification that an Index has been opened.
     */
    public  void opened(IndexPrimaryEvent ipe) {
	System.err.println(Clock.time.time() + " " + ipe.getName() + ". OPENED" + ". Thread " + Thread.currentThread().getName() );
    }

    /**
     * A notification that an Index has been closed.
     */
    public  void closed(IndexPrimaryEvent ipe) {
	System.err.println(Clock.time.time() + " " + ipe.getName() + ". CLOSED" + ". Thread " + Thread.currentThread().getName() );
    }

    /**
     * A notification that an Index has been committed.
     */
    public  void committed(IndexPrimaryEvent ipe) {
	;
    }

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe) {
	;
    }

    /**
     * A notification that a view has been added to an Index.
     */
    public void viewAdded(IndexPrimaryEvent ipe) {
	System.err.println(Clock.time.time() + " " + ipe.getName() + ". ADD_VIEW" + ". Thread " + Thread.currentThread().getName() );
    }

    /**
     * A notification that a view has been removed to an Index.
     */
    public void viewRemoved(IndexPrimaryEvent ipe) {
	System.err.println(Clock.time.time() + " " + ipe.getName() + ". REMOVE_VIEW" + ". Thread " + Thread.currentThread().getName() );
    }

   /**
     * Wrap a Writer as an Output Stream.
     */
    public class WriterOutputStream extends OutputStream {

	private Writer writer;

	public WriterOutputStream(Writer writer) {
	    this.writer = writer;
	}

	public void write(int b) throws IOException {
	    writer.write(b & 0xFF);
	}

	public void write(byte[] b) throws IOException {
	    write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
	    for (int i=0; i<len; i++) {
		writer.write(b[off+i]);
	    }
	}

	public void close() throws IOException {
	    writer.close();
	}

	public void flush() throws IOException {
	    writer.flush();
	}
    }

}
