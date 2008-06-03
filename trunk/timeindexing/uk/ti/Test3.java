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
import com.timeindexing.index.IncoreIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.DataType;
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
 * First test of IncoreIndex
 */
public class Test3 {
    public static void main(String [] args) {
	try {
	    GregorianCalendar calendar = new GregorianCalendar();

	    IndexView index = null;

	    // needs to be told to create itself
	    Properties properties = new Properties();
	    properties.put("name" , "index-Test3");

	    TimeIndexFactory factory = new TimeIndexFactory();
	    index = factory.create(IndexType.INCORE, properties);

	    /* Item 0 */

	    // ZERO timestamp
	    Timestamp dTS = new ElapsedMillisecondTimestamp();
	    // now
	    Timestamp rTS = null;

	    // A chunk of data
	    DataItem data = null;
	    long id = 0;
	    List annotations = null;
	    IndexItem item = null;


	    /* Item 0 */
	    data = new StringItem("quite a lot of stuff");

	    rTS = new MillisecondTimestamp();

	    index.addItem(data, dTS);

	    try {
		Thread.sleep(100);
	    } catch (java.lang.InterruptedException ie) {
		;
	    }

	    /* Item 1 */

	    data = new StringItem("on item 1");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);


	    try {
		Thread.sleep(100);
	    } catch (java.lang.InterruptedException ie) {
		;
	    }

	    /* Item 2 */

	    data = new StringItem("this is the voice of the mysterons");

	    // work out elasped time
	    dTS = TimeCalculator.elapsedSince(rTS);

	    index.addItem(data, dTS);

	    printIndex(index);

	    index.close();

	} catch (TimeIndexException ice) {
	    System.err.println("Test3: " + ice.getMessage());
	    System.exit(1);
	}

    }

    public static void printIndex(Index index) throws TimeIndexException {
	System.out.print("Name: " + index.getName() + "\t");
	System.out.print("Start: " + index.getStartTime() + " ");
	System.out.print("End: " + index.getEndTime() + " ");

	System.out.println("Length: " + index.getLength() + " items");

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
}

