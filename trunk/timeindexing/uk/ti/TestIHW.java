package uk.ti;

import com.timeindexing.io.IndexHeaderImpl;

import java.io.IOException;

/**
 * First test of IndexHeaderImpl
 */
public class TestIHW {
    public static void main(String [] args) throws IOException {
	String headerName = null;

	if (args.length == 1) {
	    headerName = args[0];
	} else {
	    help();
	    System.exit(1);
	}
	    

	IndexHeaderImpl header = new IndexHeaderImpl(headerName);

	header.open();

	header.write();

	header.close();

	System.out.println(header);
    }

    public static void help() {
	System.err.println("TestIHW inheader");
    }
}

