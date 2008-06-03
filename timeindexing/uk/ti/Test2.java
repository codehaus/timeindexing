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

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IncoreIndexItem;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.DataType;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.StringItem;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.SID;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.nio.ByteBuffer;

/**
 * First test of IncoreIndexItem.
 */
public class Test2 {
    public static void main(String [] args) {
	GregorianCalendar calendar = new GregorianCalendar();
	// midnight 11/9/2002
	Timestamp t0 = new MillisecondTimestamp(new GregorianCalendar(2002,11,9).getTime());
	// now
	Timestamp t1 = new MillisecondTimestamp(calendar.getTime());
	// An item of data
	DataItem data = new StringItem("quite a lot of stuff");
	ID id = new SID(123456);
	long annotations = 0;

	System.out.println("Data = " + data);

	IndexItem item0 = new IncoreIndexItem(t0,t1,data, DataType.ANY, id, annotations); // not size - calculate it.
	print(item0);

    }

    public static void print(IndexItem item) {

	System.out.print(item.getDataTimestamp() + "\t");

	System.out.print(item.getIndexTimestamp() + "\t");

	ByteBuffer itemdata = item.getData();
	if (item.getDataSize().value() > 12) {
	    System.out.print(new String(itemdata.array()).substring(0,12) + "....\t");
	} else {
	    System.out.print(new String(itemdata.array()) + "\t");
	}

	System.out.print(item.getDataSize() + "\t");

	System.out.print(item.getItemID() + "\t");

	System.out.print(item.getAnnotationMetaData() + "\n");

    }
}

