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

