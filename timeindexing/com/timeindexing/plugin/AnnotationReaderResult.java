// AnnotationReaderResult.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;
import java.nio.ByteBuffer;

/**
 * An implementation for the value returned by a reader plugin
 * that also holds a value for an annotation to be assigned with the result.
 */
public class AnnotationReaderResult extends DefaultReaderResult implements ReaderResult {
    long annotation = 0;

    /**
     * Construct an AnnotationReaderResult
     */
    public AnnotationReaderResult(ByteBuffer bb, Timestamp dTS, DataType dType, long annotationValue) {
	data = bb;
	dataTS = dTS;
	dataType = dType;
	annotation = annotationValue;
    }

    /**
     * Get the annotation value.
     */
    public long getAnnotation() {
	return annotation;
    }
}
