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
// DataAbstraction.java

package com.timeindexing.index;

import com.timeindexing.basic.Size;

/**
 * An interface that provides a common type for data objects.  
 * This interface is used by objects that hold values which are
 * implementations of a DataAbstraction.
 */
public interface DataAbstraction {
    /**
     * Get the size of the data.
     */

    public Size getSize();
}
