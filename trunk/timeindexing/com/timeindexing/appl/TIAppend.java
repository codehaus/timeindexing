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
// TIAppend.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexFactoryException;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexSpecificationException;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.time.Timestamp;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ReaderResultItem;
import com.timeindexing.plugin.ReaderPlugin;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.plugin.InputPlugin;
import com.timeindexing.plugin.DefaultInputPlugin;
import com.timeindexing.plugin.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Append to a timeindex file from a file or stdin.
 * <p>
 * Args are
 * -p plugin, one of line, web, mail, ftp, file
 * -c plugin class, e.g. com.timeindexing.plugin.FileItem
 * index file
 * input file, use - for stdin
 */
public class TIAppend {
    /**
     * Entry point 
     */
    public static void main(String [] args) {
	/*
	 * Handle broken pipes properly
	 */
	final Signal sigpipe = new Signal("PIPE");

	SignalHandler handler = new SignalHandler () {
		public void handle(Signal sig) {
		    System.exit(sigpipe.hashCode());
		}
	    };
	Signal.handle(sigpipe, handler);

	// local vars
	String plugin = null;
	String pluginClass = null;

	int argc = 0;

	if (args.length == 0) {	    
	    help(System.err);
	    System.exit(1);	
	}

	// process flags
	while  (argc<args.length) {
	    if (args[argc].startsWith("-")) {    // it's a flag
		// have specific flag
		if (args[argc].equals("-c")) {
		    pluginClass = args[argc + 1];
		    argc += 2;

		}  else if (args[argc].equals("-p")) {
		    plugin =  args[argc + 1];
		    argc += 2;
		} 
	    } else {
		break;
	    }
	}

	// Get file args
	String indexPath = null;
	String fileName = null;

	if (argc != args.length-2) {
	    // there's not enough args
	    help(System.err);
	    System.exit(1);	
	} else {
	    indexPath = args[argc];
	    fileName = args[argc+1];
	    
	    // make a few more checks

	    if (plugin == null && pluginClass == null) {
		System.err.println("Specify a plugin or a plugin class");
		help(System.err);
		System.exit(1);
	    }

	    // setup the IndexProperties
	    IndexProperties appendProperties = new IndexProperties();
	    appendProperties.putProperty("plugin", plugin);
	    appendProperties.putProperty("pluginClass", pluginClass);

	    //we're ready to go
	    TIAppend appender = new TIAppend(appendProperties, indexPath, fileName);
	    appender.append();
	}
    }

    /**
     * Print out some help
     */
    public static void help(PrintStream out) {
	out.println("tiappend [-p line|web|mail|ftp|file] [-c plugin_class] <tifile> [<file> | -]");
    }

    /**
     * Object instance vars
     */
    IndexView index = null;
    InputStream input = null;
    ReaderPlugin plugin = null;
    String inputFileName = null;

    /**
     * Build a TIAppend object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public TIAppend(IndexProperties properties, String tiFileName, String inputFileName) {
	File inputFile = null;

	this.inputFileName = inputFileName;

	if (inputFileName.equals("-")) {
	    input = System.in;
	} else {
	    try {
		inputFile = new File(inputFileName);
		input = new FileInputStream(inputFile);
	    } catch (FileNotFoundException fnfe) {
		System.err.println("Could not open input file: " + inputFileName);
		System.exit(2);
	    }
	}

	if (setup(properties, tiFileName, input) == false) {
	    throw new Error("setup failed");
	}
    }

    /**
     * Append the output.
     */
    public boolean setup(IndexProperties setupProperties, String tiFileName, InputStream input) {
	/*
	 * first setup the Properties to append and allocate the TimeIndex.
	 */

	Properties indexProperties = new Properties();

	indexProperties.setProperty("indexpath", tiFileName);


	// try and allocate an index
	try {
	    index = allocate(indexProperties);

	} catch (TimeIndexException tie) {
	    System.err.println("Problem with index \"" + indexProperties.getProperty("indexpath") + "\"");
	    tie.printStackTrace(System.err);
	    
	    return false;  // this keeps the compiler happy

	} 

	
	/*
	 * set up plugin based on a setup  property
	 */

	String pluginname = (String)setupProperties.getProperty("plugin");
	String className = (String)setupProperties.getProperty("pluginClass");

	if (pluginname == null && className == null) {
	    throw new IllegalStateException("Got pluginname == null && className == null in setup()");

	} else  if (pluginname != null && className != null) {
	    throw new IllegalStateException("Got pluginname != null && className != null in setup()");

	} else if (pluginname != null) {
	    // we got given a plugin name
	    if (pluginname.equals("line")) {
		className  = "com.timeindexing.plugin.Line";
	    } else 	if (pluginname.equals("web")) {
		className = "com.timeindexing.module.logging.WebServerLogLine";
	    } else 	if (pluginname.equals("mail")) {
		className = "com.timeindexing.module.logging.MailServerLogLine";
	    } else 	if (pluginname.equals("ftp")) {
		className = "com.timeindexing.module.logging.FtpServerLogLine";
	    } else if (pluginname.equals("file")) {
		className  = "com.timeindexing.plugin.FileItem";
	    } else 	if (pluginname.equals("block")) {
		className  = "com.timeindexing.plugin.Block";
		//((Block)plugin).setBlockSize(16);
	    } else {
		System.err.println("Got unknown pluginname " + pluginname);
		return false;
	    }


	    try {
		Class aClass = Class.forName(className);
		//System.err.println("Class = " + aClass.getName());

		Class[] types = { FileInputStream.class };
		Constructor constructor = aClass.getConstructor(types);
		//System.err.println("Constructor = " + constructor.getName());

		Object[] args = { input };
		plugin = (ReaderPlugin)constructor.newInstance(args);
	    } catch (Exception e) {
		System.err.println(e.getMessage());	
		return false;
	    }


	    return true;

	} else {
	    // we got given a plugin class name

	    try {
		Class aClass = Class.forName(className);
		//System.err.println("Class = " + aClass.getName());

		Class[] types = { FileInputStream.class };
		Constructor constructor = aClass.getConstructor(types);
		//System.err.println("Constructor = " + constructor.getName());

		Object[] args = { input };
		plugin = (ReaderPlugin)constructor.newInstance(args);
	    } catch (Exception e) {
		System.err.println(e.getMessage());	
		return false;
	    }

	    return true;

	}
    }

    /**
     * Actually append the index.
     */
    public boolean append() {
	DataItem item = null;
	ReaderResult result = null;
	Timestamp dataTS = null;



	InputPlugin inputPlugin = new DefaultInputPlugin(index, input, plugin);


	// do stuff
	try {
	    long indexSize = 0;

	    // activate the index
	    index.activate();

	    while ((result = inputPlugin.read()) != null) {
		dataTS = result.getDataTimestamp();
		item = new ReaderResultItem(result);

		index.addItem(item, dataTS);

		indexSize = index.getLength();
	    }

	    // wind it up
	    index.close();

	} catch (TimeIndexException tie) {
	    System.err.println("Problem with index \"" + index.getName() + "\"");
	    tie.printStackTrace(System.err);
	    
	    return false;  // this keeps the compiler happy

	}  catch (IOException ioe) {
	    System.err.println("Read error on input");
	}

	try {
	    //buffered.close();
	    input.close();
	} catch (IOException ioe) {
	    System.err.println("Failed to close input");
	}
	 
	//index.close();

	return true;

}

    /**
     * Allocate an View onto an Index
     */
    private IndexView allocate(Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException,  IndexOpenException {
	TimeIndexFactory factory = new TimeIndexFactory();

	return factory.append(indexProperties);
    }

}
