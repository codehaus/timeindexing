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
package uk.ti;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
//import com.timeindexing.appl.TimeParser;
import com.timeindexing.time.TimeDateParser;
import java.util.TimeZone;

/**
 * First test of Time Paerser
 */
public class TestTSParser {
    public static void main(String [] args) {
	/*
	System.err.println("TZ = " + TimeZone.getDefault());

	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	System.err.println("TZ = " + TimeZone.getDefault());
	*/

	Timestamp t0 = null;
	TimeDateParser timeParser = new TimeDateParser();


	String timeStr = null;

	timeStr = "120";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "14:00";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));


	timeStr = "20:00:37";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "20:00:37.123";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));


	timeStr = "20:00:37.12345";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));


	timeStr = "20:00:37.12345678";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "20:00:37.12345678987654";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "2002/04/05";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "05/04/2002";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));


	timeStr = "2002/6/7";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "7/6/2002";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));

	timeStr = "31/12/2020";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));



	System.out.print("\n");

    }
}







