

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

import org.xml.sax.AttributeList;

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

  /**set the *lessThanValue* attribute
   */
/*
  public void setLessThanValue(Object strLessThanValue) {
     ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strLessThanValue);
  }

  /**set the *lessThanValueOrEqualValue* attribute
   */
/*
  public void setLessThanOrEqualValue(Object strLessThanOrEqualValue) {
     ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strLessThanOrEqualValue);
  }

  /**set the *greaterThanValue* attribute
   */
/*
  public void setGreaterThanValue(Object strGreaterThanValue) {
    ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strGreaterThanValue);
  }

  /**set the *greaterThanOrEqualValue* attribute
   */
/*
  public void setGreaterThanOrEqualValue(Object strGreaterThanOrEqualValue) {
    ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strGreaterThanOrEqualValue);
  }

  /** set the *infiniteValue* attribute
   */
/*
  public void setInfiniteValue(Object strInfiniteValue) {
    ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strInfiniteValue);
  }

  /**set the *infiniteNegativeValue* attribute
   */
/*
  public void setInfiniteNegativeValue(Object strInfiniteNegativeValue) {
    ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strInfiniteNegativeValue);
  }

  /**set the *noDataValue* attribute
   */
/*
  public void setNoDataValue(Object strNoDataValue) {
     ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strNoDataValue);
  }

  /**setlength: set the *length* attribute
   */
  public void setLength(Integer numLength) {
     if (numLength != null) 
        ((XMLAttribute) attribHash.get(LENGTH_XML_ATTRIBUTE_NAME)).setAttribValue(numLength);
     else 
        Log.warnln("StringDataFormat.setLength() cant accept null value. Ignoring request.");
  }

  /**getLength
   * @return the current *length* attribute
   */
  public Integer getLength()
  {
    return (Integer) ((XMLAttribute) attribHash.get(LENGTH_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   //
   // Other PUBLIC Methods
   //

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
      generateFormatPattern();
   }

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

     specificDataFormatName = "string";

     attribOrder.add(0, LENGTH_XML_ATTRIBUTE_NAME);  //add length as the first attribute;

     attribHash.put(LENGTH_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_LENGTH), Constants.INTEGER_TYPE));

     generateFormatPattern();

  }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.12  2001/04/27 21:30:59  thomas
 * Removed get/set methods for lessThan, greaterthan attribs.
 * These are now in Field and Array classes.
 *
 * Revision 1.11  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.10  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
 * Revision 1.9  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.8  2000/11/16 20:09:02  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.7  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.6  2000/10/27 21:20:53  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.5  2000/10/26 20:18:33  kelly
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.4  2000/10/26 16:31:45  thomas
 * removed silly errorln in toXDFOutput method. -b.t.
 *
 * Revision 1.3  2000/10/26 15:55:25  thomas
 * Fixed up the toXDFOutputStream method to print
 * out node compiant wi/ DTD. -b.t.
 *
 * Revision 1.2  2000/10/16 14:49:11  kelly
 * pretty much completed the class.  --k.z.
 *
 * Revision 1.1  2000/10/11 19:09:17  kelly
 * created the class
 *
 */
