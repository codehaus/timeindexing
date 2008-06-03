/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// DataStyle.java

package com.timeindexing.index;

/**
 * Data style contants.
 */
public interface DataStyle {
    /**
     * Data is kept incore with the index.
     */
    public final int INCORE = 0;

    /**
     * Data is kept inline in the index.
     */
    public final int INLINE = 1;

    /**
     * Data is kept external to the index.
     */
    public final int EXTERNAL = 2;
}
