/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package uk.ti;

import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.data.StringItem;
import java.util.Properties;

// for printing
import com.timeindexing.index.IndexItem;
import java.nio.ByteBuffer;


/**
 * Test of TimeIndexFactory and IncoreIndex.
 */
public class TestI1 {
    public static void main(String [] args) {
	TimeIndexFactory factory = new TimeIndexFactory();

	Properties props = new Properties();
	props.setProperty("name", "ex1");

	try {
	    IndexView index = factory.create(IndexType.INCORE, props);

	    index.addItem(new StringItem("hello"));
	    index.addItem(new StringItem("world"));

	    printIndex(index);

	    factory.close(index);
	} catch (TimeIndexException tie) {
	    ;
	}

    }

    public static void printIndex(IndexView index) throws TimeIndexException {
	System.out.print("Name: " + index.getName() + "\n");
	System.out.print("URI: " + index.getURI() + "\n");
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

	System.out.print(item.getIndexTimestamp() + "\t\"");

	ByteBuffer itemdata = item.getData();
	System.out.print(itemdata.toString());

	System.out.print("\"\t" + item.getDataSize() + "\t");

	System.out.print(item.getItemID() + "\t");

	System.out.print(item.getAnnotationMetaData() + "\n");

    }

}
