

// XDF ElementNode 
// CVS $Id$

// ElementNode.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

package gov.nasa.gsfc.adc.xdf;

import java.util.List;
import java.util.ArrayList;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;

/*
// crimson DOM
import org.apache.crimson.tree.ElementNode;
import org.apache.crimson.tree.TextNode;
import org.apache.crimson.tree.CDataNode;
*/
/*
// dom4j DOM 
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMCDATA;
import org.dom4j.dom.DOMText;
import org.dom4j.Namespace;
import org.dom4j.QName;
*/

// Xerces DOM 
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.dom.CDATASectionImpl;

import org.xml.sax.Attributes;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/** 
     This class is used to hold XML element node information *inside* XDF (and child) objects.
     It rolls together the functionality of DOM Element and Text nodes. It will hold both
     the node name, node attributes AND PCData as well as child Elements.
 */

public class ElementNode extends ElementNSImpl implements Cloneable {

   // 
   // Fields
   //

   //
   // Constructors
   //

   public ElementNode (CoreDocumentImpl ownerDocument,
                          String namespaceURI,
                          String qualifiedName)
   throws DOMException
   {
      super(ownerDocument, namespaceURI, qualifiedName);
   }

   public ElementNode (String name) 
   throws DOMException
   {
      super (Constants.getInternalDOMDocument(), name);
   }

   public ElementNode (CoreDocumentImpl ownerDocument, String name) 
   {
      super(ownerDocument, name);
   }

/*
  // next 4 constructors are for DOM4J
   public ElementNode(String name) {
      super(name);
   }

   public ElementNode(QName qname) {
      super(qname);
   }

   public ElementNode(QName qname, int attributeCount) {
      super(qname, attributeCount);
   }

   public ElementNode(String name, Namespace namespace) {
      super(name, namespace);
   }
*/


   //
   // Get/Set Methods 
   //

   /** Set the value of the PCDATA held by this ElementNode.
    */
   public void setPCData (String text) {
      removeAllTextChildNodes();
      CoreDocumentImpl owner = (CoreDocumentImpl) this.getOwnerDocument();
      TextImpl newTextNode = new TextImpl(owner, text); // CDataNode is a special Text node 
      appendChild(newTextNode);
   }

   /** Set the value of the PCDATA held by this ElementNode.
       By using CDataNode here, the user may insure special characters like the
       lessthan sign are preserved without resorting to entities.
   */
  public void setPCData (CDATASectionImpl textNode) {
      removeAllTextChildNodes();
      appendChild(textNode);
  }

  private void removeAllTextChildNodes() {

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof TextImpl) {
             removeChild(thisNode);
          }
      }

   }

   /** Get any PCDATA held within this ElementNode.
       This amounts to a convenience method which appends all of 
       the child Textnodes into a String object.
   */
   public String getPCData() {

      StringBuffer myCDATA = new StringBuffer();
      boolean gotSomeTextData = false;

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof TextImpl) {
             myCDATA.append(thisNode.getNodeValue());
             gotSomeTextData = true;
          }
      }

      return gotSomeTextData ? myCDATA.toString() : null;
   }

   /** A convenience method to get all ElementNode children.
       Vanilla ElementNode children will NOT be returned.
    */
   public List getElementNodeList () { 

      ArrayList xmlChildList = new ArrayList ();

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof ElementNode) {
              xmlChildList.add(thisNode);
          }
      }

      return xmlChildList;
   }

   //
   // Other Public Methods
   //

   /** appends more PCDATA into this ElementNode. 
    */
   public void appendPCData (String text) {
      CoreDocumentImpl owner = (CoreDocumentImpl) this.getOwnerDocument();
      TextImpl newTextNode = new TextImpl(owner, text);
      appendChild(newTextNode);
   }

   /** appends more PCDATA into this ElementNode. 
    */
   public void appendPCData (CDATASectionImpl textNode) {
      appendChild(textNode);
   }

