// DefaultIndexHeader.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.Offset;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * The default index header implementation.
 * It holds all the values in an index header.
 */
public class DefaultIndexHeader implements ManagedIndexHeader {

    // these are currently synced in syncHeader()
    protected String indexName = null;
    protected ID indexID = null;
    protected Timestamp startTime = Timestamp.ZERO;
    protected Timestamp endTime = Timestamp.ZERO;
    protected Timestamp firstTime = Timestamp.ZERO;
    protected Timestamp lastTime = Timestamp.ZERO;
    protected Timestamp firstDataTime = Timestamp.ZERO;
    protected Timestamp lastDataTime = Timestamp.ZERO;
    protected long length = 0;
    protected boolean terminated = false;
    protected Offset firstOffset = null;
    protected Offset lastOffset = null;

    // These should be options in the IndexProperties

    // TODO: fix the getter and setter to use the 
    // optionalValues map.
    //protected String indexPathName = null;
    //protected String dataPathName = null;
    //protected Description description = null;
    //protected DataType dataType = DataType.NOTSET_DT;
    //protected boolean indexIsSorted = true;

    // these are not, YET

    protected int itemSize = 0;
    protected long dataSize = 0;
    protected Map dataTypeMap = null;
    protected IndexType indexType = IndexType.INCORE_DT;
    protected boolean hasAnnotations = false;
    protected int annotationStyle = AnnotationStyle.NONE;
    protected Map externalIndexMap = null;

    // low level info
    protected int versionMajor = 0;
    protected int versionMinor = 0;
    
    // A Map used for the optional value
    // These include the descrition, the dataPathName
    IndexProperties optionalValues = null;

    /**
     * Create a DefaultIndexHeader object.
     */
    protected DefaultIndexHeader() {
	setInTimeOrder();
    }

    /**
     * Create a DefaultIndexHeader object.
     */
    public DefaultIndexHeader(String name) {
	setName(name);
	setInTimeOrder();
    }

    /**
     * Get the name of the index.
     */
    public String getName() {
	return indexName;
    }

    /**
     * Set the name of the index.
     */
    public ManagedIndexHeader setName(String name) {
	indexName = name;
	return this;
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return indexID;
    }

    /**
     * Set the ID of the index.
     */
    public ManagedIndexHeader setID(ID id) {
	indexID = id;
	return this;
    }


    /**
     * Get the start time of the index.
     * This is when the index was created not necessarliy when the first item
     * was added to the index.
     */
    public Timestamp getStartTime() {
	return startTime;
    }

    /**
     * Set the start time
     */
    public ManagedIndexHeader setStartTime(Timestamp start) {
	startTime = start;
	return this;
    }

    /**
     * Get the end time of the index
     * This is the time the last item was closed, not necessarliy when the last item
     * was added to the index.
     */
    public Timestamp getEndTime() {
	return endTime;
    }

    /**
     * Set the end time
     */
    public ManagedIndexHeader setEndTime(Timestamp end) {
	endTime = end;
	return this;
    }

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstTime() {
	return firstTime;
    }

