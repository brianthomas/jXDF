
// XDF ValueList Class
// CVS $Id$

// ValueList.java Copyright (C) 2003 Brian Thomas,
// XML Group/GSFC-NASA, Code 630.1, Greenbelt MD, 20771


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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**  ValueList is the abstract class representing lists of values in XDF. 
 */

public abstract class ValueList extends BaseObject 
{

   //
   // Fields
   //
   private static final String ID_XML_ATTRIBUTE_NAME = new String("valueListId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("valueListIdRef");

   private List values;

   // 
   // Constuctors
   //

   /** The no argument constructor.
    */
   public ValueList ()
   {
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public ValueList ( Hashtable InitXDFAttributeTable )
   {

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);
 
   }

   // 
   // accessor methods
   //
 
   /** set the *valueListId* attribute
    */
   public void setValueListId (String strValueListId)
   {
      ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strValueListId);
   }
   
   /** get the *valueListId* attribute
    */
   public String getValueListId () 
   {
       return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *valueListIdRef* attribute
    */
   public void setValueListIdRef (String strValueListIdRef)
   {
      ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strValueListIdRef);
   
   }

   /** get the *valueListIdRef* attribute
    */
   public String getValueListIdRef () 
   {
       return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** get the list of values held by this valueList
    */
   public List getValues () {
      return values;
   }

   //
   // Other Methods
   //

   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }

   // 
   // Protected methods
   //

   /* Insert a Value object into the list of values held by this object.
       @param value - Value to be added
       @return a true if successfull
    */
/*
   protected boolean addValue (Value value )
   {
      getValues().add(value);
      return true;
   }
*/

  /** Set a list of Value objects as the list of values held by this object.
       @param list - List of Value objects to be set
       @return true if successful, false if not
    */
   protected boolean setValues ( List values )
   {

      resetValues();

      Iterator iter = values.iterator();
      while (iter.hasNext()) {
        Value nextValue = (Value) iter.next();
        getValues().add(nextValue);
      }

      return true;
   }  

   /** removes all values from the list of values held by this value list.
    */
   protected void resetValues() {
       values = (List) new Vector ();
   }

   protected void init () 
   {
      super.init();

      classXDFNodeName = ""; // abstract 

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);

      attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
      attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

      resetValues();
   }

}

