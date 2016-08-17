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
package com.globalsight.cxe.adaptermdb.catalyst;

import com.globalsight.cxe.adapter.BaseAdapter;
import com.globalsight.cxe.adapter.catalyst.CatalystAdapter;
import com.globalsight.cxe.adaptermdb.BaseAdapterMDB;

/**
 * CatalystTargetAdapterMDB uses the CatalystAdapter
 */
public class CatalystTargetAdapterMDB extends BaseAdapterMDB
{
    private static String ADAPTER_NAME = CatalystTargetAdapterMDB.class.getName();

    protected String getAdapterName()
    {
        return ADAPTER_NAME;
    }

    protected BaseAdapter loadAdapter() throws Exception
    {
        return new CatalystAdapter(ADAPTER_NAME);
    }
}
