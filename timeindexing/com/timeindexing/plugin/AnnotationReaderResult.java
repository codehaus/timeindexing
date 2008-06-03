Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
