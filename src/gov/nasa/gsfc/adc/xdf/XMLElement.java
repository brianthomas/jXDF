

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

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.io.OutputStream;

//import org.apache.crimson.tree.ElementNode;
import org.xml.sax.AttributeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 */

public class XMLElement extends BaseObjectWithXMLElements {

   // 
   // Fields
   //
   private Hashtable attribHash = new Hashtable();
   private String myTagName;
   private StringBuffer myCDATA;

   private List childNodeList = (List) new Vector ();

   //
   // Constructors
   //

   //public XMLElement (String tagName, Document ownerDoc) { }

   public XMLElement (String tagName) {
      init();
      setTagName(tagName);
   }

   //
   // Get/Set Methods 
   //

   public void setCData(String text) {
      myCDATA.delete(0,myCDATA.length());
      myCDATA.append(text);
   }

   /** get the *value* (PCDATA) attribute.
   */
   public String getCData() {
      return myCDATA.toString();
   }

   /**
    */
   public String getTagName( ) {
      return myTagName;
   }

   //
   // Other Public Methods
   //

   /**
    */
   public void appendCData (String text) {
      myCDATA.append(text);
   }

   public boolean addAttribute (String name, String value) {
      attribHash.put(name, value);
      return true;
   }

   public boolean removeAttribute ( String name ) {
      return attribHash.remove(name) == null ? false : true;
   }

   public void setXMLAttributes (AttributeList attribs) { 

   }

   /**
    */
   public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
   {


      String nodeNameString = this.getTagName();
      if (newNodeNameString != null) nodeNameString = newNodeNameString;

      super.toXMLOutputStream ( outputstream, new Hashtable(), indent,
                                dontCloseNode, nodeNameString, noChildObjectNodeName);
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
       classXDFNodeName = "";

   }


   //
   // Private Methods
   //

   private void setTagName (String name) {
      myTagName = name;
   }


}

/* Modification History:
 *
 * $Log$
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
