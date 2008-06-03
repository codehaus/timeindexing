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
// Log3

package  uk.ti;

import com.timeindexing.log.*;
import java.util.BitSet;
import java.io.*;

public class Log3 {
    public static void main(String [] args) {
	Logger logger = new Logger();

	// set up logger output
	// stdout
	logger.addOutput(System.out);

	// stderr
        rebindStderr(logger);

	PrintStream afile = outputFile("/tmp/bl.out", logger);
System.out.println("reset stderr");

	// make system.out accept messages with bit 1 set
	// and with bit 1 set
	logger.getMaskForOutput(System.out).set(1);
	logger.getMaskForOutput(System.out).set(3);

	// this should get stderr messages as well
	logger.getMaskForOutput(afile).set(2);

	// set the message mask to 1
	BitSet msgMaskA = new BitSet();
	msgMaskA.set(1);

	// set the message mask to 3
	BitSet msgMaskB = new BitSet();
	msgMaskB.set(3);

	// now log a message using msgMask
	logger.log(msgMaskA, "this is msg 1\n");

	System.err.println("Will it come out");

	logger.log(msgMaskA, "this is msg 2\n");

	logger.log(msgMaskB, "I have mask with 3\n");

	System.err.println("I am stderr");


    }


    public static PrintStream rebindStderr(Logger logger) {
	PrintStream stderr = null;

	// output to stderr
	stderr = new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.err)), true);
	logger.addOutput(stderr);
	// set the message mask to 2
	BitSet stderrM = new BitSet();
	stderrM.set(2);

	logger.setMaskForOutput((BitSet)stderrM.clone(), stderr);

        // patch System.err to use logger
	LoggingOutputStream os = new LoggingOutputStream(logger);
	// set this mask for the stream
	os.setMask((BitSet)stderrM.clone());

	System.setErr(new PrintStream(os,true));

        return stderr;

    }

    public static PrintStream outputFile(String filename, Logger logger) {
	PrintStream ofile = null;

	try {
	    ofile = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)), true);
	    logger.addOutput(ofile);

	    // make system.out accept messages with bit 1 set
	    //logger.setMaskForOutput(mask, ofile);
	} catch (IOException ioe) {
	    System.err.println("Cant open " + filename + ioe);
	}
	return ofile;
    }
}
