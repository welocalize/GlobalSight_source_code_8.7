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

package org.jdom;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * <p>
 * <code>Document</code> defines behavior for an XML Document, modeled
 *   in Java.  Methods allow the user to the root element as well
 *   as processing instructions and other document-level information.
 * </p>
 *
 * @author Brett McLaughlin
 * @author Jason Hunter
 * @version 1.0
 */
public class Document implements Serializable, Cloneable {

    /**
     * This <code>Document</code>'s
     *   <code>{@link Comment}</code>s,  
     *   <code>{@link ProcessingInstruction}</code>s and
     *   the root <code>{@link Element}</code>
     */
    protected List content;

    /**
     * The root <code>{@link Element}</code>
     *   of the <code>Document</code>.
     */
    protected Element rootElement;

    /** The <code>{@link DocType}</code> declaration */
    protected DocType docType;

    /**
     * <p>
     * Default, no-args constructor for implementations
     *   to use if needed.
     * </p>
     */
    // XXX Should this be public, to avoid use of Document(null)?
    protected Document() {}

    /**
     * <p>
     * This will create a new <code>Document</code>,
     *   with the supplied <code>{@link Element}</code>
     *   as the root element and the supplied
     *   <code>{@link DocType}</code> declaration.
     * </p>
     *
     * @param rootElement <code>Element</code> for document root.
     * @param docType <code>DocType</code> declaration.
     */
    public Document(Element rootElement, DocType docType) {
        this.rootElement = rootElement;
        this.docType = docType;
        content = new LinkedList();

        if (rootElement != null) {
            rootElement.setDocument(this);
            content.add(rootElement);
        }
    }

    /**
     * <p>
     * This will create a new <code>Document</code>,
     *   with the supplied <code>{@link Element}</code>
     *   as the root element, and no <code>{@link DocType}</code>
     *   declaration.
     * </p>
     *
     * @param rootElement <code>Element</code> for document root
     */
    public Document(Element rootElement) {
        this(rootElement, null);
    }

    /**
     * <p>
     * This will return the root <code>Element</code>
     *   for this <code>Document</code>, or return null in the case the
     *   root element hasn't been yet set.
     * </p>
     *
     * @return <code>Element</code> - the document's root element, or
     *    null if none has been yet set
     */
    public Element getRootElement() {
        return rootElement;
    }

    /**
     * <p>
     * This sets the root <code>{@link Element}</code> for the
     *   <code>Document</code>.
     * </p>
     *
     * @param rootElement <code>Element</code> to be new root.
     * @return <code>Document</code> - modified Document.
     */
    public Document setRootElement(Element rootElement) {
        // If existing root element, tell it that it's no longer root
        // and remove it from the content list
        int rootLocation = content.size();  // default add to end
        if (this.rootElement != null) {
            this.rootElement.setDocument(null);
            rootLocation = content.indexOf(this.rootElement);
            content.remove(rootLocation);
        }

        if (rootElement != null) {
            rootElement.setDocument(this);
            content.add(rootLocation, rootElement);
        }

        this.rootElement = rootElement;

        return this;
    }

    /**
     * <p>
     * This will return the <code>{@link DocType}</code>
     *   declaration for this <code>Document</code>, or
     *   <code>null</code> if none exists.
     * </p>
     *
     * @return <code>DocType</code> - the DOCTYPE declaration.
     */
    public DocType getDocType() {
        return docType;
    }

    /**
     * <p>
     * This will set the <code>{@link DocType}</code>
     *   declaration for this <code>Document</code>.
     * </p>
     *
     * @param docType <code>DocType</code> declaration.
     */
    public Document setDocType(DocType docType) {
        this.docType = docType;

        return this;
    }

