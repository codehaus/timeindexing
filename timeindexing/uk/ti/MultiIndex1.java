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

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;

/**
 * Test of multiple TimeIndex references to the same underlying index.
 */
public class MultiIndex1 {
    public static void main(String [] args) {
	GregorianCalendar calendar = new GregorianCalendar();

	Properties createProperties = new Properties();
	createProperties.setProperty("name", "index-Test4");

	TimeIndexFactory factory = new TimeIndexFactory();

	try {

	    IndexView index = factory.create(IndexType.INCORE, createProperties);

	    /* Item 0 */

	    // ZERO timestamp
	    Timestamp dTS = new ElapsedMillisecondTimestamp();
	    // now
	    Timestamp rTS = null;

	    // A chunk of data
	    DataItem data = null;


	    /* Item 0 */
	    data = new StringItem("quite a lot of stuff");

	    rTS = new MillisecondTimestamp();

	    index.addItem(data, dTS);

	    delay(100);

	    /* Item 1 */

	    // work out elasped time

	    data = new StringItem("on item 1");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);

	    delay(100);

	    /* Item 2 */

	    data = new StringItem("this is the voice of the mysterons");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);

	    /*
	     * This is where we get another view on the index
	     */
	    delay(200);

	    //createProperties.setProperty("incore", "true");
	    IndexView index2 = factory.open(createProperties);

	    IndexItem anItem = index2.getItem(0);

	    delay(200);

	    System.out.print("Item 0 = ");
	    printIndexItem(anItem);

	    delay(200);

	    /* A few more */
	    for (int few = 0; few < 10; few++) {

		int myDelay = 100 + (few * 10);

		data = new StringItem("delay was " + myDelay);

		delay(myDelay);

		// work out elasped time
		dTS = TimeCalculator.elapsedSince(rTS);

		index.addItem(data, dTS);

	    }

	    delay(50);

	    IndexView index3 = factory.open(createProperties);

	    anItem = index3.getItem(10);

	    delay(200);

	    System.out.print("Item 10 = ");
	    printIndexItem(anItem);

	    delay(200);

	    index3.close();
	    System.err.println("Index3 closed ");


	    index2.close();
	    System.err.println("Index2 closed ");

	    index.close();

	    printIndex(index);

	} catch (TimeIndexException ice) {
	    ice.printStackTrace();
	    System.exit(1);
	}
    }

    public static void printIndex(Index index) throws TimeIndexException {
	System.out.print("Name: " + index.getName() + "\n");
	System.out.print("Start:\t" + index.getStartTime() + "\n");
	System.out.print("First:\t" + index.getFirstTime() + "\n");
	System.out.print("Last:\t" + index.getLastTime() + "\n");
	System.out.print("End:\t" + index.getEndTime() + "\n");

	System.out.println("Length: " + index.getLength() + " items\n\n");

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    printIndexItem(itemN);
	}
	    
	    
    }

    public static void printIndexItem(IndexItem item) throws TimeIndexException {

	System.out.print(item.getDataTimestamp() + "\t");

	System.out.print(item.getIndexTimestamp() + "\t");

	ByteBuffer itemdata = item.getData();
	if (item.getDataSize().value() > 16) {
	    System.out.print(new String(itemdata.array()).substring(0,11) + "....\t");
	} else {
	    System.out.print(new String(itemdata.array()) + "\t");
	}

	System.out.print(item.getDataSize() + "\t");

	System.out.print(item.getItemID() + "\t");

	System.out.print(item.getAnnotationMetaData() + "\n");

    }

    public static void delay(long ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException ie) {
	    ;
	}
    }
}

