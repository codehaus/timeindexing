// SecurityCodeServlet.java

package com.timeindexing.servlet;

import com.timeindexing.appl.SelectionCodeEvaluator;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexProperties;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet generates a security code for an index,
 * given a start time and an end time.
 * <p>
 * The security code is placed in the request attribute "securitycode".
 * <br>
 * The response is presented through <tt>/security/show_code.jsp</tt>.
 * <p>
 * If an exception occurs, the exception is placed in the request 
 * attribute "exception".
 * <br>
 * The response is presented through <tt>/security/select_exception.jsp</tt>.
 */
public class SecurityCodeServlet extends SelectServlet {

    /**
     * Do the playout functionality.
     * In this case, it is to show the security code for a selection.
     */
    public void doPlayBack(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
	
	setContentType("text/html");

	try {
	    String filename= (String)properties.get("indexpath");

	    SelectionCodeEvaluator codeChecker = new SelectionCodeEvaluator(filename);

	    long evaluatedCode = codeChecker.evaluate(properties);

	    request.setAttribute("securitycode", new Long(evaluatedCode));

	    RequestDispatcher dispatcher =  getServletContext().getRequestDispatcher("/security/show_code.jsp");

	    
	    try {
		dispatcher.forward(request , response);
	    } catch (ServletException se) {
		// couldnt forward so have some default message
		ServletOutputStream out = response.getOutputStream();

		PrintWriter outP = new PrintWriter(out);

		outP.println("<pre>");
		outP.print(request.getQueryString());
		outP.print("&code=");
		outP.println(evaluatedCode);
		outP.println("</pre>");
		outP.flush();
	    }

	} catch (TimeIndexException tie) {
	    // the selection and code generation failed
	    request.setAttribute("exception", tie);

	    RequestDispatcher dispatcher =  getServletContext().getRequestDispatcher(selectExceptionPage);

	    try {
		dispatcher.forward(request , response);
	    } catch (ServletException se) {
		// couldnt forward so have some default message
	    
		ServletOutputStream out = response.getOutputStream();
	    
		PrintWriter outP = new PrintWriter(out);

		outP.print("Failed to open index ");
		outP.println(request.getParameter("file"));
		outP.flush();
	    }
	}
	    
	properties.clear();
    }

    /**
     * Set the content type.
     */
    protected void setContentType(HttpServletResponse response, IndexProperties properties) {
	setContentType("text/html");
    }

}
