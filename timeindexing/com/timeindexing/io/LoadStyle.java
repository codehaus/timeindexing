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
// LoadStyle.java

package com.timeindexing.io;


/**
 * An enumeration of ways to load the index.
 */
public interface LoadStyle {
    /**
     * Load all of the index and all of the data.
     */
    public final static LoadStyle ALL = new LoadStyle() {
	    public String toString() {
		return "ALL";
	    }
	};


    /**
     * Load all of the index and none of the data.
     */
    public final static LoadStyle HOLLOW = new LoadStyle() {
	    public String toString() {
		return "HOLLOW";
	    }
	};



    /**
     * Load nothing.
     */
    public final static LoadStyle NONE = new LoadStyle() {
	    public String toString() {
		return "NONE";
	    }
	};



}


    
