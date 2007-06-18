// OutputEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Output Event, which is used
 * when an output component actually does some output.
 * @see com.timeindexing.appl.OutputStreamer
 */
public interface OutputEventListener extends EventListener {
    /**
     * A notification that some output has been done
     */
    public void outputNotification(OutputEvent oe);
}
