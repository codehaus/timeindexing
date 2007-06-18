// MP3DownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.timeindexing.index.IndexProperties;

/**
 * This servlet returns downloads MP3 data.
 * <p>
 * The mime type of the reposnse is "audio/x-mpeg".
 * <br>
 * The associate file name is "download.mp3".
 */
public class MP3DownloadServlet extends SelectServlet {
    /**
     * Get the content type for this response.
     */
    protected String getContentType() {
	return "audio/mpeg";  // was x-mpeg
    }

    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	String base = super.fileNameGenerator(properties);
	String modified = base.replace('.', '%');
	return modified + ".mp3";
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename(HttpServletRequest request, HttpServletResponse response, IndexProperties properties) {
	response.setHeader("Content-Disposition", "inline; filename=" + fileNameGenerator(properties));;
    }

}
