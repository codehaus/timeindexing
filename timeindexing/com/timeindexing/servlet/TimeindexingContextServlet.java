// TimeindexingContextServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


import java.util.Properties;
import java.util.Map;
import java.util.HashMap;



/**
 * A base class for all Timeindexing Servlets.
 * It deals with getting the properties files open
 * and setting things up.
 * <p>
 * If an error occurs when checking the servlet setup or 
 * in the arguments are incorrect the response goes through
 * various jsp files.
 * The following values are currently defined in the errorpages.properties file
 * held by the servlet context in the /WEB-INF/ directory.
 * The values are shown here with their default values.
 * <table>
 * <tr><th>param-name</th><th>param-value</th></tr>
 * <tr><td>norepositorypropertiespage</td><td>/error/no_repository_properties.jsp</td></tr>
 * <tr><td>cantloadrepositorypropertiespage</td><td>/error/cant_load_repository_properties.jsp</td></tr>
 * <tr><td>badrepositorypropertiespage</td><td>/error/bad_repository_properties.jsp</td></tr>
 * <tr><td>baddefaultrepositorypage</td><td>/error/bad_default_repository.jsp</td></tr>
 * <tr><td>noerrorpagepropertiespage</td><td>/error/no_errorpage_properties.jsp</td></tr>
 * <tr><td>cantloaderrorpagepropertiespage</td><td>/error/cant_load_errorpage_properties.jsp</td></tr>
 * <tr><td>norepositorypage</td><td>/error/no_repository.jsp</td></tr>
 * <tr><td>noindexspecifiedpage</td><td>/error/no_index.jsp</td></tr>
 * <tr><td>nosecuritycodepage</td><td>/error/no_code.jsp</td></tr>
 * <tr><td>badsecuritycodepage</td><td>/error/bad_code.jsp</td></tr>
 * <tr><td>selectexceptionpage</td><td>/error/select_exception.jsp</td></tr>
 * </table>
 * <p>
 * If an excpetion occurs, the exception is placed in the request 
 * attribute "exception".
 * <br>
 * The response is presented through the <em>selectexceptionpage</em>.
 */
public abstract class TimeindexingContextServlet extends HttpServlet {
    final static int GOOD = 0;

    final static int NO_REPOSITORY_PROPERTIES = 1;
    final static int CANT_LOAD_REPOSITORY_PROPERTIES = 2;
    final static int BAD_REPOSITORY_PROPERTIES = 3;
    final static int BAD_DEFAULT_REPOSITORY = 4;

    final static int NO_ERRORPAGE_PROPERTIES = 11;
    final static int CANT_LOAD_ERRORPAGE_PROPERTIES = 12;

    final static int NO_REPOSITORY = 101;
    final static int NO_INDEX = 102;

    final static int NO_CODE = 201;
    final static int BAD_CODE = 202;
    final static int CODE_EXCEPTION = 303;

    final static int SELECT_EXCEPTION = 401;

    int status = GOOD;

    String noRepositoryPropertiesPage = null;
    String cantLoadRepositoryPropertiesPage = null;
    String badRepositoryPropertiesPage = null;
    String badDefaultRepositoryPage = null;
    String noErrorPagePropertiesPage = null;
    String cantLoadErrorPagePropertiesPage = null;
    String noRepositoryPage = null;
    String noIndexSpecifiedPage = null;
    String noSecurityCodePage = null;
    String badSecurityCodePage = null;
    String selectExceptionPage = null;

    // A map of repository names to RepositoryInfo objects
    Map repositoryMap = null;

    // the name of a default repository if no repository
    // is specified is the subclasses 
    String defaultRepositoryName = null;

    /*
     * Values out of the context.
     */
    boolean securityCodeOn = false;
    String downloadLimitString = null;

    /**
     * Initialize Timeindexing Servlets.
     * If there is a problem, then status is set to an error value,
     * rather than throwing an Exception.
     * This allows subclasses more control to get information to the end-user.
     * Consequently, all subclasses need to check that status == GOOD,
     * before continuing.
     */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	/*
	 * Get the initialisation stuff
	 */
	ServletContext context = getServletContext();

	// check the error pages are set up
	checkErrorPages();

	/*
	 * Load the repository properties
	 */
	InputStream repositoryStream = context.getResourceAsStream("/WEB-INF/repository.properties");

	Properties repositoryProperties = new Properties();

	if (repositoryStream == null) {
	    // no repository properties
	    status = NO_REPOSITORY_PROPERTIES;
	} else {
	    // load up the properties
	    try {
		repositoryProperties.load(repositoryStream);
		repositoryStream.close();
	    } catch (IOException ioe) {
		status = CANT_LOAD_REPOSITORY_PROPERTIES;
	    }
	}

