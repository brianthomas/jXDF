

// XDF StringDataFormat Class
// CVS $Id$

// StringDataFormat.java Copyright (C) 2000 Brian Thomas,
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

import org.xml.sax.Attributes;

/**
   This class describes string data.
   @version $Revision$
 */


public class StringDataFormat extends DataFormat {

   //
   // Fields
   //

   /* XML attributes */
   protected static final String LENGTH_XML_ATTRIBUTE_NAME = new String("length");

   /* default attribute values */
   public static final int DEFAULT_LENGTH = 0;

   //
   // Constructors
   //

  /** The no argument constructor.
   */
  public StringDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }


  //
  //Set Methods
  //

  /**setlength: set the *length* attribute
   */
  public void setLength(Integer numLength) {
     if (numLength != null) 
        ((Attribute) attribHash.get(LENGTH_XML_ATTRIBUTE_NAME)).setAttribValue(numLength);
     else 
        Log.warnln("StringDataFormat.setLength() cant accept null value. Ignoring request.");
  }

  /**getLength
   * @return the current *length* attribute
   */
  public Integer getLength()
  {
    return (Integer) ((Attribute) attribHash.get(LENGTH_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** set the *noDataValue* attribute
    */
   public void setNoDataValue (Object string) {

        if (string instanceof String)
           ((Attribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(string);
        else
           Log.warnln("Could'nt set the noDataValue as it is not a String object. Ignoring request.");

   }

   //
   // Other PUBLIC Methods
   //

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   public void setAttributes (Attributes attrs) {
      super.setAttributes(attrs);
      generateFormatPattern();
   }

   /**
       @deprecated use the setAttributes method instead
    */
   public void setXMLAttributes (Attributes attrs) { this.setAttributes(attrs); } 

   /** A convenience method.
      @return the number of bytes this StringDataFormat holds.
    */
   public int numOfBytes() {
     return getLength().intValue();
   }

   //
   // Private Methods 
   //

   // separate method to minimize the number of times we do this.
   private void generateFormatPattern ( ) {
      formatPattern = "{0}";
   }

   //
   // Protected Methods 
   //

   /** Special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
  protected void init() {

     super.init();

     specificDataFormatName = "string";

     attribOrder.add(0, LENGTH_XML_ATTRIBUTE_NAME);  //add length as the first attribute;

     attribHash.put(LENGTH_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_LENGTH), Constants.INTEGER_TYPE));

     generateFormatPattern();

  }

}

