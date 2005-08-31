// OctetStreamDownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.timeindexing.index.IndexProperties;

/**
 * This servlet returns downloads OctetStream data.
 * <p>
 * The mime type of the reposnse is "application/octet-stream"
 * <br>
 * The associate file name is "download.bin".
 */
public class OctetStreamDownloadServlet extends SelectServlet {
    /**
     * Set the content type.
     */
    protected void setContentType(HttpServletResponse response, IndexProperties properties) {
	setContentType("application/octet-stream");
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename(HttpServletResponse response, IndexProperties properties) {
	String generatedName = fileNameGenerator(properties);
	
	setFilename(generatedName + ".bin");
    }
}
