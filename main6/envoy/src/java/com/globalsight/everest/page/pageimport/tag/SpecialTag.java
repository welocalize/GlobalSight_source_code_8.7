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
package com.globalsight.everest.page.pageimport.tag;

public abstract class SpecialTag 
{
	public String handle(String prefixTag, String removedPrefixTag, String all, String bptTag)
	{
		if (check(prefixTag, removedPrefixTag))
		{
			return modifyTag(bptTag, all);
		}
		
		return null;
	}
	
	abstract boolean check(String prefixTag, String removedPrefixTag);
	abstract String modifyTag(String bptTag, String all);
	public abstract boolean hasSpecialTag(String gxml); 
}
