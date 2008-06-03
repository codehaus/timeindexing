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



// Grouping.java

package com.timeindexing.util;

/**
 * An interface for Grouping a collection.
 * <p>
 * The method getGroup() returns an object 
 * which acts as the identifier for a group.
 * Builtin types cannot be returned, but can be converted
 * to their object types; e.g. int -> Integer.
 * 
 */
public interface Grouping {
    /**
     * Take an object and return an object that specifies
     * the group it is in.
     */
     public Object getGroup(Object obj);
 }
