
// XDF ValueListDelimitedList Class
// CVS $Id$

// ValueListDelimitedList.java Copyright (C) 2001 Brian Thomas,
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

import java.io.Writer;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/** Create a list of values from the passed List of Value objects.
  * The ValueList object may be then passed on and used by other objects
  * to populate the list of values they hold.
  * A desirable feature of using the ValueList object is that it result in
  * a more compact format for describing the values so added to other objects
  * when they are written out using the toXMLOutputStream method.
  */

   // internal valueList class for hold algorithm style 
public class ValueListDelimitedList extends ValueList {

   //
   // Fields
   //
   private static final String DELIMITER_XML_ATTRIBUTE_NAME = new String("delimiter");
   private static final String REPEATABLE_XML_ATTRIBUTE_NAME = new String("repeatable");
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private boolean isRepeatable = false;

   //
   // Constructors
   //

   /** no arg constructor 
    */
   public ValueListDelimitedList () {
       init();
   }

   /** Constructs a valueList object with Values in passed List.
       Care should be taken that none of the Value objects are set
       to the same sequence of characters as the default delimiter.
    */
   public ValueListDelimitedList (List values) {
      init();
      setValues(values);
   }

   /** Constructs a valueList object with Values in passed List.
       Care should be taken that none of the Value objects are set
       to the same sequence of characters as the passed delimiter.
    */
   public ValueListDelimitedList (List values, String delimiter) {

      if (delimiter == null) {
         Log.errorln("ERROR: in ValueListDelimitedList() delimiter string can't be null.");
         System.exit(-1);
      }

      init();

      setDelimiter(delimiter);
      setValues(values);

   }

   // 
   // Public Methods
   //

   // accessor methods

   /** Sets the delimiter for this valuelist.
   */
   public void setDelimiter (String delimiter)
   {
      ((Attribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).setAttribValue(delimiter);
   }


   /**
    * @return the current *delimiter* attribute
    */
   public String getDelimiter()
   {
      return (String) ((Attribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

  /** Sets the repeatable for this valuelist.
   */
   public void setRepeatable (String repeatable)
   {
      if(repeatable == null || !repeatable.equals("yes"))
         isRepeatable = false;
      else
         isRepeatable = true;

      ((Attribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).setAttribValue(repeatable);
   }


   /**
    * @return the current *repeatable* attribute
    */
   public String getRepeatable()
   {
        return (String) ((Attribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** special utility method. Allows to quickly set the values of the valueList 
     * from a passed string. Parsing of the string is done using the parameters
     * of the valuelist.
     */
   public void setValues (String strListOfValues) {

       List valuesToAdd = splitStringIntoValueObjects ( strListOfValues) ;
       setPCDATA(strListOfValues);
       setValues(valuesToAdd);

   }

   //
   // Protected Methods
   //

   /** set the *value* attribute
    */
   protected void setPCDATA (String pcdata)
   {
      ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(pcdata);
   }

   protected void init()
   {

      super.init();

      classXDFNodeName = "valueList";

      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, REPEATABLE_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, DELIMITER_XML_ATTRIBUTE_NAME);

      attribHash.put(DELIMITER_XML_ATTRIBUTE_NAME, new Attribute(Constants.VALUELIST_DELIMITER, 
                                                                           Constants.STRING_TYPE));
      attribHash.put(REPEATABLE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
      attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));


   }

   //
   // Private Methods
   //

   private List splitStringIntoValueObjects ( String valueListString )
   {

        Vector valueList = new Vector();
        String regex = getDelimiter();
        if (isRepeatable) 
           regex = regex + "+";

        String[] strList = valueListString.split(regex);

        for(int i=0; i< strList.length; i++) {
           Value newvalue = new Value(strList[i]);
           valueList.add(newvalue);
        }

        return valueList;
    }

}


