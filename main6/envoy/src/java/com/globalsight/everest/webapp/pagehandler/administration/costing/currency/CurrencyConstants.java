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
package com.globalsight.everest.webapp.pagehandler.administration.costing.currency;


public interface CurrencyConstants
{
    // Constant for saving currency in session
    public static final String CURRENCY = "currency";

    // Iso currency
    public static final String ISO_CURRENCY = "iso";
    
    // For tags
    public static final String CURRENCY_LIST = "currencies";
    public static final String CURRENCY_KEY = "currency";
    
    // Actions
    public static final String CANCEL = "cancel";
    public static final String CREATE = "create";
    public static final String EDIT = "edit";
    
    
    public static final String FILTER_CURRENCY_NAME = "currencyNameFilter";
    public static final String FILTER_CURRENCY_COMPANY = "currencyCompanyFilter";

}
