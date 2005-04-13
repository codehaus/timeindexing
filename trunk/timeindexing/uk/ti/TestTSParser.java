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

	timeStr = "2002/6/7";
	System.out.print(timeStr + " => Abs ");
	System.out.println(timeParser.parse(timeStr, true));
	System.out.print(timeStr + " => Ela ");
	System.out.println(timeParser.parse(timeStr, false));



	System.out.print("\n");

    }
}







