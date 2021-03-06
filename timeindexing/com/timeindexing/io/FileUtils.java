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
     * @param origFilename The filename
     * @param extension The extension
     * @return null if a name cant be resolved.
     */
    public synchronized static String resolveFileName(String origFilename, String extension) {
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

    /**
     * Strip the extention off of a filename.
     * @param filename The filename
     */
    public synchronized static String removeExtension(String filename) {
	if (filename == null) {
	    return null;
	} else {
// A pattern to match .ti? files
	    String pattern = "\\.ti?";
	    // the compiled regexp
	    Pattern compPat = Pattern.compile(pattern);
	    // a matcher
	    Matcher matcher = compPat.matcher("");

	    // pass the matcher the  filename
	    matcher.reset(filename);

	    // does the filename match the pattern
	    if (matcher.find()) {
		// the filename ends in .ti?
		// so strip off that part
		// and add the extension
		String newFileName = filename.substring(0, matcher.start(0));

		return newFileName;
	    } else {
		return filename;
	    }
	}
    }

}
