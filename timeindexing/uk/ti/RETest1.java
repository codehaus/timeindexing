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
// RETest1.java

package uk.ti;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import java.io.*;
import java.util.regex.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;
/**
 * First test of 1.4 regexps.
 */
public class RETest1 {
    public static void main(String [] args) {
	if (args.length == 1) {
	    // have file name,
	    new RETest1(args[0]);
	} else if (args.length == 0) {
	    new RETest1();
	} else {
	     help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("RETest1 [<file>]");
    }

    // The regexp pattern
    String pattern = "\\[.*\\]";

    // A Date format for the following: [11/Aug/1999:18:07:42 +0100]
    SimpleDateFormat format = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss zzzz]");

    /**
     * Build a RETest1 object with an input filename.
     * If the input filename is "-", use stdin.
     */
    public RETest1(String inputFileName) {
	InputStream input = null;

	if (inputFileName.equals("-")) {
	    input = System.in;
	} else {
	    try {
		input = new FileInputStream(new File(inputFileName));
	    } catch (FileNotFoundException fnfe) {
		System.err.println("Could not open input file: " + inputFileName);
		System.exit(0);
	    }
	}

	find(pattern, input);
    }

    /**
     * Build a RETest1 object with stdin
     */
    public RETest1() {
	InputStream input = null;

	input = System.in;

	find(pattern, input);
    }

    /**
     * Fin the pattern in the InputStream
     */
    public boolean find(String pattern, InputStream input) {
	
	if (input == null) {
	    throw new NullPointerException("Input is null in RETest1::find()");
	}

	// Regexp set up
	Pattern compPat = Pattern.compile(pattern);
	Matcher matcher = compPat.matcher("");

	// allocate local objs
	String line = null;
	int lineNo = 0;
	String match = null;
	BufferedReader buffered = null;

	format.setLenient(true);
	format.setTimeZone(TimeZone.getTimeZone("GMT"));

	try {
	    buffered = new BufferedReader (new InputStreamReader(input));

	    while ((line = buffered.readLine()) != null) {
		lineNo++;
		matcher.reset(line);

		if (matcher.find()) {
		    match = matcher.group();
		    System.out.print(match);

		    Date date = format.parse(match);
		    System.out.print(". Date = " + date);

		    Timestamp timestamp = new MillisecondTimestamp(date.getTime());
		    System.out.print(". Timestamp = " + timestamp);
		    System.out.println();
		}
	    }
	} catch (IOException ioe) {
	    System.err.println("IOException in line " + lineNo);
	} catch (java.text.ParseException pe) {
	    System.err.println("Bad date " + match + " on line " + lineNo);
	}

	return true;
    }
}
