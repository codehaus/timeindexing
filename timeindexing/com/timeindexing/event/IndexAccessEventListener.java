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
// IndexAccessEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Index Access Event, which is used
 * when an IndexItem is accessed in an Index.
 */
public interface IndexAccessEventListener extends EventListener {
    /**
     * A notification that an IndexItem has been accessed in an Index.
     */
    public void itemAccessed(IndexAccessEvent iae);
}
