// Log1

package uk.ti;

import com.timeindexing.log.*;
import java.util.BitSet;
import java.io.*;

public class Log1 {
    public static void main(String [] args) {
	Logger logger = new Logger();

	logger.addOutput(System.out);

	// make system.out accept messages with bit 1 set
	logger.getMaskForOutput(System.out).set(1);

	
	PrintStream ofile = null;

	try {
	    ofile = new PrintStream(new BufferedOutputStream(new FileOutputStream("/tmp/log.out")), true);
	    logger.addOutput(ofile);

	    // make system.out accept messages with bit 1 set
	    logger.getMaskForOutput(ofile).set(1);
	    logger.getMaskForOutput(ofile).set(3);
	} catch (IOException ioe) {
	    System.err.println("Cant open /tmp/log.out " + ioe);
	}

	// set the message mask to 1
	BitSet msgMaskA = new BitSet();
	msgMaskA.set(1);

	// set the message mask to 3
	BitSet msgMaskB = new BitSet();
	msgMaskB.set(3);

	// now log a message using msgMask
	logger.log(msgMaskA, "this is msg 1\n");

	logger.log(msgMaskA, "this is msg 2\n");

	logger.log(msgMaskB, "I have mask with 3\n");


	logger.removeOutput(System.out);
	logger.removeOutput(ofile);

	logger.log(msgMaskA, "You wont see this");

    }
}
