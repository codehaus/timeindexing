// MPEGDownloadServlet.java

package com.timeindexing.servlet;

import com.timeindexing.servlet.SelectServlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.Selecter;
import com.timeindexing.appl.SelecterWithHeader;

/**
 * This servlet returns downloads MPEG2 data.
 * <p>
 * The mime type of the reposnse is "video/mpeg".
 * <br>
 * The associate file name is "download.mpg".
 */
public class MPEGDownloadServlet extends SelectServlet {

    /**
     * Get the content type.
     */
    protected String getContentType() {
	return "video/mpeg";
    }


    /**
     * This filename generator, takes the arguments and generates a useful filename.
     * Returns filename-0:10-to-1:23.
     */
    protected String fileNameGenerator(IndexProperties properties) {
	String base = super.fileNameGenerator(properties);
	return base + ".mpg";
    }
    /**
     * allocate a Selecter
     */
    protected Selecter allocateSelecter(String filename,  OutputStream out) {
	return new SelecterWithHeader(filename, out);
    }

}
