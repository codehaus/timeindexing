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
