// AnnotationStyle.java

package com.timeindexing.index;

/**
 * Annotation style contants.
 */
public interface AnnotationStyle {
    /**
     * Annotations are kept inline in the index.
     */
    public final int NONE = 0;

    /**
     * Annotations are kept inline in the index.
     */
    public final int INLINE = 1;

    /**
     * Annotations are kept external to the index.
     */
    public final int EXTERNAL = 2;
}
