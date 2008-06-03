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

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Determine the security code of a selection of TimeIndex
 * given the Index filename.
 */
public class SelectionCodeEvaluator {
    TimeIndexFactory factory = null;

    Properties properties = null;

    Index index = null;

    long code = 0;

    /**
     * Construct a SelectionCodeEvaluator object.
     */
    public SelectionCodeEvaluator(String filename) {
	factory = new TimeIndexFactory();
	properties = new Properties();

	// set the filename property
	properties.setProperty("indexpath", filename);
    }

    /**
     * Evaluate the security code.
     */
    public long evaluate(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	// open
	open();

	// generate the code
	generate(selectionProperties);

	// close 
	close();

	return code;
    }

    /**
     * Open
     */
    protected void open() throws TimeIndexException {
	index = factory.open(properties);
    }
	
    /**
     * Do the generation
     */
    protected void generate(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	SelectionProcessor selecter = new SelectionProcessor();
	IndexView selection = selecter.select((IndexView)index, selectionProperties);

	if (selection == null) {
	    throw new TimeIndexException("Can't evaluate security code for selection which is not in the index: " + index.getName());
	} else {
	    
	    SelectionValidationCode generator = new SelectionValidationCode();

	    code =  generator.generate(selection);
	}
    }

    /**
     * Close 
     */
    protected void close() throws TimeIndexException {
	factory.close(index);
    }
}
