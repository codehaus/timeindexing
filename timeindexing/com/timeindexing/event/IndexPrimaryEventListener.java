// IndexPrimaryEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Index Primary Event Listener, which is used
 * at primary moments.
 * These include: open , close, flush, create.
 */
public interface IndexPrimaryEventListener extends EventListener {
    /**
     * A notification that an Index has been opened.
     */
    public  void opened(IndexPrimaryEvent ipe);

    /**
     * A notification that an Index has been closed.
     */
    public  void closed(IndexPrimaryEvent ipe);

    /**
     * A notification that an Index has been flushed.
     */
    public  void flushed(IndexPrimaryEvent ipe);

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe);
} 
