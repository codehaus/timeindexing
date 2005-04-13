// OctetStreamDownloadServlet.java

package com.timeindexing.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

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
    protected void setContentType() {
	setContentType("application/octet-stream");
    }

    /**
     * Set the filename for downloads.
     */
    protected void setFilename() {
	setFilename("download.bin");
    }
}