    /**
     * <p>
     * This will return the list of
     *   <code>{@link ProcessingInstruction}</code>s
     *   for this <code>Document</code> located at the document level 
     *   (outside the root element).
     * The returned list is "live" and changes to it affect the
     * document's actual content.
     * </p>
     *
     * @return <code>List</code> - PIs for document.
     */
    public List getProcessingInstructions() {
        PartialList pis = new PartialList(content);

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof ProcessingInstruction) {
                pis.addPartial(obj);
            }
        }

        return pis;
    }

    /**
     * <p>
     * This returns the processing instructions for this
     *   <code>Document</code> located at the document level
     *   (outside the root element) which have the supplied target.
     * The returned list is "live" and changes to it affect the
     * document's actual content.
     * </p>
     *
     * @param target <code>String</code> target of PI to return.
     * @return <code>List</code> - all PIs with the specified
     *         target.
     */
    public List getProcessingInstructions(String target) {
        PartialList pis = new PartialList(content);

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof ProcessingInstruction) {
                if (((ProcessingInstruction)obj).getTarget().equals(target)) {
                    pis.addPartial(obj);
                }
            }
        }

        return pis;
    }

    /**
     * <p>
     * This returns the first processing instruction for this
     *   <code>Document</code> located at the document level 
     *   (outside the root element) for the supplied target, or null if
     *   no such processing instruction exists.
     * </p>
     *
     * @param target <code>String</code> target of PI to return.
     * @return <code>ProcessingInstruction</code> - the first PI
     *         with the specified target, or null if no such PI exists.
     */
    public ProcessingInstruction getProcessingInstruction(String target) {

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof ProcessingInstruction) {
                if (((ProcessingInstruction)obj).getTarget().equals(target)) {
                    return (ProcessingInstruction)obj;
                }
            }
        }

        // If we got here, none found
        return null;
    }

    /**
     * <p>
     * This will remove the first PI with the specified target.
     * </p>
     *
     * @param target <code>String</code> target of PI to remove.
     * @return <code>boolean</code> - whether the requested PI was removed.
     */
    public boolean removeProcessingInstruction(String target) {
        ProcessingInstruction pi = getProcessingInstruction(target);
        if (pi == null) {
            return false;
        }
        else {
            return content.remove(pi);
        }
    }

    /**
     * <p>
     * This will remove all PIs with the specified target.
     * </p>
     *
     * @param target <code>String</code> target of PI to remove.
     * @return <code>boolean</code> - whether the requested PIs were removed.
     */
    public boolean removeProcessingInstructions(String target) {
        boolean deletedSome = false;

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof ProcessingInstruction) {
                if (((ProcessingInstruction)obj).getTarget().equals(target)) {
                    deletedSome = true;
                    i.remove();
                }
            }
        }

        return deletedSome;
    }

    /**
     * <p>
     * This sets the PIs for this <code>Document</code> to those in the
     *   <code>List</code supplied (removing all other PIs).
     * </p>
     *
     * @param pis <code>List</code> of PIs to use.
     * @return <code>Document</code> - this Document modified.
     */
    public Document setProcessingInstructions(List pis) {

        List current = getProcessingInstructions();
        for (Iterator i = current.iterator(); i.hasNext(); ) {
            i.remove();
        }

        content.addAll(pis);

        return this;
    }

    /**
     * <p>
     * Adds the specified PI to the document.
     * </p>
     *
     * @param pi the PI to add.
     * @return <code>Document</code> this document modified.
     */
    public Document addContent(ProcessingInstruction pi) {
        content.add(pi);

        return this;
    }

    /**
     * <p>
     * This will add a comment to the <code>Document</code>.
     * </p>
     *
     * @param comment <code>Comment</code> to add.
     * @return <code>Document</code> - this object modified.
     */
    public Document addContent(Comment comment) {
        content.add(comment);

        return this;
    }

    /**
     * <p>
     * This will add an element to the <code>Document</code>.
     * </p>
     *
     * @param element <code>Element</code> to add.
     * @return <code>Document</code> - this object modified.
     */
    public Document addContent(Element element) {
        if (getRootElement() != null) {
            throw new IllegalAddException(
                this, element, "The document already has a root element");
        }
        setRootElement(element);

        return this;
    }

    /**
     * <p>
     * This will return all content for the <code>Document</code>.
     * The returned list is "live" and changes to it affect the
     * document's actual content.
     * </p>
     *
     * @return <code>List</code> - all Document content
     */
    public List getMixedContent() {
        return content;
    }

    /**
     * <p>
     * This will set all content for the <code>Document</code>.
     * The List may contain only objects of type Element, Comment, and
     * ProcessingInstruction; and only one Element that becomes the root.
     * </p>
     *
     * @param content the new mixed content
     * @return the modified Document
     * @throws <code>IllegalAddException</code> if the List contains more than
     *         one Element or objects of illegal types
     */
    public Document setMixedContent(List content) {
        content.clear();
        rootElement = null;

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof Element) {
                addContent((Element)obj);
            }
            else if (obj instanceof Comment) {
                addContent((Comment)obj);
            }
            else if (obj instanceof ProcessingInstruction) {
                addContent((ProcessingInstruction)obj);
            }
            else {
                throw new IllegalAddException(
                    "A Document may contain only objects of type Element, " +
                    "Comment, and ProcessingInstruction");
            }
        }

        return this;
    }

    /**
     * <p>
     *  This returns a <code>String</code> representation of the
     *    <code>Document</code>, suitable for debugging. If the XML
     *    representation of the <code>Comment</code> is desired,
     *    <code>{@link #getSerializedForm}</code> should be used.
     * </p>
     *
     * @return <code>String</code> - information about the
     *         <code>Document</code>
     */
    public final String toString() {
        StringBuffer stringForm = new StringBuffer()
            .append("[Document: ");

        if (docType != null) {
            stringForm.append(docType.toString())
                      .append(" ");
        } else {
            stringForm.append(" No DOCTYPE declaration. ");
        }

        if (rootElement != null) {
            stringForm.append("Root - ")
                      .append(rootElement.toString());
        } else {
            stringForm.append(" No Root Element.");
        }

        stringForm.append("]");

        return stringForm.toString();
    }

    /**
     * <p>
     *  This will return the <code>Document</code> in XML format,
     *    usable in an XML document.
     * </p>
     *
     * @return <code>String</code> - the serialized form of the
     *         <code>Document</code>.
     */
    public final String getSerializedForm() {
        throw new RuntimeException(
          "Document.getSerializedForm() is not yet implemented");
    }

    /**
     * <p>
     *  This tests for equality of this <code>Document</code> to the supplied
     *    <code>Object</code>.
     * </p>
     *
     * @param ob <code>Object</code> to compare to.
     * @return <code>boolean</code> - whether the <code>Document</code> is
     *         equal to the supplied <code>Object</code>.
     */
    public final boolean equals(Object ob) {
        return (ob == this);
    }

    /**
     * <p>
     *  This returns the hash code for this <code>Document</code>.
     * </p>
     *
     * @return <code>int</code> - hash code.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * <p>
     *  This will return a deep clone of this <code>Document</code>.
     * </p>
     *
     * @return <code>Object</code> - clone of this <code>Document</code>.
     */
    public final Object clone() {
        Document doc = new Document(null);

        for (Iterator i = content.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof Element) {
                Element e = (Element)obj;
                doc.addContent((Element)e.clone());
            }
            else if (obj instanceof Comment) {
                Comment c = (Comment)obj;
                doc.addContent((Comment)c.clone());
            }
            else if (obj instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction)obj;
                doc.addContent((ProcessingInstruction)pi.clone());
            }
        }

        if (docType != null) {
            doc.docType = (DocType)docType.clone();
        }

        return doc;
    }

    /**
     * @deprecated use addContent(Comment) instead
     */
    public Document addComment(Comment c) {
        return addContent(c);
    }

    /**
     * @deprecated use addContent(ProcessingInstruction) instead
     */
    public Document addProcessingInstruction(ProcessingInstruction pi) {
        return addContent(pi);
    }

    /**
     * @deprecated use addContent(ProcessingInstruction) instead
     */
    public Document addProcessingInstruction(String target, String data) {
        return addContent(new ProcessingInstruction(target, data));
    }

    /**
     * @deprecated use addContent(ProcessingInstruction) instead
     */
    public Document addProcessingInstruction(String target, Map data) {
        return addContent(new ProcessingInstruction(target, data));
    }

    /**
     * @deprecated use doc.getMixedContent().remove(PI) instead
     */
    public boolean removeProcessingInstruction(ProcessingInstruction pi) {
        return getMixedContent().remove(pi);
    }
}



