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
    public int value();
    public String mimeType();

    /**
     * Nothing has been set
     */
    public final int NOTSET = -1;
    public final static DataType NOTSET_DT = new DataType() {
	    public String mimeType() {
		return "type/notset";
	    }

	    public int value() {
		return NOTSET;
	    }
	};

    /**
     * The data is of any type.
     */
    public final static int ANY = 0;
    public final static DataType ANY_DT = new DataType() {
	    public String mimeType() {
		return "type/any";
	    }

	    public int value() {
		return ANY;
	    }
	};

    /**
     * The data is of unknown type.
     */
    public final static int UNKNOWN = 0;
    public final static DataType UNKNOWN_DT = new DataType() {
	    public String mimeType() {
		return "type/unknown";
	    }

	    public int value() {
		return UNKNOWN;
	    }
	};


    /**
     * The data is of arbitrary type.
     */
    public final static int ARBITRARY = 0;
    public final static DataType ARBITRARY_DT = new DataType() {
	    public String mimeType() {
		return "type/arbitrary";
	    }

	    public int value() {
		return ARBITRARY;
	    }
	};

    /**
     * The data is of mixed type.
     */
    public final static int MIXED = 0;
    public final static DataType MIXED_DT = new DataType() {
	    public String mimeType() {
		return "type/mixed";
	    }

	    public int value() {
		return MIXED;
	    }
	};


    /**
     * The data is an integer.
     */
    public final static int INTEGER = 1;
    public final static DataType INTEGER_DT = new DataType() {
	    public String mimeType() {
		return "type/integer";
	    }

	    public int value() {
		return INTEGER;
	    }
	};

    /**
     * The data is a float.
     */
    public final static int FLOAT = 2;
    public final static DataType FLOAT_DT = new DataType() {
	    public String mimeType() {
		return "type/float";
	    }

	    public int value() {
		return FLOAT;
	    }
	};


    /**
     * The data is a double.
     */
    public final static int DOUBLE = 3;
    public final static DataType DOUBLE_DT = new DataType() {
	    public String mimeType() {
		return "type/double";
	    }

	    public int value() {
		return DOUBLE;
	    }
	};


    /**
     * The data is a long.
     */
    public final static int LONG = 4;
    public final static DataType LONG_DT = new DataType() {
	    public String mimeType() {
		return "type/long";
	    }

	    public int value() {
		return LONG;
	    }
	};


    /**
     * The data is a short.
     */
    public final static int SHORT = 5;
    public final static DataType SHORT_DT = new DataType() {
	    public String mimeType() {
		return "type/short";
	    }

	    public int value() {
		return SHORT;
	    }
	};


    /**
     * The data is a boolean.
     */
    public final static int BOOLEAN = 6;
    public final static DataType BOOLEAN_DT = new DataType() {
	    public String mimeType() {
		return "type/boolean";
	    }

	    public int value() {
		return BOOLEAN;
	    }
	};


    /**
     * The data is a byte.
     */
    public final static int BYTE = 7;
    public final static DataType BYTE_DT = new DataType() {
	    public String mimeType() {
		return "type/byte";
	    }

	    public int value() {
		return BYTE;
	    }
	};


    /**
     * The data is a char.
     */
    public final static int CHAR = 8;
    public final static DataType CHAR_DT = new DataType() {
	    public String mimeType() {
		return "type/char";
	    }

	    public int value() {
		return CHAR;
	    }
	};


    /**
     * The data is a string.
     */
    public final static int STRING = 9;
    public final static DataType STRING_DT = new DataType() {
	    public String mimeType() {
		return "type/string";
	    }

	    public int value() {
		return STRING;
	    }
	};


    /**
     * The data is a void.
     */
    public final static int VOID = 10;
    public final static DataType VOID_DT = new DataType() {
	    public String mimeType() {
		return "type/void";
	    }

	    public int value() {
		return VOID;
	    }
	};

    /**
     * The data is text
     */
    public final static int TEXT = 101;
    public final static DataType TEXT_DT = new DataType() {
	    public String mimeType() {
		return "text/plain";
	    }

	    public int value() {
		return TEXT;
	    }
	};

    /**
     * The data is html
     */
    public final static int HTML = 102;
    public final static DataType HTML_DT = new DataType() {
	    public String mimeType() {
		return "text/html";
	    }

	    public int value() {
		return HTML;
	    }
	};


    /**
     * The data is xml
     */
    public final static int XML = 103;
    public final static DataType XML_DT = new DataType() {
	    public String mimeType() {
		return "text/xml";
	    }

	    public int value() {
		return XML;
	    }
	};


    /**
     * The data is MP3
     */
    public final static int MP3 = 110;
    public final static DataType MP3_DT = new DataType() {
	    public String mimeType() {
		return "audio/x-mpeg";
	    }

	    public int value() {
		return MP3;
	    }
	};

    /**
     * The data is M3U
     */
    public final static int M3U = 111;
    public final static DataType M3U_DT = new DataType() {
	    public String mimeType() {
		return "audio/x-mpegurl";
	    }

	    public int value() {
		return M3U;
	    }
	};


    /**
     * The data is WAV
     */
    public final static int WAV = 112;
    public final static DataType WAV_DT = new DataType() {
	    public String mimeType() {
		return "audio/wav";
	    }

	    public int value() {
		return WAV;
	    }
	};

    /**
     * The data is MPEG
     */
    public final static int MPEG = 120;
    public final static DataType MPEG_DT = new DataType() {
	    public String mimeType() {
		return "video/mpeg";
	    }

	    public int value() {
		return MPEG;
	    }
	};

    /**
     * The data is QUICKTIME
     */
    public final static int QUICKTIME = 121;
    public final static DataType QUICKTIME_DT = new DataType() {
	    public String mimeType() {
		return "video/quicktime";
	    }

	    public int value() {
		return QUICKTIME;
	    }
	};


    /**
     * The data is JPEG
     */
    public final static int JPEG = 130;
    public final static DataType JPEG_DT = new DataType() {
	    public String mimeType() {
		return "image/jpeg";
	    }

	    public int value() {
		return JPEG;
	    }
	};

    /**
     * The data is GIF
     */
    public final static int GIF = 131;
    public final static DataType GIF_DT = new DataType() {
	    public String mimeType() {
		return "image/gif";
	    }

	    public int value() {
		return GIF;
	    }
	};

    /**
     * The data is PNG
     */
    public final static int PNG = 131;
    public final static DataType PNG_DT = new DataType() {
	    public String mimeType() {
		return "image/png";
	    }

	    public int value() {
		return PNG;
	    }
	};

    /**
     * The data is TIFF
     */
    public final static int TIFF = 133;
    public final static DataType TIFF_DT = new DataType() {
	    public String mimeType() {
		return "image/tiff";
	    }

	    public int value() {
		return TIFF;
	    }
	};

    /**
     * The data is BMP
     */
    public final static int BMP = 134;
    public final static DataType BMP_DT = new DataType() {
	    public String mimeType() {
		return "image/bmp";
	    }

	    public int value() {
		return BMP;
	    }
	};


    /**
     * The data is a Index Reference.
     */
    public final static int REFERENCE = 1000;
    public final static DataType REFERENCE_DT = new DataType() {
	    public String mimeType() {
		return "type/reference";
	    }

	    public int value() {
		return REFERENCE;
	    }
	};

}


    
