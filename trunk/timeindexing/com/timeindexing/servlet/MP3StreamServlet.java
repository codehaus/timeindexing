// MP3StreamServlet.java

package com.timeindexing.servlet;

import com.timeindexing.index.IndexProperties;
import com.timeindexing.time.Clock;
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

    public int doPlayBack(HttpServletRequest request, HttpServletResponse response, IndexProperties properties, int passedInStatus)
    throws IOException {
	ServletOutputStream out = response.getOutputStream();
	String contextName = request.getContextPath();


	String ua = request.getHeader("User-Agent").toLowerCase();

	setContentType(request, response, "audio/x-mpegurl");

	setFilename(request, response, properties);

	int port = request.getServerPort();

	out.println("http://" + request.getServerName() + (port==80? "" : ":"+port) + contextName + "/MP3DownloadServlet?" + encode(request.getQueryString()) + "&style=download");

	return passedInStatus;
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename(HttpServletRequest request, HttpServletResponse response, IndexProperties properties) {
	response.setHeader("Content-Disposition", "inline; filename=" + fileNameGenerator(properties));;
    }

    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	return "download.m3u";
    }

    /**
     * Some URL encoding.
     */
    protected String encode(String httpdata) {
	return httpdata.replaceAll(" ", "+");
    }
}
