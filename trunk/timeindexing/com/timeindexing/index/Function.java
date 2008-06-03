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



// Function.java

package com.timeindexing.index;

/**
 * An interface for functions that are passed to filter() and map().
 * <p>
 * The identity function can be coded as:
 * <tt>
 * Function identity = new Function() {
 *     public Object evaluate(IndexItem i) {
 *         return i;
 *     }
 * }
 * </tt>
 */
public interface Function {
    /**
     * Evaluate an IndexItem in some way.
     * @return an Object, if the function evaluates, null otherwise
     */
    public Object evaluate(IndexItem i);
}
