// DataTyoeDirectory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.SID;
import com.timeindexing.time.Clock;

import java.util.Properties;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * This is the DataTypeDirectory which returns DataType objects.
 *
 * It provides a directory of current DataTypes, which can be looked up by name
 * or by ID.
 * This is needed in order to share data type objects, and
 * to have a central repository for creatinf new data type objects.
 *
 */
public class DataTypeDirectory implements Serializable {
    final static long serialVersionUID = 789782781860612814L;

    protected static DataTypeDirectory directory = new DataTypeDirectory();

    /**
     * The ID directory.
     */
    protected transient  HashMap indexByIDDirectory = null;

    /**
     * The name directory.
     */
    protected transient HashMap indexByNameDirectory = null;

    /**
     * The next allocatable ID.
     */
    protected int nextID = 10000;

    /**
     * The read-in loaded ID
     */
    private int loadedID = nextID;

    /**
     * Construct a DataTypeDirectory
     */
    public DataTypeDirectory() {
	indexByIDDirectory = new HashMap();
	indexByNameDirectory = new HashMap();

	// register all the types from DataType

	registerDataType(DataType.NOTSET_DT);
	registerDataType(DataType.ANY_DT);
	registerDataType(DataType.INTEGER_DT);
	registerDataType(DataType.FLOAT_DT);
	registerDataType(DataType.DOUBLE_DT);
	registerDataType(DataType.LONG_DT);
	registerDataType(DataType.SHORT_DT);
	registerDataType(DataType.BOOLEAN_DT);
	registerDataType(DataType.BYTE_DT);
	registerDataType(DataType.CHAR_DT);
	registerDataType(DataType.STRING_DT);
	registerDataType(DataType.VOID_DT);
	registerDataType(DataType.TEXT_DT);
	registerDataType(DataType.HTML_DT);
	registerDataType(DataType.XML_DT);
	registerDataType(DataType.MP3_DT);
	registerDataType(DataType.M3U_DT);
	registerDataType(DataType.WAV_DT);
	registerDataType(DataType.MPEG_DT);
	registerDataType(DataType.QUICKTIME_DT);
	registerDataType(DataType.JPEG_DT);
	registerDataType(DataType.GIF_DT);
	registerDataType(DataType.PNG_DT);
	registerDataType(DataType.TIFF_DT);
	registerDataType(DataType.BMP_DT);
	registerDataType(DataType.REFERENCE_DT);
	registerDataType(DataType.REFERENCE_LIST_DT);
 
	// now try and get the saved ID
	getSavedID();
    }

    /*
     * Static directory methods.
     */

	
    /**
     *  Find a DataType by dataType name.
     */
    public static DataType find(String name) {
	return directory.getDataType(name);
    }

    /**
     * Find a DataType by ID.
     */
    public static DataType find(ID anID) {
	return directory.getDataType(anID);
    }

    /**
     * Find a DataType by value.
     */
    public static DataType find(int value) {
	return directory.getDataType(new SID(value));
    }

    /**
     * Register a new DataType given a mime-type and an int id.
     * It will create a new DataType if necessary.
     */
    public static DataType register(String name) {
	return directory.registerDataType(name);
    }

    /**
     * Register a new DataType given a mime-type and an int id.
     * It will create a new DataType if necessary.
     */
    public static DataType register(String name, int value) {
	return directory.registerDataType(name, value);
    }

    /**
     * Register an DataType
     */
    public static DataType register(DataType dataType) { 
	return directory.registerDataType(dataType);
    }

    /**
     * Unregister an DataType 
     */
    public static boolean unregister(DataType index) { 
	return directory.unregisterDataType(index);
    }