/*
   // ugh. Wont allow me to override the 'Element' method that is 
   // already declared. so be it.
   public boolean addAttribute (String name, String value) {
      this.setAttribute(name, value);
      return true;
   }

   // ugh. Wont allow me to override the 'void' method that is 
   // already declared. so be it.
   public boolean removeAttribute ( String name ) 
   {

      this.removeAttribute(name);
      NamedNodeMap attribs = this.getAttributes();
      if (attribs.getNamedItem(name) != null) {
         attribs.removeNamedItem(name);
         return true;
      }
      return true;
   }
*/

   public boolean addElementNode (ElementNode obj) {

      appendChild(obj);
      return false;
   }

   public boolean removeElementNode (ElementNode obj) {

      removeChild(obj);
      return false;
   }

   public void setAttributes (Attributes attrs) { 

      resetAttributes();

      // must we do it this way? argh.
      if (attrs != null) {
          // whip thru the list setting each
          int size = attrs.getLength();
          for (int i = 0; i < size; i++) {
             String attribName = attrs.getQName(i);
             String attribValue = attrs.getValue(i);
             this.setAttribute(attribName, attribValue);
          }
      }

   }

   /** just a convenience method for setAttribute
    */
   public void setAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   public void setAttributes (List attributeList) {

   }

   /** just a convenience method for getAttribute
    */
   public void addAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   /** Clears all (XML) attributes in this object.
    */
   public void resetAttributes() {

       if (this.hasAttributes()) {

          NamedNodeMap attribs = this.getAttributes();

          int size = attribs.getLength();
          for (int i = 0; i < size; i++) {
             Attr attrib = (Attr) attribs.item(i);
             attribs.removeNamedItem(attrib.getName());
          }
       }

   }

   /**
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
   throws java.io.IOException
   {

      Writer outputWriter = new BufferedWriter(new OutputStreamWriter(outputstream));
      toXMLWriter (outputWriter, indent, dontCloseNode, newNodeNameString, noChildObjectNodeName);

      // this *shouldnt* be needed, but tests with both Java 1.2.2 and 1.3.0
      // on SUN and Linux platforms show that it is. Hopefully we can remove
      // this in the future.
      outputWriter.flush();

   }
 
   public void toXMLWriter ( 
                                Writer outputWriter
                           )
   throws java.io.IOException
   {
       toXMLWriter(outputWriter, "", false, null, null);
   }

   public void toXMLWriter ( 
                               Writer outputWriter,
                               String indent
                           )
   throws java.io.IOException
   {
       toXMLWriter(outputWriter, indent, false, null, null);
   }


   public void toXMLWriter ( 
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
   throws java.io.IOException
   {

      boolean isPrettyOutput = Specification.getInstance().isPrettyXDFOutput();

      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the class getName method.
      String nodeNameString = this.getTagName();
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (nodeNameString != null) 
      {

        if (isPrettyOutput)
          outputWriter.write(indent); // indent node if desired
        outputWriter.write("<" + nodeNameString);   // print opening statement

      }

      // 2. Print out string object XML attributes 
      NamedNodeMap attribs = this.getAttributes();
      // is synchronized here correct?
      synchronized(attribs) {

          int size = attribs.getLength();
          for (int i = 0; i < size; i++) {
             Attr attrib = (Attr) attribs.item(i);
             outputWriter.write(" "+attrib.getName()+"=\""+attrib.getValue()+"\"");
          }

      }

      //3. print child nodes OR CDATA 
      int nrofChildXMLElements = getElementNodeList().size();
      String pcdata = getPCData();
      if (pcdata != null || nrofChildXMLElements > 0 )
      {

          // close opening node
          outputWriter.write(">");

          if (pcdata != null) {
             outputWriter.write(pcdata);
          }
  

          if (nrofChildXMLElements > 0 )
          {
             String moreIndent = Specification.getInstance().getPrettyXDFOutputIndentation();
             if (isPrettyOutput && pcdata == null) 
                outputWriter.write(Constants.NEW_LINE);
             List childXMLElements = getElementNodeList();
             for (int i = 0; i < nrofChildXMLElements ; i++) {
                if (isPrettyOutput && pcdata == null) 
                  outputWriter.write(indent); // indent node if desired
                ElementNode node = (ElementNode) childXMLElements.get(i);
                node.toXMLWriter (outputWriter, indent+moreIndent);
             }
          }

          // print closing element
          outputWriter.write("</"+nodeNameString+">");

      } else {

          // close opening node
          outputWriter.write("/>");

      }

      if (isPrettyOutput) 
          outputWriter.write(Constants.NEW_LINE);

   }

   /**
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   String indent
                                )
   throws java.io.IOException
   {
      toXMLOutputStream(outputstream, indent, false, null, null);
   }

   /**
    */
   public void toXMLOutputStream ( OutputStream outputstream)
   throws java.io.IOException
   {
      toXMLOutputStream(outputstream, "", false, null, null);
   }

   /**
    */
   public Object clone() throws CloneNotSupportedException {
     return super.clone(); 
   }

   //
   // Protected Methods
   //

   //
   // Private Methods
   //

}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/09/13 21:36:59  thomas
 * *** empty log message ***
 *
 * Revision 1.3  2001/08/23 18:01:52  thomas
 * Switched to Xerces implementation
 *
 * Revision 1.2  2001/08/01 18:08:12  thomas
 * new version using dom4j
 *
 * Revision 1.1  2001/07/26 15:58:36  thomas
 * From the old 'XMLElement' class, just changed name.
 *
 *
 */
