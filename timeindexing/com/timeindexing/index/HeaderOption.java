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



// HeaderOption.java

package com.timeindexing.index;


/**
 * An enumeration of the different kinds of header option.
 */
public interface HeaderOption {
    public byte value();

    /**
     * Index Path
     * The path to the file that contains the index.
     */
    public final static byte INDEXPATH = 1;
    public final static HeaderOption INDEXPATH_HO = new HeaderOption() {
	    public String toString() {
		return "indexpath";
	    }

	    public byte value() {
		return INDEXPATH;
	    }
	};



    /**
     * Data Path
     * The path to the file that contains the data.
     */
    public final static byte DATAPATH = 2;
    public final static HeaderOption DATAPATH_HO = new HeaderOption() {
	    public String toString() {
		return "datapath";
	    }

	    public byte value() {
		return DATAPATH;
	    }
	};



    /**
     * Description
     */
    public final static byte DESCRIPTION = 3;
    public final static HeaderOption DESCRIPTION_HO = new HeaderOption() {
	    public String toString() {
		return "description";
	    }

	    public byte value() {
		return DESCRIPTION;
	    }
	};



    /**
     * Data type
     */
    public final static byte DATATYPE = 4;
    public final static HeaderOption DATATYPE_HO = new HeaderOption() {
	    public String toString() {
		return "datatype";
	    }

	    public byte value() {
		return DATATYPE;
	    }
	};

    /**
     * Is sorted
     */
    public final static byte IS_IN_TIME_ORDER = 5;
    public final static HeaderOption IS_IN_TIME_ORDER_HO = new HeaderOption() {
	    public String toString() {
		return "is_in_time_order";
	    }

	    public byte value() {
		return IS_IN_TIME_ORDER;
	    }
	};


    /**
     * No data file header.
     * This means that the data file will not have the usual
     * Timeindexing header, but will start immediately with the data.
     */
    public final static byte NO_DATA_FILE_HEADER = 6;
    public final static HeaderOption NO_DATA_FILE_HEADER_HO = new HeaderOption() {
	    public String toString() {
		return "no_data_file_header";
	    }

	    public byte value() {
		return NO_DATA_FILE_HEADER;
	    }
	};



    /**
     * Type Mapping
     */
    public final static byte TYPEMAPPING = 11;
    public final static HeaderOption TYPEMAPPING_HO = new HeaderOption() {
	    public String toString() {
		return "typemapping";
	    }

	    public byte value() {
		return TYPEMAPPING;
	    }
	};

    /**
     * Reference Mapping
     */
    public final static byte REFERENCEMAPPING = 12;
    public final static HeaderOption REFERENCEMAPPING_HO = new HeaderOption() {
	    public String toString() {
		return "referencemapping";
	    }

	    public byte value() {
		return REFERENCEMAPPING;
	    }
	};



}


    
