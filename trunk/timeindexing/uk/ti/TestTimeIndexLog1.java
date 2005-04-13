package uk.ti;

import com.timeindexing.index.*;
import java.io.OutputStream;

/**
 * First test of TimeIndexLog
 */
public class TestTimeIndexLog1 {
    public static void main(String [] args) throws TimeIndexException {
	new TestTimeIndexLog1();
    }

    public TestTimeIndexLog1() throws TimeIndexException {
	TimeIndex index = null;

	System.out.println("CREATE");
	index = (TimeIndex)TimeIndexLog.createIndex("/tmp/", "test-TIL-1");
	printIndex(index, System.out);

	System.out.println("OPEN");
	index = (TimeIndex)TimeIndexLog.openIndex("/tmp/", "test-TIL-1");
	printIndex(index, System.out);

	System.out.println("ADD");
	TimeIndexLog.addEntry(index, "<one>");
	printIndex(index, System.out);


	System.out.println("CLOSE");
	TimeIndexLog.closeIndex(index);
	printIndex(index, System.out);

	System.out.println("OPEN");
	index = (TimeIndex)TimeIndexLog.openIndex("/tmp/", "test-TIL-1");
	printIndex(index, System.out);

	System.out.println("ADD");
	TimeIndexLog.addEntry(index, "<two>");
	printIndex(index, System.out);

	System.out.println("CLOSE");
	TimeIndexLog.closeIndex(index);

	
    }

    /**
     * Print an index to the OutputStream. From TIHeader.
     */
    protected void printIndex(Index index, OutputStream out) {
	StringBuffer buf = new StringBuffer(512);

	buf.append("Name: \"" + index.getName() + "\"");
	buf.append("  ID: " + index.getID());
	buf.append("  Length: " + index.getLength() + " items, ");
	buf.append("\n");

	String indexPath = index.getIndexPathName();

	if (indexPath != null) {
	    buf.append("Index Path: \"" + indexPath + "\" ");
	}

	String dataPath = index.getDataPathName();

	if (dataPath != null) {
	    buf.append("Data Path: \"" + dataPath + "\" ");
	}
	buf.append("\n");

	
	buf.append("Start:  " + index.getStartTime() + " ");
	buf.append(" End: " + index.getEndTime() + " ");

	buf.append("\n");

	buf.append("First:  " + index.getFirstTime() + " ");
	buf.append("Last:  " + index.getLastTime() + " ");

	buf.append("\n");

	buf.append("FirstD: " + index.getFirstDataTime() + " ");
	buf.append("LastD: " + index.getLastDataTime() + " ");
	buf.append("\n");

	boolean terminated = index.isTerminated();
	buf.append("Terminated: " + (terminated ? "true" : "false"));
	buf.append("\t");

	long dataSize = index.getDataSize();
	buf.append("DataSize: " + (dataSize == 0 ? "variable" : ""+dataSize));
	buf.append("\n");
	try {
	    out.write(buf.toString().getBytes());
	} catch (java.io.IOException ioe) {
	    ;
	}
    }
}

