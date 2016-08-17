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

package com.globalsight.ling.lucene;

import com.globalsight.ling.lucene.Index;
import com.globalsight.ling.lucene.IndexDocument;
import com.globalsight.ling.tm2.lucene.LuceneUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.AttributeImpl;

import java.io.*;
import java.util.*;

/**
 * TmFuzzyIndex is a TM fuzzy index on segments that uses stemming
 * (or 3grams?).
 *
 * The text is indexed but not returned with the result to save space
 * (TM segments are many and long). The original text can be retrieved
 * from the TRANSLATION_UNIT table.
 */
public class TmFuzzyIndex
    extends Index
{
    public TmFuzzyIndex(String p_dbname, String p_name, String p_locale) throws IOException
    {
        super(CATEGORY_TM, p_dbname, p_name, p_locale, TYPE_TERM, TOKENIZE_STEM);
    }

    protected Document getDocument(long p_mainId, long p_subId, String p_text)
        throws IOException
    {
        return IndexDocument.IndexDocument(p_mainId, p_subId, p_text);
    }

    protected Query getQuery(String p_text)
        throws IOException
    {
        BooleanQuery result = new BooleanQuery();

        TokenStream tokens = m_analyzer.tokenStream(
            IndexDocument.TEXT, new StringReader(p_text));
        tokens.reset();
        

        Token t;
        while ((t = LuceneUtil.getNextToken(tokens)) != null)
        {
            result.add(new BooleanClause(new TermQuery(
                new Term(IndexDocument.TEXT, t.toString())), Occur.SHOULD));
        }

        return result;
    }
}