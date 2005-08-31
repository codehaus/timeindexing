// MP3StreamServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet returns a response that causes 
 * the receiver to stream MP3 data.
 * <p>
 * The mime type of the reposnse is "audio/x-mpegurl".
 * <br>
 * The associate file name is "download.m3u".
 */
public class MP3StreamServlet extends SelectServlet {

    public void doPlayBack(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
	ServletOutputStream out = response.getOutputStream();
	String contextName = request.getContextPath();


	String ua = request.getHeader("User-Agent").toLowerCase();

	System.err.println(this.getClass().getName() + " User-Agent: " + request.getHeader("User-Agent"));

	response.setContentType("audio/x-mpegurl");

	response.setHeader("Content-Disposition", "inline; filename=" + "download.m3u");

	// Was this.  Changes to fix a bug in IE.
	//response.setHeader("Content-Disposition", "attachment; filename=" + "download.m3u" );

	int port = request.getServerPort();

	out.println("http://" + request.getServerName() + (port==80? "" : ":"+port) + contextName + "/MP3DownloadServlet?" + encode(request.getQueryString()) + "&style=stream");

    }

    /**
     * Some URL encoding.
     */
    protected String encode(String httpdata) {
	return httpdata.replaceAll(" ", "+");
    }
}
