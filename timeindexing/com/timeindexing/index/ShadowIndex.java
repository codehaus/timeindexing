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
// ShadowIndex.java

package com.timeindexing.index;

import com.timeindexing.io.IndexHeaderIO;
import com.timeindexing.io.ShadowIndexIO;
import com.timeindexing.cache.*;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import java.util.Properties;

/**
 * An implementation of an shadow Index object.
 * It represents the index header, the index stream and the data stream,
 * BUT it does not write any of its own data.
 * it shadows an existing data file.
 * If the data file is removed the data is lost.
 */
public class ShadowIndex extends ExternalIndex  implements ManagedIndex  {

    /**
     * Create an ShadowIndex
     */
    public ShadowIndex() throws IndexSpecificationException {
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this, indexName);
	indexCache = new FileIndexCache(this);

	setCachePolicy(new HollowAtDataVolumeRemoveAfterTimeoutPolicy(1024*1024, new ElapsedMillisecondTimestamp(200)));
	//new HollowAtDataVolumeRemoveAfterTimeoutPolicy());
	// new HollowAtDataVolumePolicy(1024*1024)); //

	setIndexType(IndexType.SHADOW);

	indexInteractor = new ShadowIndexIO(this);

    }
}
