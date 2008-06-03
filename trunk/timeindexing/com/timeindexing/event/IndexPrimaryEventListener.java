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
// IndexPrimaryEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Index Primary Event Listener, which is used
 * at primary moments.
 * These include: open , close, commit, create.
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
     * A notification that an Index has been committed.
     */
    public  void committed(IndexPrimaryEvent ipe);

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe);

    /**
     * A notification that a view has been added to an Index.
     */
    public void viewAdded(IndexPrimaryEvent ipe);

    /**
     * A notification that a view has been removed to an Index.
     */
    public void viewRemoved(IndexPrimaryEvent ipe);
} 
