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



// ArgArrayConverter.java

package com.timeindexing.appl;

import com.timeindexing.index.IndexProperties;

/**
 * This class is used by applications to do processing
 * of an arg array in order to get an IndexProperties
 * object that can be used by a SelectionProcessor.
 */
public class ArgArrayConverter {
    /**
     * Construct an ArgArrayConverter.
     */
    public ArgArrayConverter() {
    }

    /**
     * Process and convert the argument array and build an IndexProperties object.
     * The IndexProperties object willbe suitable for use by a SelectionProcessor.
     */
    public IndexProperties convert(String[] args) {
	int argc = 0;
	boolean startDone = false;
	boolean endDone = false;
	IndexProperties properties = new IndexProperties();


	while (argc< args.length) {
	    if (args[argc].charAt(0) == '-') {		// its a flag
		if (args[argc].equals("-sp")) {
		    if (startDone) {
			System.err.println("An interval start already processed");
		    } else {
			properties.put("startpos", args[argc+1]);
			startDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-st")) {
		    if (startDone) {
			System.err.println("An interval start already processed");
		    } else {
			properties.put("starttime", args[argc+1]);
			startDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-ep")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.put("endpos", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-et")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.put("endtime", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-c")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.put("count", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-f")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.put("for", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-n")) {
		    properties.put("newline", "true");
		    // skip to next arg
		    argc++;
		}
	    } else {
		properties.put("indexpath", args[argc]);
		// skip to next arg
		argc++;
	    }
	}

	return properties;
    }
}
