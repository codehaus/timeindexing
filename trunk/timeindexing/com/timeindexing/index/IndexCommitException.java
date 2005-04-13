// IndexCommitException.java

package com.timeindexing.index;

/**
 * An IndexCommitException is thrown when doing a commit()
 * on an Index fails.
 */
public class IndexCommitException extends TimeIndexException {
    /**
     * Throw a IndexCommitException with no message.
     */
    public IndexCommitException() {
	super();
    }

    /**
     * Throw a IndexCommitException with a message.
     */
    public IndexCommitException(String s) {
	super(s);
    }
}
