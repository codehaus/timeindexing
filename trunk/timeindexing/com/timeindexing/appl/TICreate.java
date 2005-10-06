// TICreate.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexFactoryException;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexCreateException;
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
 * Create a timeindex file from a file or stdin.
 * <p>
 * Args are
 * -t type,  one of external, shadow, inline
 * -e == -t external
 * -s == -t shadow
 * -i == -t inline
 * -p plugin, one of line, web, mail, ftp, file
 * -c plugin class, e.g. com.timeindexing.plugin.FileItem
 * -n index name
 * index file
 * input file, use - for stdin
 */
public class TICreate {
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
	String indexType = null;
	String indexName = null;

	int argc = 0;

	if (args.length == 0) {	    
	    help(System.err);
	    System.exit(1);	
	}

	// process flags
	while  (argc<args.length) {
	    if (args[argc].startsWith("-")) {    // it's a flag
		// have specific flag
		if (args[argc].equals("-e")) {
		    indexType = "external";
		    argc ++;;
		} else if (args[argc].equals("-s")) {
		    indexType = "shadow";
		    argc ++;;
		} else if (args[argc].equals("-i")) {
		    indexType = "inline";
		    argc ++;;
		} else if (args[argc].equals("-p")) {
		    plugin = args[argc + 1];
		    argc += 2;

		} else if (args[argc].equals("-c")) {
		    pluginClass = args[argc + 1];
		    argc += 2;

		}  else if (args[argc].equals("-t")) {
		    indexType =  args[argc + 1];
		    argc += 2;

		}  else if (args[argc].equals("-n")) {
		    indexName =  args[argc + 1];
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
	    // can't shadow stdin
	    if (fileName.equals("-") && indexType.equals("shadow")) {
		System.err.println("Index can;t shadow stdin.  Use a real filename");
		help(System.err);
		System.exit(1);
	    }

	    if (plugin == null && pluginClass == null) {
		System.err.println("Specify a plugin or a plugin class");
		help(System.err);
		System.exit(1);
	    }

	    if (indexType == null) {
		System.err.println("Specify an index type");
		help(System.err);
		System.exit(1);
	    }

	    // setup the IndexProperties
	    IndexProperties createProperties = new IndexProperties();
	    createProperties.putProperty("plugin", plugin);
	    createProperties.putProperty("pluginClass", pluginClass);
	    createProperties.putProperty("indexType", indexType);
	    createProperties.putProperty("indexName", indexName);

	    //we're ready to go
	    TICreate creator = new TICreate(createProperties, indexPath, fileName);
	    creator.create();
	}
    }

    /**
     * Print out some help
     */
    public static void help(PrintStream out) {
	out.println("ticreate [-e] [-s] [-e] [-t external|shadow|inline] [-p line|web|mail|ftp|file] [-c plugin_class] [-n index name] <tifile> [<file> | -]");
    }

    /**
     * Object instance vars
     */
    IndexView index = null;
    InputStream input = null;
    ReaderPlugin plugin = null;
    String inputFileName = null;

    /**
     * Build a TICreate object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public TICreate(IndexProperties properties, String tiFileName, String inputFileName) {
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
     * Create the output.
     */
    public boolean setup(IndexProperties setupProperties, String tiFileName, InputStream input) {
	/*
	 * first setup the Properties to create and allocate the TimeIndex.
	 */

	Properties indexProperties = new Properties();

	indexProperties.setProperty("indexpath", tiFileName);

	if (((String)setupProperties.getProperty("indexType")).equals("shadow")) {
	    indexProperties.setProperty("datapath", inputFileName);
	} else {
	    indexProperties.setProperty("datapath", tiFileName);
	}

	if (((String)setupProperties.getProperty("indexName")) == null) {
	    // no specific name, so create one
	    indexProperties.setProperty("name", tiFileName);
	} else {
	    indexProperties.setProperty("name", (String)setupProperties.getProperty("indexName"));
	}

	// try and allocate an index
	try {
	    index = allocate((String)setupProperties.getProperty("indexType"), indexProperties);

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
		//System.err.println("plugin = " + plugin);
	    } catch (Exception e) {
		System.err.println(e.getClass().getName() + " => " + e.getMessage());	
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
		System.err.println(e.getClass().getName() + " => " + e.getMessage());	
		return false;
	    }

	    return true;

	}
    }

    /**
     * Actually create the index.
     */
    public boolean create() {
	DataItem item = null;
	ReaderResult result = null;
	Timestamp dataTS = null;



	InputPlugin inputPlugin = new DefaultInputPlugin(index, input, plugin);

	// do stuff
	try {
	    long indexSize = 0;

	    while ((result = inputPlugin.read()) != null) {
		dataTS = result.getDataTimestamp();
		item = new ReaderResultItem(result);

		indexSize = index.addItem(item, dataTS);
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
    private IndexView allocate(String type, Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException,  IndexCreateException {
	TimeIndexFactory factory = new TimeIndexFactory();

	IndexType indexType = null;


	if (type == null) {
	    throw new Error("Set system propery -Dindextype=[inline|external|shadow]");
	} else if (type.equals("inline")) {
	    indexType = IndexType.INLINE_DT;
	} else if (type.equals("external")) {
	    indexType = IndexType.EXTERNAL_DT;
	} else if (type.equals("shadow")) {
	    indexType = IndexType.SHADOW_DT;
	} else {
	   throw new Error("Set flag -t [inline|external|shadow]");
	} 

	return factory.create(indexType, indexProperties);
    }

}
