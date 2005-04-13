package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.DataType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexFactoryException;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.index.IndexSpecificationException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ReaderResultItem;
import com.timeindexing.plugin.ReaderPlugin;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.plugin.*;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.*;

/**
 * Create a timeindex file from a file or stdin.
 */
public class TICreate {
    File inputFile = null;
    String inputFileName = null;
    String type = null;

    public static void main(String [] args) {
	if (args.length == 2) {
	    // have tifile name and input file name
	    new TICreate(args[0], args[1]);
	} else if (args.length == 1) {
	    // have tifile name only, assume input is stdin
	    new TICreate(args[0]);
	} else {
	    help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("ticreate <tifile> [<file>]");
    }

    /**
     * Build a TICreate object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public TICreate(String tiFileName, String inputFileName) {
	InputStream input = null;
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	this.inputFileName = inputFileName;

	if (inputFileName.equals("-")) {
	    input = System.in;
	} else {
	    try {
		inputFile = new File(inputFileName);
		input = new FileInputStream(inputFile);
	    } catch (FileNotFoundException fnfe) {
		System.err.println("Could not open input file: " + inputFileName);
		System.exit(0);
	    }
	}

	create(tiFileName, input);
    }

    /**
     * Build a TICreate object with a timeindex filename only
     */
    public TICreate(String tiFileName) {
	InputStream input = null;

	input = System.in;

	create(tiFileName, input);
    }

    /**
     * Build a TICreate object with a timeindex file name and an input stream
     */
    public TICreate(String tiFileName, InputStream input) {
	create(tiFileName, input);
    }

    /**
     * Allocate an View onto an Index
     */
    public IndexView allocate(Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException,  IndexCreateException {
	TimeIndexFactory factory = new TimeIndexFactory();

	IndexType indexType = null;

	if (type == null) {
	    throw new Error("Set system propery -Dindextype=[inline|external|shadow]");
	} else if (type.equals("inline")) {
	    indexType = IndexType.INLINE_DT;
	} else if (type.equals("external")) {
	    indexType = IndexType.EXTERNAL_DT;
	} else if (type.equals("shadow")) {
	    if (inputFileName.equals("-")) {
		throw new Error("Index can;t shadow stdin.  Use a filename");
	    } else {
		indexType = IndexType.SHADOW_DT;
	    }
	} else {
	   throw new Error("Set system propery -Dindextype=[inline|external|shadow]");
	} 

	return factory.create(indexType, indexProperties);
    }

    /**
     * Create the output.
     */
    public boolean create(String tiFileName, InputStream input) {
	if (input == null) {
	    throw new NullPointerException("Input is null in TICreate::create()");
	}

	// set up the Index based on system property
	// make it "external", if nothing is set
	type = System.getProperty("indextype", "external");

	// allocate local objs
	//BufferedReader buffered = new BufferedReader (new InputStreamReader(input));

	Properties indexProperties = new Properties();

	indexProperties.setProperty("indexpath", tiFileName);
	if (inputFileName != null && type.equals("shadow")) {
	    indexProperties.setProperty("datapath", inputFileName);
	} else {
	    indexProperties.setProperty("datapath", tiFileName);
	}

	indexProperties.setProperty("name", (inputFile == null ? "ticreate" : inputFile.getName()));

	try {
	    IndexView index = allocate(indexProperties);

	    DataItem item = null;

	    ReaderPlugin plugin = null;
	    ReaderResult result = null;
	    Timestamp dataTS = null;

	    // set up plugin based on system property
	    String pluginname = System.getProperty("plugin");

	    if (pluginname == null) {
		plugin = new Line(input);
	    } else if (pluginname.equals("line")) {
		plugin = new Line(input);
	    } else 	if (pluginname.equals("web")) {
		plugin = new  WebServerLogLine(input);
	    } else 	if (pluginname.equals("mail")) {
		plugin = new  MailServerLogLine(input);
	    } else 	if (pluginname.equals("ftp")) {
		plugin = new  FtpServerLogLine(input);
	    } else 	if (pluginname.equals("block")) {
		plugin = new  Block(input);
		((Block)plugin).setBlockSize(16);
	    } else if (pluginname.equals("file")) {
		plugin = new FileItem((FileInputStream)input);
	    } else if (pluginname.equals("mp3")) {
		plugin = new MP3(input);
	    } else {
		plugin = new Line(input);
	    }

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
	} catch (TimeIndexException tie) {
	    System.err.println("Problem with index \"" + indexProperties.getProperty("indexpath") + "\"");
	    tie.printStackTrace(System.err);
	    
	    return false;  // this keeps the compiler happy

	} 

    }
}
