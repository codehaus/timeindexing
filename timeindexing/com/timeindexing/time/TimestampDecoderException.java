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



// TimestampDecoderException.java

package com.timeindexing.time;

/**
 * A TimestampDecoderException is thrown when a TimestampDecoder cannot
 * decode a value posing as a time.
 */
public class TimestampDecoderException extends RuntimeException {
    /**
     * Throw a TimestampDecoderException with no message.
     */
    public TimestampDecoderException() {
	super();
    }

    /**
     * Throw a TimestampDecoderException with a message.
     */
    public TimestampDecoderException(String s) {
	super(s);
    }
}
