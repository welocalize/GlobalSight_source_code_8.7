/*-- 

 Copyright (C) 2000 Brett McLaughlin & Jason Hunter.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact license@jdom.org.
 
 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management (pm@jdom.org).
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Brett McLaughlin <brett@jdom.org> and 
 Jason Hunter <jhunter@jdom.org>.  For more information on the 
 JDOM Project, please see <http://www.jdom.org/>.
 
 */

package org.jdom.output;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
   
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Entity;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;

/**
 * <p><code>XMLOutputter</code> takes a JDOM tree and formats it to a
 * stream as XML.  This formatter performs typical document
 * formatting.  The XML declaration and processing instructions are
 * always on their own lines.  Empty elements are printed as
 * &lt;empty/&gt; and text-only contents are printed as
 * &lt;tag&gt;content&lt;/tag&gt; on a single line.  Constructor
 * parameters control the indent amount and whether new lines are
 * printed between elements.  The other parameters are configurable
 * through the <code>set*</code> methods.  </p>
 *
 * <p> For compact machine-readable output create a default
 * XMLOutputter and call setTrimText(true) to strip any whitespace
 * that was preserved from the source.  </p>
 *
 * <p> There are <code>output(...)</code> methods to print any of the
 * standard JDOM classes, including <code>Document</code> and
 * <code>Element</code>, to either a <code>Writer</code> or an
 * <code>OutputStream</code>.  Warning: using your own
 * <code>Writer</code> may cause the outputter's preferred character
 * encoding to be ignored.  If you use encodings other than UTF8, we
 * recommend using the method that takes an OutputStream instead.
 * </p>
 *
 * <p> The methods <code>outputString(...)</code> are for convenience
 * only; for top performance you should call <code>output(...)</code>
 * and pass in your own <code>Writer</code> or
 * <code>OutputStream</code> to if possible.  </p>
 *
 * @author Brett McLaughlin
 * @author Jason Hunter
 * @author Jason Reid
 * @author Wolfgang Werner
 * @author Elliotte Rusty Harold
 * @author David & Will (from Post Tool Design)
 * @author Dan Schaffer
 * @author Alex Chaffee (alex@jguru.com)
 * @version 1.0 */
public class XMLOutputter implements Cloneable {

    /** standard value to indent by, if we are indenting **/
    protected static final String STANDARD_INDENT = "  ";
    
    /** Whether or not to suppress the XML declaration - default is <code>false</code> */
    private boolean suppressDeclaration = false;

    /** The encoding format */
    private String encoding = "UTF8";

    /** Whether or not to output the encoding in the XML declaration - default is <code>false</code> */
    private boolean omitEncoding = false;

    /** The default indent is no spaces (as original document) */
    private String indent = null;

    /** The initial number of indentations (so you can print a whole
        document indented, if you like) **/
    // kind of dangerous having same name for instance and local
    // variable, but that's OK...
    private int indentLevel = 0;
    
    /** Whether or not to expand empty elements to &lt;tagName&gt;&lt;/tagName&gt; - default is <code>false</code> */
    private boolean expandEmptyElements = false;

    /** The default new line flag, set to do new lines only as in original document */
    private boolean newlines = false;

    /** New line separator */
    private String lineSeparator = "\r\n";

    /** should we preserve whitespace or not in text nodes? */
    private boolean trimText = false;

    /**
     * <p>
     * This will create an <code>XMLOutputter</code> with
     *   no additional whitespace (indent or new lines) added;
     *   the whitespace from the element text content is fully preserved.
     * </p>
     */
    public XMLOutputter() {
    }

    /**
     * <p>
     * This will create an <code>XMLOutputter</code> with
     *   the given indent added but no new lines added;
     *   all whitespace from the element text content is included as well.
     * </p>
     *
     * @param indent  the indent string, usually some number of spaces
     */
    public XMLOutputter(String indent) {
       this.indent = indent;
    }

    /**
     * <p>
     * This will create an <code>XMLOutputter</code> with
     *   the given indent that prints newlines only if <code>newlines</code> is
     *   <code>true</code>; 
     *   all whitespace from the element text content is included as well.
     * </p>
     *
     * @param indent the indent <code>String</code>, usually some number
     *        of spaces
     * @param newlines <code>true</code> indicates new lines should be
     *                 printed, else new lines are ignored (compacted).
     */
    public XMLOutputter(String indent, boolean newlines) {
       this.indent = indent;
       this.newlines = newlines;
    }

