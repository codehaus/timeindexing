// MP3DownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This servlet returns downloads MP3 data.
 * <p>
 * The mime type of the reposnse is "audio/x-mpeg".
 * <br>
 * The associate file name is "download.mp3".
 */
public class MP3DownloadServlet extends SelectServlet {
    /**
     * Set the content type.
     */
    protected void setContentType() {
	setContentType("audio/x-mpeg");
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename() {
	setFilename("download.mp3");
    }
}