	/*
	 * Is it work continuing.
	 */
	if (status != GOOD) {
	    return;
	}

	/*
	 * Load the error page properties
	 */
	InputStream errorPageStream = context.getResourceAsStream("/WEB-INF/errorpages.properties");

	Properties errorPageProperties = new Properties();

	if (errorPageStream == null) {
	    // no errorPage properties
	    status = NO_ERRORPAGE_PROPERTIES;
	} else {
	    // load up the properties
	    try {
		errorPageProperties.load(errorPageStream);
		errorPageStream.close();
	    } catch (IOException ioe) {
		status = CANT_LOAD_ERRORPAGE_PROPERTIES;
	    }
	}

	/*
	 * Evaluate the error pages
	 */

	// get names of pages to forward to if an error occurs
	noRepositoryPropertiesPage = errorPageProperties.getProperty("norepositorypropertiespage");
	cantLoadRepositoryPropertiesPage = errorPageProperties.getProperty("cantloadrepositorypropertiespage");
	badRepositoryPropertiesPage = errorPageProperties.getProperty("badrepositorypropertiespage");
	badRepositoryPropertiesPage = errorPageProperties.getProperty("baddefaultrepositorypage");
	noErrorPagePropertiesPage = errorPageProperties.getProperty("noerrorpagepropertiespage");
	cantLoadErrorPagePropertiesPage = errorPageProperties.getProperty("cantloaderrorpagepropertiespage");
	noRepositoryPage = errorPageProperties.getProperty("norepositorypage");
	noIndexSpecifiedPage = errorPageProperties.getProperty("noindexspecifiedpage");
	noSecurityCodePage = errorPageProperties.getProperty("nosecuritycodepage");
	badSecurityCodePage = errorPageProperties.getProperty("badsecuritycodepage");
	selectExceptionPage = errorPageProperties.getProperty("selectexceptionpage");

	// setup the error pages in case any are null
	checkErrorPages();

	/*
	 * Is it work continuing.
	 */
	if (status != GOOD) {
	    return;
	}

	/*
	 * Things are looking good.
	 * Evaluate the repositories.
	 */
	String repositiories = repositoryProperties.getProperty("repositories");

	if (empty(repositiories)) {
	    status = NO_REPOSITORY;
	    return;
	} else {
	    // allocate a HashMap to hold repositiory information
	    repositoryMap = new HashMap();

	    // get the names of the repositories from the property file
	    String[] repositioryNames = repositiories.split(",");

	    // skip through all the names
	    for (int rep=0; rep < repositioryNames.length; rep++) {
		// get the name
		String name = repositioryNames[rep];

		// get the index path
		String indexPath = repositoryProperties.getProperty("repository." + name + ".indexpath");

		// if we didn't get an indexpath for this name, break out
		if (indexPath == null) {
		    status = BAD_REPOSITORY_PROPERTIES;
		    return;
		}

		// get the descriptor path
		String descriptorPath = repositoryProperties.getProperty("repository." + name + ".descriptorpath");

		// if we didn't get a descriptorpath for this name, break out
		if (descriptorPath == null) {
		    status = BAD_REPOSITORY_PROPERTIES;
		    return;
		}

		// allocate the RepositoryInfo
		RepositoryInfo repInfo = new RepositoryInfo(name, indexPath, descriptorPath);

		// and put it in the map
		repositoryMap.put(name, repInfo);
	    }


	}

	// get some init parameters out of the context

	String securityCodeString = (String)context.getInitParameter("security");

	// set up values
	securityCodeOn = new Boolean(securityCodeString).booleanValue();

