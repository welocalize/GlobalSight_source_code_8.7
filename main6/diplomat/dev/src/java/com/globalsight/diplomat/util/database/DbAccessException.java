/**
 *  Copyright 2009 Welocalize, Inc. 
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */
package com.globalsight.diplomat.util.database;

import com.globalsight.exception.WrappedException;

/**
 * Represents a generic exception that might occur when any database access
 * operation fails for any reason.
 */
public class DbAccessException
    extends WrappedException
{
    //
    // PRIVATE CONSTANTS
    //
    private static final String DEFAULT_MSG = "Database Access Exception";

    //
    // PUBLIC CONSTRUCTORS
    //
    /**
     * Construct an exception with a default message.
     */
    public DbAccessException()
    {
        this(null, null);
    }

    /**
     * Construct an exception with the given message.
     *
     * @param p_message the message to ge displayed to the caller.
     */
    public DbAccessException(String p_message)
    {
        this(p_message, null);
    }

    /**
     * Construct an exception with the given exception.
     *
     * @param p_exception the exception to be wrapped.
     */
    public DbAccessException(Exception p_exception)
    {
        this(null, p_exception);
    }

    /**
     * Construct an exception with the given message, and wrap the given
     * exception.
     *
     * @param p_message the message to ge displayed to the caller.
     * @param p_exception the exception to be wrapped.
     */
    public DbAccessException(String p_message, Exception p_exception)
    {
        super((p_message == null ? DEFAULT_MSG : p_message), p_exception);
    }
}
