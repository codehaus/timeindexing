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
