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
// SelectionCode.java

package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import com.timeindexing.appl.ArgArrayConverter;
import com.timeindexing.appl.SelectionCodeEvaluator;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Test 1 of SelectionCodeEvaluator.
 */
public class SelectionCode1 {
    public static void main(String [] args) {
	/*
	 * Process args
	 */
	IndexProperties selectionProperties = null;

	ArgArrayConverter argConverter = new ArgArrayConverter();
	
	selectionProperties = argConverter.convert(args);

	String filename = (String)selectionProperties.get("indexpath");

	SelectionCodeEvaluator codeChecker = new SelectionCodeEvaluator(filename);

	/*
	 * Go for it
	 */
	try {
	    long evaluatedCode = codeChecker.evaluate(selectionProperties);

	    System.out.println(evaluatedCode);

	} catch (TimeIndexException tie) {
	    System.err.println("SelectionCode1: error " + tie.getMessage());
	} catch (IOException ioe) {
	    System.err.println("SelectionCode1: error " + ioe.getMessage());
	}
    
    }
}
