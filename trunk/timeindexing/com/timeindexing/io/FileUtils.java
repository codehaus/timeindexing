// FileUtils.java

package com.timeindexing.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A class with a set of file utiltiies.
 */
public class FileUtils {
    /**
     * Resolve a filename given a filename
     * and an extension.
     * @param filename The filename
     * @param extension The extension
     * @return null if a name cant be resolved.
     */
    public static String resolveFileName(String origFilename, String extension) {
	if (origFilename == null) {
	    return null;
	} else if (extension == null) {
	    // it's fine as-is
	    return origFilename;
	} else if (origFilename.endsWith(extension)) {
	    // it's fine as-is
	    return origFilename;
	} else {
	    // A pattern to match .ti? files
	    String pattern = "\\.ti?";
	    // the compiled regexp
	    Pattern compPat = Pattern.compile(pattern);
	    // a matcher
	    Matcher matcher = compPat.matcher("");

	    // pass the matcher the  filename
	    matcher.reset(origFilename);

	    // does the filename match the pattern
	    if (matcher.find()) {
		// the filename ends in .ti?
		// so strip off that part
		// and add the extension
		String newFileName = origFilename.substring(0, matcher.start(0)) + extension;

		/*
		System.err.print("Found errant filename");
		System.err.print(". Filename length = " + origFilename.length());
		System.err.print(". Match start = " + matcher.start(0));
		System.err.print(". Match value = " + matcher.group());
		System.err.print(". New filename = " + newFileName);
		System.err.println();
		*/

		return newFileName;
	    } else {
		// its some other name
		// so return that with the extension
		String newFileName = origFilename + extension;
		return newFileName;
	    }
	}

    }

}
