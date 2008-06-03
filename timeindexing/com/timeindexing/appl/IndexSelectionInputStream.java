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



// IndexSelectionInputStream.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

/**
 * A InputStream class to get to data in an Index.
 */
public class IndexSelectionInputStream extends IndexInputStream {
    /**
     * Construct an IndexSelectionInputStream object given
     * an Index and some IndexProperties
     */
    public IndexSelectionInputStream(Index anIndex, IndexProperties properties) {
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((IndexView)anIndex, properties);
	index = selection;
	length = selection.getLength();
    }
}