    /**
     * <p>
     * This will create an <code>XMLOutputter</code> with
     *   the given indent and new lines printing only if newlines is
     *   <code>true</code>, and encoding format <code>encoding</code>.
     * </p>
     *
     * @param indent the indent <code>String</code>, usually some number
     *        of spaces
     * @param newlines <code>true</code> indicates new lines should be
     *                 printed, else new lines are ignored (compacted).
     * @param encoding set encoding format.
     */
    public XMLOutputter(String indent, boolean newlines, String encoding) {
       this.indent = indent;
       this.newlines = newlines;
       this.encoding = encoding;
    }

    /**
     * <p> This will create an <code>XMLOutputter</code> with all the
     * options as set in the given <code>XMLOutputter</code>.  Note
     * that <code>XMLOutputter two = (XMLOutputter)one.clone();</code>
     * would work equally well.  </p>
     *
     * @param that the XMLOutputter to clone
     **/
    public XMLOutputter(XMLOutputter that) {
        this.suppressDeclaration = that.suppressDeclaration;
        this.omitEncoding = that.omitEncoding;
        this.indent = that.indent;
        this.indentLevel = that.indentLevel;
        this.expandEmptyElements = that.expandEmptyElements;
        this.newlines = that.newlines;
        this.encoding = that.encoding;
        this.lineSeparator = that.lineSeparator;
        this.trimText = that.trimText;      
    }
    
    /**
     * <p>This will set the new-line separator. The default is
     * <code>\r\n</code>. Note that if the "newlines" property is
     * false, this value is irrelevant.  </p>
     * 
     * <blockquote>
     *  We could change this to the System default, 
     *  but I prefer not to make output platform dependent.
     *  A carriage return, linefeed pair is the most generally
     *  acceptable linebreak.  Another possibility is to use
     *  only a line feed, which is XML's preferred (but not required)
     *  solution. However, both carriage return and linefeed are
     *  required for many network protocols, and the parser on the
     *  other end should normalize this.  --Rusty
     * </blockquote>
     *
     * @see #setNewlines(boolean)
     * @param separator <code>String</code> line separator to use.
     **/
    public void setLineSeparator(String separator) {
        lineSeparator = separator;
    }

    /**
     * @see #setLineSeparator(String)
     * @param newlines <code>true</code> indicates new lines should be
     *                 printed, else new lines are ignored (compacted).
     **/
    public void setNewlines(boolean newlines) {
        this.newlines = newlines;
    }

    /**
     * @param encoding encoding format
     **/
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * <p>
     *  This will set whether the XML declaration (<code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;</code>)
     *    includes the encoding of the document. It is common to suppress this in uses such
     *    as WML and other wireless device protocols.
     * </p>
     *
     * @param omitEncoding <code>boolean</code> indicating whether or not
     *        the XML declaration should indicate the document encoding.
     */
    public void setOmitEncoding(boolean omitEncoding) {
        this.omitEncoding = omitEncoding;
    }

    /**
     * <p>
     *  This will set whether the XML declaration (<code>&lt;?xml version="1.0"?&gt;</code>)
     *    will be suppressed or not. It is common to suppress this in uses such
     *    as SOAP and XML-RPC calls.
     * </p>
     *
     * @param suppressDeclaration <code>boolean</code> indicating whether or not
     *        the XML declaration should be suppressed.
     */
    public void setSuppressDeclaration(boolean suppressDeclaration) {
        this.suppressDeclaration = suppressDeclaration;
    }

    /**
     * <p>
     *  This will set whether empty elements are expanded from <code>&lt;tagName&gt;</code> to
     *    <code>&lt;tagName&gt;&lt;/tagName&gt;</code>.
     * </p>
     *
     * @param expandEmptyElements <code>boolean</code> indicating whether or not
     *        empty elements should be expanded.
     */
    public void setExpandEmptyElements(boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
    }

    /**
     * <p> This will set whether the text is output verbatim (false)
     *  or with whitespace stripped as per <code>{@link
     *  org.jdom.Element#getTextTrim()}</code>.<p>
     *
     * <p>Default: false </p>
     *
     * @param trimText <code>boolean</code> true=>trim the whitespace, false=>use text verbatim
     **/
    public void setTrimText(boolean trimText) {
        this.trimText = trimText;
    }

