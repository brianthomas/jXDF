

// XDF XMLElement
// CVS $Id$

// XMLElement.java Copyright (C) 2000 Brian Thomas,
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

// import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.crimson.tree.ElementNode;
import org.xml.sax.Attributes;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

//import org.w3c.dom.Document;
// import org.w3c.dom.Element;
//import org.apache.crimson.tree.DomEx;

/** 
 */

public class XMLElement extends ElementNode implements Cloneable {

   // 
   // Fields
   //
   private StringBuffer myCDATA;
//   private List childElementNodeList = Collections.synchronizedList(new ArrayList());


   //
   // Constructors
   //

   //public XMLElement (String tagName, Document ownerDoc) { }

   public XMLElement (String tagName) 
//   throws DomEx
   {
      super(tagName);
      init();
   }

   public XMLElement(String namespaceURI, String qName)
//   throws DomEx
   {
      super(namespaceURI,qName);
      init();
   }


   //
   // Get/Set Methods 
   //

   /** Set the value of the CDATA held by this XMLElement.
    */
   public void setCData(String text) {
      myCDATA.delete(0,myCDATA.length());
      myCDATA.append(text);
   }

   /** Get any CDATA held within this XMLElement.
   */
   public String getCData() {
      return myCDATA.toString();
   }

//   public void getXMLElementList () { return childElementNodeList(); }

   //
   // Other Public Methods
   //

   /** appends more CDATA into this XMLElement. 
    */
   public void appendCData (String text) {
      myCDATA.append(text);
   }

   public boolean addAttribute (String name, String value) {
      this.setAttribute(name, value);
      return true;
   }

/*
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

   public boolean addXMLElement (XMLElement obj) {

      return false;
   }

   public boolean removeXMLElement (XMLElement obj) {

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

   public void setXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   public void addXMLAttribute (String name, String value) { 
      this.setAttribute(name, value);
   }

   /** Clears all XML attributes in this XMLElement.
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


      boolean isPrettyOutput = Specification.getInstance().isPrettyXDFOutput();
      if (isPrettyOutput) 
          writeOut(outputstream, indent); // indent node if desired
      writeOut(outputstream, this.toString());
      if (isPrettyOutput) 
          writeOut(outputstream, Constants.NEW_LINE); 

/*
      String nodeNameString = this.getTagName();
      // Setup. Sometimes the name of the node we are opening is different from
      // that specified in the classXDFNodeName (*sigh*)
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      // 1. open this node, print its simple XML attributes
      if (nodeNameString != null) {

        if (Specification.getInstance().isPrettyXDFOutput())
          writeOut(outputstream, indent); // indent node if desired
        writeOut(outputstream,"<" + nodeNameString);   // print opening statement

      }

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
      int nrofChildXMLElements = getXMLElementList().size();
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
             List childXMLElements = getXMLElementList();
          }

          // print closing element
          writeOut(outputstream, "</"+nodeNameString+">");

      } else {

          // close opening node
          writeOut(outputstream, "/>");

      }
*/

   }

   /**
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   String indent
                                )
   throws java.io.IOException
   {
      this.toXMLOutputStream(outputstream, indent, false, null, null);
   }

   /**
    */
   public void toXMLOutputStream ( OutputStream outputstream)
   throws java.io.IOException
   {
      this.toXMLOutputStream(outputstream, "", false, null, null);
   }

   /**
    */
   // NOT finished.
   public Object clone() throws CloneNotSupportedException {
     return super.clone(); // not quite right, gets local fields only 
   }

   //
   // Protected Methods
   //

   /** A special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init( )
   {

       resetXMLAttributes();

   }


   //
   // Private Methods
   //

   private void writeOut ( OutputStream outputstream, String msg )
   throws java.io.IOException
   {
       outputstream.write(msg.getBytes());
   }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.6  2001/07/19 22:00:38  thomas
 * Got the class working. Interface not quite right, but good
 * enuff for the momement.
 *
 * Revision 1.5  2001/07/17 19:06:23  thomas
 * upgrade to use JAXP (SAX2) only. Namespaces NOT
 * implemented (yet).
 *
 * Revision 1.4  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.3  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.2  2001/05/10 21:46:20  thomas
 * more code, but this class still unfinished.
 * It looks like it will be a pain to get this
 * to conform to XML::DOM::Element.
 *
 * Revision 1.1  2001/05/04 20:05:53  thomas
 * Initial version
 *
 *
 */
