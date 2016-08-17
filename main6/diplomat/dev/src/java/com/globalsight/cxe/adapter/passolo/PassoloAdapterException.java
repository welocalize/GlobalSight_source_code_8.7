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

package com.globalsight.cxe.adapter.passolo;

import com.globalsight.util.GeneralException;

public class PassoloAdapterException extends GeneralException 
{
    private static final long serialVersionUID = -1168613445594128511L;

    public PassoloAdapterException(String p_key, String[] p_args,
            Exception p_exception) 
    {
        super(p_key, p_args, p_exception, "PassoloAdapterMsg");
    }

}