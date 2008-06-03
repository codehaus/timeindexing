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



// ReadRequest.java


package com.timeindexing.io;

import com.timeindexing.basic.Position;

/**
 * A class that represents a read request in the I/O thread.
 * It holds data on the Position being requested and a Boolean
 * stating if the data should be loaded with the IndexItem.
 */
class ReadRequest {
    Position position;
    boolean doLoadData;

    /**
     * Construct a ReadRequest.
     */
    public ReadRequest(Position p, boolean load) {
	position = p;
	doLoadData = load;
    }
}

