/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
