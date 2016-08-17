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
package com.globalsight.everest.projecthandler;

/**
 * MachineTranslatorExtentInfo generated by hbm2java
 */
@SuppressWarnings("serial")
public class MachineTranslationExtentInfo implements java.io.Serializable
{
    private long id;

    public MachineTranslationProfile getMtProfile()
    {
        return mtProfile;
    }

    public void setMtProfile(MachineTranslationProfile mtProfile)
    {
        this.mtProfile = mtProfile;
    }

    private MachineTranslationProfile mtProfile;
    private Long languagePairCode;
    private String languagePairName;
    private String domainCode;

    public MachineTranslationExtentInfo()
    {
    }

    public MachineTranslationExtentInfo(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Long getLanguagePairCode()
    {
        return this.languagePairCode;
    }

    public void setLanguagePairCode(Long languagePairCode)
    {
        this.languagePairCode = languagePairCode;
    }

    public String getLanguagePairName()
    {
        return this.languagePairName;
    }

    public void setLanguagePairName(String languagePairName)
    {
        this.languagePairName = languagePairName;
    }

    public String getDomainCode()
    {
        return this.domainCode;
    }

    public void setDomainCode(String domainCode)
    {
        this.domainCode = domainCode;
    }

    public String getSelfInfo()
    {
        return this.getLanguagePairName() + "@" + this.getLanguagePairCode()
                + "@" + this.getDomainCode();
    }

    public void setSelfInfo(String[] key)
    {
        this.setLanguagePairName(key[0]);
        this.setLanguagePairCode(Long.parseLong(key[1]));
        this.setDomainCode(key[2]);

    }

}