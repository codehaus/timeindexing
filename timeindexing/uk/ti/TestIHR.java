package uk.ti;

import com.timeindexing.io.IndexDecoder;

import java.io.IOException;

/**
 * First test of IndexDecoder
 */
public class TestIHR {
    public static void main(String [] args) throws IOException {
	String headerName = null;

	if (args.length == 1) {
	    headerName = args[0];
	} else {
	    help();
	    System.exit(1);
	}
	    

	IndexDecoder header = new IndexDecoder(headerName);

	header.open();

	header.read();

	header.close();

	System.out.println(header);
    }

    public static void help() {
	System.err.println("TestIHR inheader");
    }
}

