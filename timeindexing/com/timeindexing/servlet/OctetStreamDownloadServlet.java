Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
