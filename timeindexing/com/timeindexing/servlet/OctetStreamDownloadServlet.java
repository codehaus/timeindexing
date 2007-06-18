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
     * Get the content type for this response.
     */
    protected String getContentType() {
	return "application/octet-stream";
    }

    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	String base = super.fileNameGenerator(properties);
	return base + ".bin";
    }

}
