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
// IndexReference.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Position;
import java.net.URI;

/**
 * A reference to an IndexItem in another Index.
 */
public interface IndexReference {
    /**
     * The URI of Index being referenced.
     */
    public URI getIndexURI();

    /**
     * The ID of the  Index being referenced.
     */
    public ID getIndexID();

    /**
     * The Position of the IndexItem being referenced.
     */
    public Position getIndexItemPosition();

    /**
     * Follow this reference.
     */
    public IndexItem follow() throws GetItemException, IndexClosedException;
} 
