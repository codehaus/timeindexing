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