	String downloadLimitString = (String)context.getInitParameter("downloadlimit");
	


    }

    /**
     * This is called if there is an error during init.
     */
    protected void initError(HttpServletRequest request, HttpServletResponse response) throws IOException {

	PrintWriter out = new PrintWriter(response.getWriter());

	boolean browser = isBrowser(request);


	if ( ! browser) {
	    if (status == NO_REPOSITORY_PROPERTIES) {
		response.sendError(response.SC_NOT_FOUND, "The administrator needs to set the repository.properties for this application");

	    } else if (status == CANT_LOAD_REPOSITORY_PROPERTIES) {
		response.sendError(response.SC_NOT_FOUND, "Can't load the repository.properties for this application. Call the administrator.");

	    } else if (status == NO_REPOSITORY) {
		response.sendError(response.SC_NOT_FOUND, "The administrator needs to set the 'repositories' in repository.properties.");

	    } else if (status == BAD_REPOSITORY_PROPERTIES) {
		response.sendError(response.SC_NOT_FOUND,  "The repository.properties is not configure properly. Call the administrator.");

	    } else if (status == BAD_DEFAULT_REPOSITORY) {
		response.sendError(response.SC_NOT_FOUND, "The default repository has not been setup correctly. Call the administrator.");

	    } else if (status == NO_ERRORPAGE_PROPERTIES) {
		response.sendError(response.SC_NOT_FOUND, "The administrator needs to set the errorpage.properties for this application");

	    } else if (status == CANT_LOAD_ERRORPAGE_PROPERTIES) {
		response.sendError(response.SC_NOT_FOUND, "Can't load the errorpage.properties for this application. Call the administrator.");

	    } else {
		response.sendError(response.SC_NOT_FOUND, "Unexpected error code: " + status + ". Call the administrator");
	    }

	} else {
	    // it is a browser

	    response.setContentType("text/html");

	    ServletContext context = getServletContext();
	    RequestDispatcher dispatcher = null;

	    if (status == NO_REPOSITORY_PROPERTIES) {
		dispatcher = context.getRequestDispatcher(noRepositoryPropertiesPage);

		try {
		    System.err.println("page = " + noRepositoryPropertiesPage);
		    System.err.println("dispatcher = " + dispatcher);
		    System.err.println("request = " + request);
		    System.err.println("response = " + response);

		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>The administrator needs to set  'repository.properties'");
		    out.println("in /WEB-INF/.</p>");

		}


	    } else if (status == CANT_LOAD_REPOSITORY_PROPERTIES) {
		dispatcher = context.getRequestDispatcher(cantLoadRepositoryPropertiesPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>Can't load  'repository.properties'");
		    out.println("Call the administrator.</p>");

		}

	    } else if (status == NO_REPOSITORY) {
		dispatcher = context.getRequestDispatcher(noRepositoryPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>The administrator needs to set the 'repositories'");
		    out.println("correctly in repository.properties</p>");
		}

	    } else if (status == BAD_REPOSITORY_PROPERTIES) {
		dispatcher = context.getRequestDispatcher(badRepositoryPropertiesPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>There are incorrectly set parameter is 'repository.properties'.");
		    out.println("Call the administrator.</p>");

		}


	    } else if (status == BAD_DEFAULT_REPOSITORY) {
		dispatcher = context.getRequestDispatcher(badDefaultRepositoryPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>The default repository has not been setup correctly in 'repository.properties'.");
		    out.println("Call the administrator.</p>");

		}


	    } else if (status == NO_ERRORPAGE_PROPERTIES) {
		dispatcher = context.getRequestDispatcher(noErrorPagePropertiesPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>The administrator needs to set  'errorpage.properties'");
		    out.println("in /WEB-INF/.</p>");

		}


	    } else if (status == CANT_LOAD_ERRORPAGE_PROPERTIES) {
		dispatcher = context.getRequestDispatcher(cantLoadErrorPagePropertiesPage);

		try {
		    dispatcher.forward(request , response);
		} catch (ServletException se) {
		} catch (NullPointerException nul) {
		    out.println("<h2>Misconfigured Web Application</h2>");

		    out.println("<p>Can't load  'errorpage.properties'");
		    out.println("Call the administrator.</p>");

		}


	    } else {
		out.println("<h2>Misconfigured Web Application</h2>");

		out.println("<p>Unexpected error status code: ");
		out.println(status);
		out.println(". ");
		out.println("Call the administrator.</p>");


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
     * Make sure the names of the error pages are set up
     * with soem default values, if any of them are set to null.
     */
    protected void checkErrorPages() {
	// check the error pages
	if (noRepositoryPropertiesPage == null) {
	    noRepositoryPropertiesPage = "/error/no_repository_properties.jsp";
	}

	if (cantLoadRepositoryPropertiesPage == null) {
	    cantLoadRepositoryPropertiesPage = "/error/cant_load_repository_properties.jsp";
	}

	if (badRepositoryPropertiesPage == null) {
	    badRepositoryPropertiesPage = "/error/bad_repository_properties.jsp";
	}

	if (badDefaultRepositoryPage == null) {
	    badDefaultRepositoryPage = "/error/bad_default_repository.jsp";
	}

	if (noErrorPagePropertiesPage == null) {
	    noErrorPagePropertiesPage = "/error/no_errorpage_properties.jsp";
	}

	if (cantLoadErrorPagePropertiesPage == null) {
	    cantLoadErrorPagePropertiesPage = "/error/cant_load_errorpage_properties.jsp";
	}

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
}
