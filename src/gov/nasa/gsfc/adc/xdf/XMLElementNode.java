

// XDF XMLElementNode 
// CVS $Id$

// XMLElementNode.java Copyright (C) 2000 Brian Thomas,
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
import org.apache.crimson.tree.ElementNode;
import org.apache.crimson.tree.TextNode;
import org.apache.crimson.tree.CDataNode;
*/
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMCDATA;
import org.dom4j.dom.DOMText;

import org.xml.sax.Attributes;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.dom4j.Namespace;
import org.dom4j.QName;

/** 
     This class is used to hold XML element node information *inside* XDF (and child) objects.
     It rolls together the functionality of DOM Element and Text nodes. It will hold both
     the node name, node attributes AND PCData as well as child Elements.
 */

public class XMLElementNode extends DOMElement implements Cloneable {

   // 
   // Fields
   //

   //
   // Constructors
   //

   public XMLElementNode(String name) {
      super(name);
   }

   public XMLElementNode(QName qname) {
      super(qname);
   }

   public XMLElementNode(QName qname, int attributeCount) {
      super(qname, attributeCount);
   }

   public XMLElementNode(String name, Namespace namespace) {
      super(name, namespace);
   }


   //
   // Get/Set Methods 
   //

   /** Set the value of the PCDATA held by this XMLElementNode.
    */
   public void setPCData (String text) {
      removeAllTextChildNodes();
      DOMText newTextNode = new DOMText(text); // CDataNode is a special Text node 
      appendChild(newTextNode);
  }

   /** Set the value of the PCDATA held by this XMLElementNode.
       By using CDataNode here, the user may insure special characters like the
       lessthan sign are preserved without resorting to entities.
   */
  public void setPCData (DOMCDATA textNode) {
      removeAllTextChildNodes();
      appendChild(textNode);
  }

  private void removeAllTextChildNodes() {

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof DOMText) {
             removeChild(thisNode);
          }
      }

   }

   /** Get any PCDATA held within this XMLElementNode.
       This amounts to a convience method which appends all of 
       the child Textnodes into a String object.
   */
   public String getPCData() {

      StringBuffer myCDATA = new StringBuffer();

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof DOMText) {
             myCDATA.append(thisNode.toString());
          }
      }

      return myCDATA.toString();
   }

   /** A convience method to get all XMLElementNode children.
       Vanilla ElementNode children will NOT be returned.
    */
   public List getXMLElementNodeList () { 

      ArrayList xmlChildList = new ArrayList ();

      NodeList childNodes = this.getChildNodes();
      int size = childNodes.getLength();
      for (int i = 0; i < size; i++) {
          Node thisNode = childNodes.item(i);
          if (thisNode instanceof XMLElementNode) {
              xmlChildList.add(thisNode);
          }
      }

      return xmlChildList;
   }

   //
   // Other Public Methods
   //

   /** appends more PCDATA into this XMLElementNode. 
    */
   public void appendPCData (String text) {
      DOMText newTextNode = new DOMText(text);
      appendChild(newTextNode);
   }

   /** appends more PCDATA into this XMLElementNode. 
    */
   public void appendPCData (DOMCDATA textNode) {
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

   public boolean addXMLElementNode (XMLElementNode obj) {

      appendChild(obj);
      return false;
   }

   public boolean removeXMLElementNode (XMLElementNode obj) {

      removeChild(obj);
      return false;
   }

   public void setXMLAttributes (Attributes attrs) { 

      resetXMLAttributes();

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

   /** just a convience method for setAttribute
    */
   public void setXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   public void setAttributes (List attributeList) {

   }

   /** just a convience method for getAttribute
    */
   public void addXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   /** Clears all (XML) attributes in this object.
    */
   public void resetXMLAttributes() {

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
      String nodeNameString = this.getName();
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (nodeNameString != null) 
      {

        if (isPrettyOutput)
          outputWriter.write(indent); // indent node if desired
        outputWriter.write("<" + nodeNameString);   // print opening statement

      }

      outputWriter.write(">"); //close node

/*
      // 2. Print out string object XML attributes 
      ArrayList attribs = (ArrayList) xmlInfo.get("attribList");
      // is synchronized here correct?
      NamedNodeMap attribs = this.getAttributes();
      synchronized(attribs) {

          int size = attribs.getLength();
          for (int i = 0; i < size; i++) {
             Attr attrib = (Attr) attribs.item(i);
             writeOut(outputstream, " "+attrib.getName()+"=\""+attrib.getValue()+"\"");
          }

      }

      //3. print child nodes OR CDATA 
      int nrofChildXMLElements = getXMLElementNodeList().size();
      String pcdata = getCData();
      if (pcdata != null || nrofChildXMLElements > 0 )
      {

          // close opening node
          writeOut(outputstream, ">");

          if (pcdata != null) {
             writeOut(outputstream, pcdata);
          }
  

          if (pcdata != null || nrofChildXMLElements > 0 )
          {
             List childXMLElements = getXMLElementNodeList();
          }

          // print closing element
          writeOut(outputstream, "</"+nodeNameString+">");

      } else {

          // close opening node
          writeOut(outputstream, "/>");

      }
*/
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
/*
   public Object clone() throws CloneNotSupportedException {
     return super.clone(); 
   }
*/

   //
   // Protected Methods
   //

   //
   // Private Methods
   //

/*
   private void writeOut ( OutputStream outputstream, String msg )
   throws java.io.IOException
   {
       outputstream.write(msg.getBytes());
   }
*/

}

/* Modification History:
 *
 * $Log$
 * Revision 1.2  2001/08/01 18:08:12  thomas
 * new version using dom4j
 *
 * Revision 1.1  2001/07/26 15:58:36  thomas
 * From the old 'XMLElement' class, just changed name.
 *
 *
 */