    /**
     * Set the first time
     */
    public ManagedIndexHeader setFirstTime(Timestamp first) {
	firstTime = first;
	return this;
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastTime() {
	return lastTime;
    }

    /**
     * Set the last time
     */
    public ManagedIndexHeader setLastTime(Timestamp last) {
	lastTime = last;
	return this;
    }

    /**
     * Get the data time for the first IndexItem in the Index.
     */
    public Timestamp getFirstDataTime() {
	return firstDataTime;
    }

    /**
     * Set the first data time
     */
    public ManagedIndexHeader setFirstDataTime(Timestamp first) {
	firstDataTime = first;
	return this;
    }


    /**
     * Get the data time for the last IndexItem in the Index.
     */
    public Timestamp getLastDataTime() {
	return lastDataTime;
    }

    /**
     * Set the last data time
     */
    public ManagedIndexHeader setLastDataTime(Timestamp last) {
	lastDataTime = last;
	return this;
    }


    /**
     * Get the length of the index.
     */
    public long getLength() {
	return length;
    }

     /**
     * Set the no of items in the index.
     */
    public ManagedIndexHeader setLength(long length) {
	this.length = length;
	return this;
    }
    
    /**
     * Is the Index terminated.
     */
    public boolean isTerminated() {
	return terminated;
    }

    /**
     * Set the index to be terminated.
     */
    public ManagedIndexHeader terminate() {
	terminated = true;
	return this;
    }
    
    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset() {
	return firstOffset;
    }

    /**
     * Set the Offset of the fisrt item.
     */
    public ManagedIndexHeader setFirstOffset(Offset offset) {
	firstOffset = offset;
	return this;
    }

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset() {
	return lastOffset;
    }

    /**
     * Set the Offset of the last item.
     */
    public ManagedIndexHeader setLastOffset(Offset offset) {
	lastOffset = offset;
	return this;
    }

    /**
     * Get the size of the items.
     */
    public int getItemSize() {
	return itemSize;
    }

   /**
     * Set the size of the index items.
     */
    public ManagedIndexHeader setItemSize(int size) {
	itemSize = size;
	return this;
    }

    /**
     * Does the index have fixed size data.
     */
    public boolean isFixedSizeData() {
	return dataSize == 0 ? false : true;
    }

    /**
     * Get the size of the data items, if there is fixed size data.
     * The value 0 means variable sized data.
     */
    public long getDataSize() {
	return dataSize;
    }

    /**
     * Set the size of the data items, if there is fixed size data.
     */
    public ManagedIndexHeader setDataSize(long size) {
	return this;
    }

    /**
     * Get the data style.
     * Either inline or external or shadow.
     */
    public IndexType getIndexType() {
	return indexType;
    }

    /**
     * Set the data style.
     * Either inline or external or shadow.
     */
    public ManagedIndexHeader setIndexType(IndexType type) {
	indexType = type;
	return this;
    }

    /**
     * Get the index data type.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public DataType getIndexDataType() {
	return (DataType)getOption(HeaderOption.DATATYPE_HO);
    }

    /**
     * Set the data type of the index.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public ManagedIndexHeader setIndexDataType(DataType type) {
	if (type != null) {	// only set an option if there is a value
	    setOption(HeaderOption.DATATYPE_HO, type);
	}
	return this;
    }


    /**
     * Get the path of the index  file.
     * @return null if there is no index path
     */
    public String getIndexPathName() {
	return (String)getOption(HeaderOption.INDEXPATH_HO);
    }

    /**
     * Set the path of the index file
     */
    public ManagedIndexHeader setIndexPathName(String path) {
	if (path != null) {	// only set an option if there is a value
	    setOption(HeaderOption.INDEXPATH_HO, path);
	}
	return this;
    }

    /**
     * Get the path of the data if the index data style
     * is external or shadow.
     * @return null if there is no data path
     */
    public String getDataPathName() {
	return (String)getOption(HeaderOption.DATAPATH_HO);
    }

    /**
     * Set the path of the data if the index data style
     * is external or shadow.
     */
    public ManagedIndexHeader setDataPathName(String path) {
	if (path != null) {	// only set an option if there is a value
	    setOption(HeaderOption.DATAPATH_HO, path);
	}
	return this;
    }

    /**
     * Get the description for an index.
     * @return null if there is no description
     */
    public Description getDescription() {
	return (Description)getOption(HeaderOption.DESCRIPTION_HO);
    }

    /**
     * Set the description.
     * This is one of the few attributes of an index that can be set directly.
     */
    public IndexHeader setDescription(Description d) {
	if (d != null) {	// only set an option if there is a value
	    setOption(HeaderOption.DESCRIPTION_HO, d);
	}
	return this;
    }

    /**
     * State that the index is not in time order any more.
     */
    public boolean isInTimeOrder() {
	return ((Boolean)getOption(HeaderOption.IS_IN_TIME_ORDER_HO)).booleanValue();
    }

    /**
     * State that the index is in time order.
     */
    public ManagedIndexHeader setInTimeOrder() {
	setOption(HeaderOption.IS_IN_TIME_ORDER_HO, new Boolean(true));
	return this;
    }


    /**
     * State that the index is not in time order any more.
     */
    public ManagedIndexHeader notInTimeOrder() {
	setOption(HeaderOption.IS_IN_TIME_ORDER_HO, new Boolean(false));
	return this;
    }


    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations() {
	return hasAnnotations;
    }

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle() {
	return annotationStyle;
    }


    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID) {
	if (dataTypeMap == null) {
	    return null;
	} else {
	    return (String)dataTypeMap.get(typeID);
	}
    }

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName) {
	if (dataTypeMap == null) {
	    return false;
	} else {
	    return dataTypeMap.containsValue(typeName);
	}
    }

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName) {
	if (dataTypeMap == null) {
	    dataTypeMap = new HashMap();
	}

	dataTypeMap.put(typeID, typeName);
	return true;
    }

    /**
     * Get the index URI of a nominated index.
     */
    public String getIndexURI(ID indexID) {
	if (externalIndexMap == null) {
	    return null;
	} else {
	    return (String)externalIndexMap.get(indexID);
	}
    }

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName) {
	if (externalIndexMap ==  null) {
	    return false;
	} else {
	    return dataTypeMap.containsValue(URIName);
	}
    }

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName) {
	if (externalIndexMap == null) {
	    externalIndexMap = new HashMap();
	}

	externalIndexMap.put(indexID, URIName);
	return true;
    }

    /**
     * Syncrhronize the values in this index header 
     * from values in a specified IndexHeader object.
     */
    public boolean syncHeader(ManagedIndexHeader indexHeader) {
	setName(indexHeader.getName());
	setID(indexHeader.getID());
	setStartTime(indexHeader.getStartTime());
	setEndTime(indexHeader.getEndTime());
	setFirstTime(indexHeader.getFirstTime());
	setLastTime(indexHeader.getLastTime());
	setFirstDataTime(indexHeader.getFirstDataTime());
	setLastDataTime(indexHeader.getLastDataTime());
	setLength(indexHeader.getLength());
	setFirstOffset(indexHeader.getFirstOffset());
	setLastOffset(indexHeader.getLastOffset());
	setIndexPathName(indexHeader.getIndexPathName());
	setDataPathName(indexHeader.getDataPathName());
	setDescription(indexHeader.getDescription());
	setIndexDataType(indexHeader.getIndexDataType());
	
	// if the other indexHeader is NOT in time order
	// then set me to be NOT in time order
	if (!indexHeader.isInTimeOrder()) {
	    notInTimeOrder();
	}

	// if the other indexHeader is terminated
	// then set me to be terminated
	if (indexHeader.isTerminated()) {
	    terminate();
	}

	//setItemSize(indexHeader.getItemSize());

	// sync the options
	setOptions(indexHeader.getAllOptions());
	return true;
    }

    /**
     * Get a value from the header.
     */
    public Object getValue(String name) {
	return null;
    }

    /**
     * Get an option from the header.
     */
    public Object getOption(HeaderOption option) {
	if (optionalValues == null) {
	    return null;
	} else {
	    return optionalValues.get(option);
	}
    }

    /**
     * Set an option in the header.
     */
    public ManagedIndexHeader setOption(HeaderOption option, Object object) {
	if (optionalValues == null) {
	    optionalValues = new IndexProperties();
	}

	optionalValues.put(option, object);

	return this;
    }

    /**
     * Does an option exist in the header.
     */
    public boolean hasOption(HeaderOption option) {
	if (optionalValues == null) {
	    return false;
	} else {
	    return optionalValues.containsKey(option);
	}
    }

    /**
     * Get the set of optional header values.
     */
    public Set listOptions() {
	if (optionalValues == null) {
	    return null;
	} else {
	    return optionalValues.keySet();
	}
    }

    /**
     * Get all the option from the header.
     */
    public IndexProperties getAllOptions() {
	if (optionalValues == null) {
	    return null;
	} else {
	    return optionalValues;
	}
    }

    /**
     * Set options in the header based on the passed IndexProperties.
     */
    public ManagedIndexHeader setOptions(IndexProperties someProperties) {
	if (someProperties == null) {
	    // no copying to do
	    return this;
	} else {
	    if (optionalValues == null) {
		optionalValues = new IndexProperties();
	    }

	    optionalValues.putAll(someProperties);

	    return this;
	}
    }

    /**
     * A strign version of the header.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append("TimeIndex ");
	buffer.append("Header, ");
	buffer.append("Version ");
	buffer.append(versionMajor);
	buffer.append(" . ");
	buffer.append(versionMinor);
	buffer.append(", ");

	buffer.append(indexType);
	buffer.append(", ");

	/*
	switch (indexType) {
	case IndexType.INLINE: {
	    buffer.append("Inline, ");
	    break;
	}
	case IndexType.EXTERNAL: {
	    buffer.append("External, ");
	    break;
	}
	case IndexType.SHADOW: {
	    buffer.append("Shadow, ");
	    break;
	}
	case IndexType.INCORE: {
	    buffer.append("Incore, ");
	    break;
	}
	default: {
	    buffer.append("Missing cases in code for IndexType!, ");
	    break;
	}
	}
	*/

	
	buffer.append(getIndexDataType());
	buffer.append(", ");

	/*
	switch (getIndexDataType().value()) {
	case DataType.NOTSET: {
	    buffer.append("Type not set!, ");
	    break;
	}
	case DataType.ANY: {
	    buffer.append("Any type, ");
	    break;
	}
	case DataType.INTEGER: {
	    buffer.append("Integer, ");
	    break;
	}
	case DataType.FLOAT: {
	    buffer.append("Float, ");
	    break;
	}
	default: {
	    buffer.append("Missing cases in code for DataType!, ");
	    break;
	}
	}
	*/

	buffer.append("Index name \"");
	buffer.append(indexName);
	buffer.append("\"");

	buffer.append("\n");
	buffer.append("ID: ");
	buffer.append(indexID);
	buffer.append("\nStart Time: ");
	buffer.append(startTime);
	buffer.append("\nFirst Time: ");
	buffer.append(firstTime);
	buffer.append("\nLast Time: ");
	buffer.append(lastTime);
	buffer.append("\nEnd Time: ");
	buffer.append(endTime);
	buffer.append("\nFirst Offset: ");
	buffer.append(firstOffset);
	buffer.append("\nLast Offset: ");
	buffer.append(lastOffset);
	buffer.append("\n");

	return buffer.toString();
    }


}
