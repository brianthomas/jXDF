

// XDF BaseObjectWithXMLElements Class
// CVS $Id$


// BaseObjectWithXMLElements.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
     The base object class for XDF objects which may hold internal XML elements.
 */
public abstract class BaseObjectWithXMLElements extends BaseObject implements Cloneable {

   //
   // Fields
   //

   /* XML attribute names */
   protected static final String XML_ELEMENTLIST_XML_ATTRIBUTE_NAME = new String("xmlElementList");

   public BaseObjectWithXMLElements() {
      // init(); // Well, this might be best, but we do it a different way right now. 
   }

   //
   // Get/Set Methods
   //

   /** get the list of XML Elements held within this object.
      @return list of XMLElement objects.
    */
   public List getXMLElementList() {
      return (List) ((XMLAttribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /**set the *location* attribute.
   */
   public void setXMLElementList(List elements)
   {
      ((XMLAttribute) attribHash.get(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME)).setAttribValue(elements);
   }

   //
   // Other Public Methods
   //

   /** append an XMLElement into the list of internal elements held by this object.
    */
   public void addXMLElement (XMLElement element) {
      getXMLElementList().add(element);
   }

   /** Indicate the datacell that this note applies to within an array.
    */
   public void removeXMLElement (XMLElement element) {
      getXMLElementList().remove(element);
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   //
   // Protected Methods
   //

   /** a special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {

      attribOrder.add(0, XML_ELEMENTLIST_XML_ATTRIBUTE_NAME);

      attribHash.put(XML_ELEMENTLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));


   }

}

