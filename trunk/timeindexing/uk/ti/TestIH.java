package uk.ti;

import com.timeindexing.io.IndexHeaderImpl;

import java.io.IOException;

/**
 * First test of IndexHeaderImpl
 */
public class TestIH {
    public static void main(String [] args) throws IOException {
	String headerName = null;
	String newHeader = null;

	if (args.length == 2) {
	    headerName = args[0];
	    newHeader = args[1];
	} else {
	    help();
	    System.exit(1);
	}


	IndexHeaderImpl header = new IndexHeaderImpl(headerName);

	header.open();

	header.read();

	header.close();


	System.out.println(header);

	IndexHeaderImpl header2 = null;

	try {
	    header2 = (IndexHeaderImpl)header.clone();
	} catch (CloneNotSupportedException cnse) {
	    cnse.printStackTrace(System.err);
	}

	header2.open(newHeader);

	header2.flush();

	header2.close();

	System.out.println(header2);
    }

    public static void help() {
	System.err.println("TestIH inheader newheader");
    }
}

