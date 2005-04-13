// FlashStreamDownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet returns downloads FlashStream data.
 * <p>
 * The mime type of the reposnse is "audio/x-mpeg",
 * unless the browser is IE, then reposnse is "audio/mp3".
 * <br>
 * The associate file name is "download.mp3".
 */
public class FlashStreamDownloadServlet extends SelectServlet {
    // is the browser Internet Explorer
    boolean browserIsIE = false;

    /**
     * Playback the data.
     */
    protected void doPlayBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
	String user_agent = request.getHeader("user-agent");

	// if user_agent matches MSIE do slightly different things
	if (user_agent.matches(".*MSIE.*")) {
	    browserIsIE = true;
	}

	System.err.print(this.getClass().getName() + ": user_agent: = " + user_agent);
	System.err.println(" IE?: " + browserIsIE);

	// now do usual playback
	super.doPlayBack(request, response);
    }

    /**
     * Set the content type.
     */
    protected void setContentType() {
	if (browserIsIE) {	// do special IE processing
	    setContentType("audio/mp3");
	    response.setContentLength(100000000);
	} else {
	    setContentType("audio/x-mpeg");
	}
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename() {
	setFilename("download.mp3");
    }
}
