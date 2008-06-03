// DataType.java

package com.timeindexing.index;

/*
 * Type layout
 *
 * -1 notset
 *  0 any/mixed/unknown/arbitrary
 *
 *  1-10  basic types
 *
 *  101-200 general mime types
 * 1000 time index reference.
 */
/**
 * An enumeration of well known data types.
 */
public interface DataType {
    /**
     * The value for this instance of the enumeration.
     */
    public int value();

    /**
     * The mime-type of this DataType.
     */
    public String mimeType();

    /**
     * Nothing has been set
     */
    final static int NOTSET_VALUE = -1;
    public final static DataType NOTSET = new DataType() {
	    public String mimeType() {
		return "type/notset";
	    }

	    public int value() {
		return NOTSET_VALUE;
	    }


	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is of any type.
     */
    final static int ANY_VALUE = 0;
    public final static DataType ANY = new DataType() {
	    public String mimeType() {
		return "type/any";
	    }

	    public int value() {
		return ANY_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is of unknown type.
     */
    final static int UNKNOWN_VALUE = 0;
    public final static DataType UNKNOWN = new DataType() {
	    public String mimeType() {
		return "type/unknown";
	    }

	    public int value() {
		return UNKNOWN_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is of arbitrary type.
     */
    final static int ARBITRARY_VALUE = 0;
    public final static DataType ARBITRARY = new DataType() {
	    public String mimeType() {
		return "type/arbitrary";
	    }

	    public int value() {
		return ARBITRARY_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is of mixed type.
     */
    final static int MIXED_VALUE = 0;
    public final static DataType MIXED = new DataType() {
	    public String mimeType() {
		return "type/mixed";
	    }

	    public int value() {
		return MIXED_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is an integer.
     */
    final static int INTEGER_VALUE = 1;
    public final static DataType INTEGER = new DataType() {
	    public String mimeType() {
		return "type/integer";
	    }

	    public int value() {
		return INTEGER_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is a float.
     */
    final static int FLOAT_VALUE = 2;
    public final static DataType FLOAT = new DataType() {
	    public String mimeType() {
		return "type/float";
	    }

	    public int value() {
		return FLOAT_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a double.
     */
    final static int DOUBLE_VALUE = 3;
    public final static DataType DOUBLE = new DataType() {
	    public String mimeType() {
		return "type/double";
	    }

	    public int value() {
		return DOUBLE_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a long.
     */
    final static int LONG_VALUE = 4;
    public final static DataType LONG = new DataType() {
	    public String mimeType() {
		return "type/long";
	    }

	    public int value() {
		return LONG_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a short.
     */
    final static int SHORT_VALUE = 5;
    public final static DataType SHORT = new DataType() {
	    public String mimeType() {
		return "type/short";
	    }

	    public int value() {
		return SHORT_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a boolean.
     */
    final static int BOOLEAN_VALUE = 6;
    public final static DataType BOOLEAN = new DataType() {
	    public String mimeType() {
		return "type/boolean";
	    }

	    public int value() {
		return BOOLEAN_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a byte.
     */
    final static int BYTE_VALUE = 7;
    public final static DataType BYTE = new DataType() {
	    public String mimeType() {
		return "type/byte";
	    }

	    public int value() {
		return BYTE_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a char.
     */
    final static int CHAR_VALUE = 8;
    public final static DataType CHAR = new DataType() {
	    public String mimeType() {
		return "type/char";
	    }

	    public int value() {
		return CHAR_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a string.
     */
    final static int STRING_VALUE = 9;
    public final static DataType STRING = new DataType() {
	    public String mimeType() {
		return "type/string";
	    }

	    public int value() {
		return STRING_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a void.
     */
    final static int VOID_VALUE = 10;
    public final static DataType VOID = new DataType() {
	    public String mimeType() {
		return "type/void";
	    }

	    public int value() {
		return VOID_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is a big integer.
     */
    final static int BIG_INTEGER_VALUE = 11;
    public final static DataType BIG_INTEGER = new DataType() {
	    public String mimeType() {
		return "type/big-integer";
	    }

	    public int value() {
		return BIG_INTEGER_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is a big decimal
     */
    final static int BIG_DECIMAL_VALUE = 12;
    public final static DataType BIG_DECIMAL = new DataType() {
	    public String mimeType() {
		return "type/big-decimal";
	    }

	    public int value() {
		return BIG_DECIMAL_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is serializable.
     */
    final static int SERIALIZABLE_VALUE = 99;
    public final static DataType SERIALIZABLE = new DataType() {
	    public String mimeType() {
		return "type/serializable";
	    }

	    public int value() {
		return SERIALIZABLE_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is text
     */
    final static int TEXT_VALUE = 101;
    public final static DataType TEXT = new DataType() {
	    public String mimeType() {
		return "text/plain";
	    }

	    public int value() {
		return TEXT_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is html
     */
    final static int HTML_VALUE = 102;
    public final static DataType HTML = new DataType() {
	    public String mimeType() {
		return "text/html";
	    }

	    public int value() {
		return HTML_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is xml
     */
    final static int XML_VALUE = 103;
    public final static DataType XML = new DataType() {
	    public String mimeType() {
		return "text/xml";
	    }

	    public int value() {
		return XML_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is MP3
     */
    final static int MP3_VALUE = 110;
    public final static DataType MP3 = new DataType() {
	    public String mimeType() {
		return "audio/x-mpeg";
	    }

	    public int value() {
		return MP3_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is M3U
     */
    final static int M3U_VALUE = 111;
    public final static DataType M3U = new DataType() {
	    public String mimeType() {
		return "audio/x-mpegurl";
	    }

	    public int value() {
		return M3U_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is WAV
     */
    final static int WAV_VALUE = 112;
    public final static DataType WAV = new DataType() {
	    public String mimeType() {
		return "audio/wav";
	    }

	    public int value() {
		return WAV_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is MPEG
     */
    final static int MPEG_VALUE = 120;
    public final static DataType MPEG = new DataType() {
	    public String mimeType() {
		return "video/mpeg";
	    }

	    public int value() {
		return MPEG_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is QUICKTIME
     */
    final static int QUICKTIME_VALUE = 121;
    public final static DataType QUICKTIME = new DataType() {
	    public String mimeType() {
		return "video/quicktime";
	    }

	    public int value() {
		return QUICKTIME_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is FLV 
     */
    final static int FLV_VALUE = 122;
    public final static DataType FLV = new DataType() {
	    public String mimeType() {
		return "video/flv";
	    }

	    public int value() {
		return FLV_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is JPEG
     */
    final static int JPEG_VALUE = 130;
    public final static DataType JPEG = new DataType() {
	    public String mimeType() {
		return "image/jpeg";
	    }

	    public int value() {
		return JPEG_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is GIF
     */
    final static int GIF_VALUE = 131;
    public final static DataType GIF = new DataType() {
	    public String mimeType() {
		return "image/gif";
	    }

	    public int value() {
		return GIF_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is PNG
     */
    final static int PNG_VALUE = 131;
    public final static DataType PNG = new DataType() {
	    public String mimeType() {
		return "image/png";
	    }

	    public int value() {
		return PNG_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is TIFF
     */
    final static int TIFF_VALUE = 133;
    public final static DataType TIFF = new DataType() {
	    public String mimeType() {
		return "image/tiff";
	    }

	    public int value() {
		return TIFF_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is BMP
     */
    final static int BMP_VALUE = 134;
    public final static DataType BMP = new DataType() {
	    public String mimeType() {
		return "image/bmp";
	    }

	    public int value() {
		return BMP_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};


    /**
     * The data is a Index Reference.
     */
    final static int REFERENCE_VALUE = 1000;
    public final static DataType REFERENCE = new DataType() {
	    public String mimeType() {
		return "type/reference";
	    }

	    public int value() {
		return REFERENCE_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};

    /**
     * The data is a List of Index References.
     */
    final static int REFERENCE_LIST_VALUE = 1001;
    public final static DataType REFERENCE_LIST = new DataType() {
	    public String mimeType() {
		return "type/reference-list";
	    }

	    public int value() {
		return REFERENCE_LIST_VALUE;
	    }

	    public String toString() {
		return mimeType();
	    }
	};
}


    
