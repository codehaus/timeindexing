Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// FlashStreamDownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.timeindexing.index.IndexProperties;

/**
 * This servlet returns downloads FlashStream data.
 * <p>
 * The mime type of the reposnse is "audio/x-mpeg",
 * unless the browser is IE, then reposnse is "audio/mp3".
 * <br>
 * The associate file name is "download.mp3".
 */
public class FlashStreamDownloadServlet extends SelectServlet {
    /**
     * Playback the data.
     */
    protected int doPlayBack(HttpServletRequest request, HttpServletResponse response, IndexProperties properties, int passedInStatus) throws IOException {
	// now do usual playback
	return super.doPlayBack(request, response, properties, passedInStatus);
    }

    /**
     * Set the content type.
     */
    protected void setContentType(HttpServletRequest request, HttpServletResponse response, String type) {
	// we need to do special processing for Flash embedded in IE
	if (type.equals("audio/x-mpeg")) {
	    // is the browser Internet Explorer
	    boolean browserIsIE = false;

	    String user_agent = request.getHeader("user-agent");

	    // if user_agent matches MSIE do slightly different things
	    if (user_agent.matches(".*MSIE.*")) {
		browserIsIE = true;
	    }

	    System.err.print(this.getClass().getName() + ": user_agent: = " + user_agent);
	    System.err.println(" IE?: " + browserIsIE);

	    if (browserIsIE) {	// do special IE processing
		super.setContentType(request, response, "audio/mp3");
		response.setContentLength(100000000);
	    } else {
		setContentType(request, response, "audio/x-mpeg");
	    }
	} else {
	    super.setContentType(request, response, type);
	}
    }

    /**
     * Get the content type
     */
    protected String getContentType() {
	return "audio/x-mpeg";
    }


    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	String base = super.fileNameGenerator(properties);
	return base + ".mp3";
    }
}
