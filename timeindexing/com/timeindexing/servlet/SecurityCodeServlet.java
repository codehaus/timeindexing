/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
    public int doPlayBack(HttpServletRequest request, HttpServletResponse response, IndexProperties properties, int passedInStatus) throws IOException {
	
	setContentType(request, response, "text/html");

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

	return passedInStatus;
    }

    /**
     * Get the content type.
     */
    protected String getContentType() {
	return "text/html";
    }

}
