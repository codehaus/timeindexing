// IndexCreateException.java

package com.timeindexing.index;

/**
 * A IndexCreateException is thrown when an Index
 * cannot be created.
 * This usually because the Properties passed at create time
 * do not have enough information.
 */
public class IndexCreateException extends TimeIndexException {
    /**
     * Throw a IndexCreateException with no message.
     */
    public IndexCreateException() {
	super();
    }

    /**
     * Throw a IndexCreateException with a message.
     */
    public IndexCreateException(String s) {
	super(s);
    }

    /**
     * Throw a IndexCreateException with an  exception from the underlying cause.
     */
    public IndexCreateException(Exception e) {
	super(e);
    }

}