    /**
     * Find a DataType by dataType name.
     */
    public  DataType getDataType(String name) {
	// lookup an dataType using the dataType's name.

	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The Name Directory has disappeared");
	} else {
	    DataType dataType = (DataType)indexByNameDirectory.get(name);

	    if (dataType == null) {
		return DataType.UNKNOWN_DT;
	    } else {
		return dataType;
	    }
	}

    }

    /**
     * Save an DataType by dataType name.
     */
    protected boolean putDataType(String name, DataType dataType) {
	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The Name Directory has disappeared");
	} else {
	    indexByNameDirectory.put(name, dataType);
	    return true;
	}
    }

    /**
     * Remove an DataType by dataType name.
     */
    protected boolean removeDataType(String name) {
	if (indexByNameDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The Name Directory has disappeared");
	} else {
	    indexByNameDirectory.remove(name);
	    return true;
	}
    }

    /**
     * Find a DataType by ID.
     */
    public DataType getDataType(ID anID) {
	// lookup an dataType using an ID.

	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The ID Directory has disappeared");
	} else {
	    DataType dataType = (DataType)indexByIDDirectory.get(anID);

	    if (dataType == null) {
		return DataType.UNKNOWN_DT;
	    } else {
		return dataType;
	    }
	}
    }

    /**
     * Save an DataType by dataType ID
     */
    protected boolean putDataType(ID id, DataType dataType) {
	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The ID Directory has disappeared");
	} else {
	    indexByIDDirectory.put(id, dataType);
	    return true;
	}
    }

     /**
     * Remove an DataType by dataType ID
     */
    protected boolean removeDataType(ID id) {
	if (indexByIDDirectory == null) {
	    // this shoudl never happen
	    throw new DataTypeDirectoryException("DataTypeDirectory: The ID Directory has disappeared");
	} else {
	    indexByIDDirectory.remove(id);
	    return true;
	}
    }


    /**
     * Register a new DataType given a mime-type.
     * It will create a new DataType if necessary.
     */
    public DataType registerDataType(String name) {
	DataType dataType = registerDataType(name, nextID());
	incrementID();
	return dataType;
    }

    /**
     * Register a new DataType given a mime-type and an int id.
     * It will create a new DataType if necessary.
     */
    public DataType registerDataType(final String name, final int value) {
	DataType dataType = new DataType() {
		public String mimeType() {
		    return name;
		}

		public int value() {
		    return value;
		}
	    };

	return registerDataType(dataType, name, new SID(value));
    }
    /**
     * Register an DataType using its name and its ID.
     */
    public DataType registerDataType(DataType dataType, String name, ID anID) {
	boolean result =  putDataType(name, dataType) && putDataType(anID, dataType);

	return dataType;
    }

    /**
     * Register an DataType
     */
    public DataType registerDataType(DataType dataType) { 
	return registerDataType(dataType, dataType.mimeType(), new SID(dataType.value()));
    }

    /**
     * Unregister an DataType.
     */
    public boolean unregisterDataType(DataType dataType) {
	String name = dataType.mimeType();
	ID anID = new SID(dataType.value());

	boolean result =  removeDataType(name) && removeDataType(anID);

	return result;
    }


    /**
     * Whats the next ID.
     */
    public int nextID() {
	return nextID;
    }

    /**
     * Increment the next ID.
     */
    public int incrementID() {
	nextID++;
	saveNextID();
	return nextID;
    }

    /** 
     * Write out the ID.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeInt(nextID);
    }

    /** 
     * Read in the ID.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	nextID  = in.readInt();
	loadedID = nextID;
    }


    /**
     * Get the saved ID.
     */
    public void getSavedID() {
	try {
	    File serialFile = new File(System.getProperty("user.home"), ".timeindexing.DataTypeID.ser");
	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serialFile));
	    DataTypeDirectory copy = (DataTypeDirectory)ois.readObject();
	    ois.close();
	    nextID = copy.nextID;
	    loadedID = nextID;

	} catch (IOException ioe) {
	    //System.err.println("getSavedID: IOException");
            //ioe.printStackTrace();
	    
	} catch (ClassNotFoundException cnfe) {
	     System.err.println("getSavedID: ClassNotFoundException");
	}
    }

    /**
     * Save the next ID.
     */
    public void saveNextID() {
	try {
	    File serialFile = new File(System.getProperty("user.home"), ".timeindexing.DataTypeID.ser");
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serialFile));
	    oos.writeObject(this);
	    oos.close();
	} catch (IOException ioe) {
	    System.err.println("saveNextID: IOException" + ioe);
	}
    }

    /**
     * On finalize, save the ID.
     */
    protected void finalize() {
	if (nextID != loadedID) {
	    // something has changed
	    System.err.println("finalize: save next ID");
	    saveNextID();
	}
    }

}