    /**
     * <p> This will set the indent <code>String</code> to use; this
     *   is usually a <code>String</code> of empty spaces. If you pass
     *   null, or the empty string (""), then no indentation will
     *   happen. </p>
     * Default: none (null)
     *
     * @param indent <code>String</code> to use for indentation.
     **/
    public void setIndent(String indent) {
        // if passed the empty string, change it to null, for marginal
        // performance gains later (can compare to null first instead
        // of calling equals())
        if ("".equals(indent))
            indent = null;
        this.indent = indent;
    }

    /**
     * Set the indent on or off.  If setting on, will use the value of
     * STANDARD_INDENT, which is usually two spaces.
     *
     * @param doIndent if true, set indenting on; if false, set indenting off
     **/
    public void setIndent(boolean doIndent) {
        if (doIndent) { 
            this.indent = STANDARD_INDENT;
        }
        else {
            this.indent = null;
        }
    }

    /**
     * Set the initial indentation level.  This can be used to output
     * a document (or, more likely, an element) starting at a given
     * indent level, so it's not always flush against the left margin.
     * Default: 0
     *
     * @param indentLevel the number of indents to start with
     **/
    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }
    
    /**
     * <p>
     *   This will set the indent <code>String</code>'s size; an indentSize
     *    of 4 would result in the indention being equivalent to the <code>String</code>
     *    "&nbsp;&nbsp;&nbsp;&nbsp;" (four space characters).
     * </p>
     *
     * @param indentSize <code>int</code> number of spaces in indentation.
     */
    public void setIndentSize(int indentSize) {
        StringBuffer indentBuffer = new StringBuffer();
        for (int i=0; i<indentSize; i++) {
            indentBuffer.append(" ");
        }
        this.indent = indentBuffer.toString();
    }

    /**
     * <p>
     * This will print the proper indent characters for the given indent level.
     * </p>
     *
     * @param out <code>Writer</code> to write to
     * @param level <code>int</code> indentation level
     */
    protected void indent(Writer out, int level) throws IOException {
        if (indent != null && !indent.equals("")) {
            for (int i = 0; i < level; i++) {
                out.write(indent);
            }
        }
    }
    
    /**
     * <p>
     * This will print a new line only if the newlines flag was set to true
     * </p>
     *
     * @param out <code>Writer</code> to write to
     */
    protected void maybePrintln(Writer out) throws IOException  {
        if (newlines) {
            out.write(lineSeparator);
        }
    }

    /**
     * Get an OutputStreamWriter, use preferred encoding.
     */
    protected Writer makeWriter(OutputStream out) throws java.io.UnsupportedEncodingException {
        Writer writer = new OutputStreamWriter
            (new BufferedOutputStream(out), this.encoding);
        return writer;
    }
    
    /**
     * Get an OutputStreamWriter, use specified encoding.
     */
    protected Writer makeWriter(OutputStream out, String encoding) throws java.io.UnsupportedEncodingException {
        Writer writer = new OutputStreamWriter
            (new BufferedOutputStream(out), encoding);
        return writer;
    }
    
    /**
     * <p>
     * This will print the <code>Document</code> to the given output stream.
     *   The characters are printed using the encoding specified in the
     *   constructor, or a default of UTF-8.
     * </p>
     *
     * @param doc <code>Document</code> to format.
     * @param out <code>OutputStream</code> to write to.
     * @throws <code>IOException</code> - if there's any problem writing.
     */
    public void output(Document doc, OutputStream out)
                                           throws IOException {
        Writer writer = makeWriter(out);
        output(doc, writer);
        writer.flush();
    }

    /**
     * <p> This will print the <code>Document</code> to the given
     * Writer.
     * </p>
     *
     * <p> Warning: using your own Writer may cause the outputter's
     * preferred character encoding to be ignored.  If you use
     * encodings other than UTF8, we recommend using the method that
     * takes an OutputStream instead.  </p>
     *
     * <p>Note: as with all Writers, you may need to flush() yours
     * after this method returns.</p>
     *
     * @param doc <code>Document</code> to format.
     * @param out <code>Writer</code> to write to.
     * @throws <code>IOException</code> - if there's any problem writing.
     **/
    public void output(Document doc, Writer writer)
                                           throws IOException {
        // Print out XML declaration
        if (indentLevel>0)
            indent(writer, indentLevel);        
        printDeclaration(doc, writer, encoding);

        if (doc.getDocType() != null) {
            if (indentLevel>0)
                indent(writer, indentLevel);
            printDocType(doc.getDocType(), writer);
        }
        
        // Print out root element, as well as any root level
        // comments and processing instructions, 
        // starting with no indentation
        Iterator i = doc.getMixedContent().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Element) {
                output(doc.getRootElement(), writer);   // outputs at initial indentLevel
            } else if (obj instanceof Comment) {
                printComment((Comment) obj, writer, indentLevel);
            } else if (obj instanceof ProcessingInstruction) {
                printProcessingInstruction((ProcessingInstruction) obj, writer, indentLevel);
            } else if (obj instanceof CDATA) {
                printCDATASection((CDATA)obj, writer, indentLevel);
            }
        }
    }

    // output element
    
    /**
     * <p>
     * Print out an <code>{@link Element}</code>, including
     *   its <code>{@link Attribute}</code>s, and its value, and all
     *   contained (child) elements etc.
     * </p>
     *
     * @param element <code>Element</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(Element element, Writer out)
        throws IOException
    {
        // if this is the root element we could pre-initialize the namespace stack
        // with the namespaces
        printElement(element, out, indentLevel, new NamespaceStack());
    }
    
    /**
     * <p>
     * Print out an <code>{@link Element}</code>, including
     *   its <code>{@link Attribute}</code>s, and its value, and all
     *   contained (child) elements etc.
     * </p>
     *
     * @param element <code>Element</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(Element element, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        output(element, writer);
        writer.flush();         // Flush the output to the underlying stream
    }

    // output cdata

    /**
     * <p>
     * Print out a <code>{@link CDATA}</code>
     * </p>
     *
     * @param cdata <code>CDATA</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(CDATA cdata, Writer out)
        throws IOException
    {
        printCDATASection(cdata, out, indentLevel);
    }
    
    /**
     * <p>
     * Print out a <code>{@link CDATA}</code>
     * </p>
     *
     * @param cdata <code>CDATA</code> to output.
     * @param out <code>OutputStream</code> to write to.
     **/
    public void output(CDATA cdata, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        output(cdata, writer);
        writer.flush();         // Flush the output to the underlying stream
    }

    // output comment

    /**
     * <p>
     * Print out a <code>{@link Comment}</code>
     * </p>
     *
     * @param comment <code>Comment</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(Comment comment, Writer out)
        throws IOException
    {
        printComment(comment, out, indentLevel);
    }
    
    /**
     * <p>
     * Print out a <code>{@link Comment}</code>
     * </p>
     *
     * @param comment <code>Comment</code> to output.
     * @param out <code>OutputStream</code> to write to.
     **/
    public void output(Comment comment, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        output(comment, writer);
        writer.flush();         // Flush the output to the underlying stream
    }
    

    // output String

    /**
     * <p> Print out a <code>{@link java.lang.String}</code>.  Perfoms
     * the necessary entity escaping and whitespace stripping.  </p>
     *
     * @param string <code>String</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(String string, Writer out)
        throws IOException
    {
        printString(string, out);
    }
    
    /**
     * <p>
     * <p> Print out a <code>{@link java.lang.String}</code>.  Perfoms
     * the necessary entity escaping and whitespace stripping.  </p>
     * </p>
     *
     * @param cdata <code>CDATA</code> to output.
     * @param out <code>OutputStream</code> to write to.
     **/
    public void output(String string, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        printString(string, writer);
        writer.flush();         // Flush the output to the underlying stream
    }
    
    // output Entity

    /**
     * <p> Print out an <code>{@link Entity}</code>.  
     * </p>
     *
     * @param entity <code>Entity</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(Entity entity, Writer out)
        throws IOException
    {
        printEntity(entity, out);
    }
    
    /**
     * <p>
     * Print out an <code>{@link Entity}</code>. 
     * </p>
     *
     * @param cdata <code>CDATA</code> to output.
     * @param out <code>OutputStream</code> to write to.
     **/
    public void output(Entity entity, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        printEntity(entity, writer);
        writer.flush();         // Flush the output to the underlying stream
    }
    

    // output processingInstruction

    /**
     * <p>
     * Print out a <code>{@link ProcessingInstruction}</code>
     * </p>
     *
     * @param element <code>ProcessingInstruction</code> to output.
     * @param out <code>Writer</code> to write to.
     **/
    public void output(ProcessingInstruction processingInstruction, Writer out)
        throws IOException
    {
        printProcessingInstruction(processingInstruction, out, indentLevel);
    }
    
    /**
     * <p>
     * Print out a <code>{@link ProcessingInstruction}</code>
     * </p>
     *
     * @param processingInstruction <code>ProcessingInstruction</code> to output.
     * @param out <code>OutputStream</code> to write to.
     **/
    public void output(ProcessingInstruction processingInstruction, OutputStream out)
        throws IOException
    {
        Writer writer = makeWriter(out);
        output(processingInstruction, writer);
        writer.flush();         // Flush the output to the underlying stream
    }
    

    // output as string
    
    /**
     * Return a string representing a document.  Uses an internal
     * StringWriter. Warning: a String is Unicode, which may not match
     * the outputter's specified encoding.
     *
     * @param doc <code>Document</code> to format.
     **/
    public String outputString(Document doc) throws IOException {
        StringWriter out = new StringWriter();
        output(doc, out);
        out.flush();
        return out.toString();
    }

    /**
     * Return a string representing an element. Warning: a String is
     * Unicode, which may not match the outputter's specified
     * encoding.
     *
     * @param doc <code>Element</code> to format.
     **/
    public String outputString(Element element) throws IOException {
        StringWriter out = new StringWriter();
        output(element, out);
        out.flush();
        return out.toString();
    }

    // internal printing methods
    
    /**
     * <p>
     * This will write the declaration to the given Writer.
     *   Assumes XML version 1.0 since we don't directly know.
     * </p>
     *
     * @param docType <code>DocType</code> whose declaration to write.
     * @param out <code>Writer</code> to write to.
     */
    public void printDeclaration(Document doc,
                                    Writer out,
                                    String encoding)  throws IOException {

        // Only print of declaration is not suppressed
        if (!suppressDeclaration) {
            // Assume 1.0 version
            if (encoding.equals("UTF8")) {
                out.write("<?xml version=\"1.0\"");
                if (!omitEncoding) {
                    out.write(" encoding=\"UTF-8\"");
                }
                out.write("?>");
            } else {
                out.write("<?xml version=\"1.0\"");
                if (!omitEncoding) {
                    out.write(" encoding=\"" + encoding + "\"");
                }
                out.write("?>");
            }

            maybePrintln(out);
        }        
    }    

    /**
     * <p>
     * This will write the DOCTYPE declaration if one exists.
     * </p>
     *
     * @param doc <code>Document</code> whose declaration to write.
     * @param out <code>Writer</code> to write to.
     */
    public void printDocType(DocType docType, Writer out)  throws IOException {
        if (docType == null) {
            return;
        }

        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();
        boolean hasPublic = false;

        out.write("<!DOCTYPE ");
        out.write(docType.getElementName());
        if ((publicID != null) && (!publicID.equals(""))) {
            out.write(" PUBLIC \"");
            out.write(publicID);
            out.write("\"");
            hasPublic = true;
        }
        if ((systemID != null) && (!systemID.equals(""))) {
            if (!hasPublic) {
                out.write(" SYSTEM");
            }
            out.write(" \"");
            out.write(systemID);
            out.write("\"");
        }
        out.write(">");
        maybePrintln(out);
    }

    /**
     * <p>
     * This will write the comment to the specified writer.
     * </p>
     *
     * @param comment <code>Comment</code> to write.
     * @param out <code>Writer</code> to write to.
     * @param indentLevel Current depth in hierarchy.
     */
    protected void printComment(Comment comment,
                             Writer out, int indentLevel) throws IOException
    {
        indent(out, indentLevel);
        out.write(comment.getSerializedForm());  //XXX
        maybePrintln(out);
    }
      
    /**
     * <p>
     * This will write the processing instruction to the specified writer.
     * </p>
     *
     * @param comment <code>ProcessingInstruction</code> to write.
     * @param out <code>Writer</code> to write to.
     * @param indentLevel Current depth in hierarchy.
     */
    protected void printProcessingInstruction(ProcessingInstruction pi,
                                    Writer out, int indentLevel) throws IOException {
                                        
        indent(out, indentLevel);
        out.write(pi.getSerializedForm());
        maybePrintln(out);

    }
    
    /**
     * <p>
     * This will handle printing out an <code>{@link CDATA}</code>,
     *   and its value.
     * </p>
     *
     * @param cdata <code>CDATA</code> to output.
     * @param out <code>Writer</code> to write to.
     * @param indent <code>int</code> level of indention.
     */
    protected void printCDATASection(CDATA cdata,
                              Writer out, int indentLevel) throws IOException {
        
        indent(out, indentLevel);
        out.write(cdata.getSerializedForm());
        maybePrintln(out);

    }

    /**
     * <p>
     * This will handle printing out an <code>{@link Element}</code>,
     *   its <code>{@link Attribute}</code>s, and its value.
     * </p>
     *
     * @param element <code>Element</code> to output.
     * @param out <code>Writer</code> to write to.
     * @param indent <code>int</code> level of indention.
     * @param namespaces <code>List</code> stack of Namespaces in scope.
     */
    protected void printElement(Element element, Writer out,
                                int indentLevel, NamespaceStack namespaces)  throws IOException {

        List mixedContent = element.getMixedContent();

        boolean empty = mixedContent.size() == 0;
        boolean stringOnly =
            !empty &&
            mixedContent.size() == 1 &&
            mixedContent.get(0) instanceof String;

        // Print beginning element tag
        /* maybe the doctype, xml declaration, and processing instructions 
           should only break before and not after; then this check is unnecessary,
           or maybe the println should only come after and never before. 
           Then the output always ends with a newline */
           
        indent(out, indentLevel);

        // Print the beginning of the tag plus attributes and any
        // necessary namespace declarations
        out.write("<");
        out.write(element.getQualifiedName());
        int previouslyDeclaredNamespaces = namespaces.size();
        Namespace ns = element.getNamespace();
        if (ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
            String prefix = ns.getPrefix();        
            String uri = namespaces.getURI(prefix);
            if (!ns.getURI().equals(uri)) { // output a new namespace declaration
                namespaces.push(ns);
                printNamespace(ns, out);
            }
        }

        printAttributes(element.getAttributes(), element, out, namespaces);

        // handle "" string same as empty
        if (stringOnly) {
            String elementText =
                trimText ? element.getTextTrim() : element.getText();
            if (elementText == null ||
                elementText.equals("")) {
                empty = true;
            }
        }
        
        if (empty) {
            // Simply close up
            if (!expandEmptyElements) {
                out.write(" />");
            } else {
                out.write("></");
                out.write(element.getQualifiedName());
                out.write(">");
            }
            maybePrintln(out);
        } else {
            // we know it's not null or empty from above
            out.write(">");

            if (stringOnly) {
                // if string only, print content on same line as tags
                printElementContent(element, out, indentLevel, namespaces, mixedContent);
            }
            else {
                maybePrintln(out);
                printElementContent(element, out, indentLevel, namespaces, mixedContent);
                indent(out, indentLevel);               
            }

            out.write("</");
            out.write(element.getQualifiedName());
            out.write(">");

            maybePrintln(out);
        }

        // remove declared namespaces from stack
        while (namespaces.size() > previouslyDeclaredNamespaces) namespaces.pop();
    }
    
    /**
     * <p> This will handle printing out an <code>{@link
     * Element}</code>'s content only, not including its tag,
     * attributes, and namespace info.  </p>
     *
     * @param element <code>Element</code> to output.
     * @param out <code>Writer</code> to write to.
     * @param indent <code>int</code> level of indention.  */
    public void printElementContent(Element element, Writer out)
        throws IOException
    {
        List mixedContent = element.getMixedContent();
        printElementContent(element, out, indentLevel,
                            new NamespaceStack(),
                            mixedContent);
    }

    /**
     * <p> This will handle printing out an <code>{@link
     * Element}</code>'s content only, not including its tag,
     * attributes, and namespace info.  </p>
     *
     * @param element <code>Element</code> to output.
     * @param out <code>Writer</code> to write to.
     * @param indent <code>int</code> level of indention.  */
    protected void printElementContent(Element element, Writer out,
                                       int indentLevel,
                                       NamespaceStack namespaces,
                                       List mixedContent
                                       )  throws IOException
    {
        // get same local flags as printElement does
        // a little redundant code-wise, but not performance-wise
        boolean empty = mixedContent.size() == 0;
        boolean stringOnly =
            !empty &&
            (mixedContent.size() == 1) &&
            mixedContent.get(0) instanceof String;

        if (stringOnly) {
            // Print the tag  with String on same line
            // Example: <tag name="value">content</tag>
            String elementText =
                trimText ? element.getTextTrim() : element.getText();
            
            out.write(escapeElementEntities(elementText));
            
        } else {
            /**
             * Print with children on future lines
             * Rather than check for mixed content or not, just print
             * Example: <tag name="value">
             *             <child/>
             *          </tag>
             */
            // Iterate through children
            Object content = null;
            for (int i=0, size=mixedContent.size(); i<size; i++) {
                content = mixedContent.get(i);
                // See if text, an element, a processing instruction or a comment
                if (content instanceof Comment) {
                    printComment((Comment) content, out, indentLevel + 1);
                } else if (content instanceof String) {
                    printString((String)content, out);
                } else if (content instanceof Element) {
                    printElement((Element) content, out, indentLevel + 1, namespaces);
                } else if (content instanceof Entity) {
                    printEntity((Entity) content, out);
                } else if (content instanceof ProcessingInstruction) {
                    printProcessingInstruction((ProcessingInstruction) content, out, indentLevel + 1);
                } else if (content instanceof CDATA) {
                    printCDATASection((CDATA)content, out, indentLevel + 1);
                }
                // Unsupported types are *not* printed
            }
        }
    }  // printElementContent


    /**
     * Print a string.  Escapes the element entities, trims interior
     * whitespace if necessary.
     **/     
    protected void printString(String s, Writer out) throws IOException {
        s = escapeElementEntities(s);
        // patch by Brad Morgan to strip interior whitespace
        // (Brad.Morgan@e-pubcorp.com)
        if (trimText) {
            StringTokenizer tokenizer = new StringTokenizer(s);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                out.write(token);
                if (tokenizer.hasMoreTokens()) {
                    out.write(" ");
                }
            }
        } else {                    
            out.write(s);
        }
    }
    
    /**
     * <p>
     * This will handle printing out an <code>{@link Entity}</code>.
     * Only the entity reference such as <code>&amp;entity;</code>
     * will be printed. However, subclasses are free to override 
     * this method to print the contents of the entity instead.
     * </p>
     *
     * @param entity <code>Entity</code> to output.
     * @param out <code>Writer</code> to write to.  */
    protected void printEntity(Entity entity, Writer out) throws IOException {
        out.write(entity.getSerializedForm());
    }
    

    /**
     * <p>
     *  This will handle printing out any needed <code>{@link Namespace}</code>
     *    declarations.
     * </p>
     *
     * @param ns <code>Namespace</code> to print definition of
     * @param out <code>Writer</code> to write to.
     */
    protected void printNamespace(Namespace ns, Writer out) throws IOException {
        out.write(" xmlns");
        if (!ns.getPrefix().equals("")) {
            out.write(":");
            out.write(ns.getPrefix());
        }
        out.write("=\"");
        out.write(ns.getURI());
        out.write("\"");
    }

    /**
     * <p>
     * This will handle printing out an <code>{@link Attribute}</code> list.
     * </p>
     *
     * @param attributes <code>List</code> of Attribute objcts
     * @param out <code>Writer</code> to write to
     */
    protected void printAttributes(List attributes, Element parent, 
                                   Writer out, NamespaceStack namespaces) 
      throws IOException {

        // I do not yet handle the case where the same prefix maps to
        // two different URIs. For attributes on the same element
        // this is illegal; but as yet we don't throw an exception
        // if someone tries to do this
        Set prefixes = new HashSet();

        for (int i=0, size=attributes.size(); i < size; i++) {
            Attribute attribute = (Attribute)attributes.get(i);
            Namespace ns = attribute.getNamespace();
            if (ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
                String prefix = ns.getPrefix();           
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) { // output a new namespace declaration
                    printNamespace(ns, out);
                    namespaces.push(ns);
                }
            }
            
            out.write(" ");
            out.write(attribute.getQualifiedName());
            out.write("=");

            out.write("\"");
            out.write(escapeAttributeEntities(attribute.getValue()));
            out.write("\"");
        }
        
    }

    /**
     * <p>
     * This will take the five pre-defined entities in XML 1.0 and
     *   convert their character representation to the appropriate
     *   entity reference, suitable for XML attributes.
     * </p>
     *
     * @param st <code>String</code> input to escape.
     * @return <code>String</code> with escaped content.
     */
    private String escapeAttributeEntities(String st) {
        StringBuffer buff = new StringBuffer();
        char[] block = st.toCharArray();
        String stEntity = null;
        int i, last;

        for (i=0, last=0; i < block.length; i++) {
            switch(block[i]) {
                case '<' :
                    stEntity = "&lt;";
                    break;
                case '>' :
                    stEntity = "&gt;";
                    break;
                case '\'' :
                    stEntity = "&apos;";
                    break;
                case '\"' :
                    stEntity = "&quot;";
                    break;
                case '&' :
                    stEntity = "&amp;";
                    break;
                default :
                    /* no-op */ ;
            }
            if (stEntity != null) {
                buff.append(block, last, i - last);
                buff.append(stEntity);
                stEntity = null;
                last = i + 1;
            }
        }
        if(last < block.length) {
            buff.append(block, last, i - last);
        }

        return buff.toString();
    }


    /**
     * <p>
     * This will take the three pre-defined entities in XML 1.0
     *   (used specifically in XML elements) and
     *   convert their character representation to the appropriate
     *   entity reference, suitable for XML element.
     * </p>
     *
     * @param st <code>String</code> input to escape.
     * @return <code>String</code> with escaped content.
     */
    private String escapeElementEntities(String st) {
        StringBuffer buff = new StringBuffer();
        char[] block = st.toCharArray();
        String stEntity = null;
        int i, last;

        for (i=0, last=0; i < block.length; i++) {
            switch(block[i]) {
                case '<' :
                    stEntity = "&lt;";
                    break;
                case '>' :
                    stEntity = "&gt;";
                    break;
                case '&' :
                    stEntity = "&amp;";
                    break;
                 case '"' : // added by JEH
                    stEntity = "&quot;";
                    break;
                 case '\'' : // added by JEH
                    stEntity = "&apos;";
                    break;
                default :
                    /* no-op */ ;
            }
            if (stEntity != null) {
                buff.append(block, last, i - last);
                buff.append(stEntity);
                stEntity = null;
                last = i + 1;
            }
        }
        if(last < block.length) {
            buff.append(block, last, i - last);
        }

        return buff.toString();
    }

    /**
     * parse command-line arguments of the form <code>-omitEncoding
     * -indentSize 3 ...</code>
     * @return int index of first parameter that we didn't understand
     **/      
    public int parseArgs(String[] args, int i) {
        for (; i<args.length; ++i) {
            if (args[i].equals("-suppressDeclaration")) {
                setSuppressDeclaration(true);
            }
            else if (args[i].equals("-omitEncoding")) {
                setOmitEncoding(true);
            }
            else if (args[i].equals("-indent")) {
                setIndent(args[++i]);
            }
            else if (args[i].equals("-indentSize")) {
                setIndentSize(Integer.parseInt(args[++i]));
            }
            else if (args[i].equals("-indentLevel")) {
                setIndentLevel(Integer.parseInt(args[++i]));
            }
            else if (args[i].startsWith("-expandEmpty")) {
                setExpandEmptyElements(true);
            }
            else if (args[i].equals("-encoding")) {
                setEncoding(args[++i]);
            }
            else if (args[i].equals("-newlines")) {
                setNewlines(true);
            }
            else if (args[i].equals("-lineSeparator")) {
                setLineSeparator(args[++i]);
            }
            else if (args[i].equals("-trimText")) {
                setTrimText(true);
            }
            else {
                return i;
            }
        }
        return i;
    } // parseArgs
    
}

class NamespaceStack {
 
    private Stack prefixes = new Stack();
    private Stack uris = new Stack();        
  
    public void push(Namespace ns) {
        prefixes.push(ns.getPrefix());
        uris.push(ns.getURI());
    }      
    
    public void pop() {      
        String s = (String) prefixes.pop();
        uris.pop();
    }
    
    public int size() {
        return prefixes.size();     
    }    
  
    // find the URI matching the nearest prefix
    public String getURI(String prefix) {
       int index = prefixes.lastIndexOf(prefix);
       if (index == -1) return null;
       String s = (String) uris.elementAt(index);
       return s;       
    }
    
    /* For debugging...
    public void printStack() {
        System.out.println("Stack: " + prefixes.size());
        for (int i = 0; i < prefixes.size(); i++) {
            System.out.println(prefixes.elementAt(i) + "&" + uris.elementAt(i));
        }        
    }  
    */
}