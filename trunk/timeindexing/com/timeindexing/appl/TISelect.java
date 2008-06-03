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



// TISelect.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Select a region of TimeIndex to the stdout.
 */
public class TISelect {
    public static void main(String [] args) {
	/*
	 * Handle broken pipes properly
	 */
	/*
	final Signal sigpipe = new Signal("PIPE");

	SignalHandler handler = new SignalHandler () {
		public void handle(Signal sig) {
		    System.exit(sigpipe.hashCode());
		}
	    };
	Signal.handle(sigpipe, handler);
	*/

	/*
	 * Process args
	 */
	Selecter select = null;
	IndexProperties selectionProperties = null;

	ArgArrayConverter argConverter = new ArgArrayConverter();
	
	selectionProperties = argConverter.convert(args);

	select = new Selecter((String)selectionProperties.get("indexpath"), System.out);
	/*
	 * Go for it
	 */
	try {
	    select.select(selectionProperties);
	} catch (TimeIndexException tie) {
	    System.err.println("TISelect: error " + tie.getMessage());
	} catch (IOException ioe) {
	    System.err.println("TISelect: error " + ioe.getMessage());
	}
    
    }

    static void help(PrintStream out) {
	out.println("ticat <tifile>");
    }

}
