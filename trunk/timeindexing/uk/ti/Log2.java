// Log2

package uk.ti;

import com.timeindexing.log.*;
import java.util.BitSet;
import java.io.*;

public class Log2 {
    public static void main(String [] args) {
	Logger logger = new Logger();

	// set up logger output
	logger.addOutput(System.out);

        // patch System.err to use logger
	LoggingOutputStream os = new LoggingOutputStream(logger);
	// set this mask for the special output stream
	BitSet osb = new BitSet();

	// clear it
	osb.xor(osb);

	// set the message mask to 3
	BitSet stderrM = new BitSet();
	stderrM.set(3);

	// now set bits from msgMaskB
	osb.or(stderrM);

	os.setMask(osb);

	System.setErr(new PrintStream(os,true));

System.out.println("reset stderr");

	// make system.out accept messages with bit 1 set
	// and with bit 1 set
	logger.getMaskForOutput(System.out).set(1);
	logger.getMaskForOutput(System.out).set(3);

	// set the message mask to 1
	BitSet msgMaskA = new BitSet();
	msgMaskA.set(1);

	// set the message mask to 3
	BitSet msgMaskB = new BitSet();
	msgMaskB.set(3);

System.out.println("maskA = " + msgMaskA);
System.out.println("maskB = " + msgMaskB);
System.out.println("OS bm = " + os.getMask());


	// now log a message using msgMask
	logger.log(msgMaskA, "this is msg 1\n");

	System.err.println("Will it come out");

	logger.log(msgMaskA, "this is msg 2\n");

	logger.log(msgMaskB, "I have mask with 3\n");

	System.err.println("I am stderr");

	logger.removeOutput(System.out);

    }
}
