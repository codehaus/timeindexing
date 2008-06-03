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
