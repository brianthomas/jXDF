

// XDF IntegerDataFormat Class
// CVS $Id$

// IntegerDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 * IntegerDataFormat.java:IntegerDataFormat class describes the data format
 * of objects which require such description (Field, Array).
 * @version $Revision$
 */


public class IntegerDataFormat extends DataFormat {

  //
  // Constructors
  //
  /** The no argument constructor.
   */
  public IntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  //
  //Get/Set Methods
  //

  /** set the *lessThanValue* attribute
   */
  public void setLessThanValue(Object numLessThanValue) {
     ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(numLessThanValue);
  }


  /** set the *lessThanValueOrEqualValue* attribute
   */
  public void setLessThanOrEqualValue(Object numLessThanOrEqualValue) {
     ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(numLessThanOrEqualValue);
  }


  /** set the *greaterThanValue* attribute
   */
  public void setGreaterThanValue(Object numGreaterThanValue) {
     ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(numGreaterThanValue);
  }

  /** set the *greaterThanOrEqualValue* attribute
   */
  public void setGreaterThanOrEqualValue(Object numGreaterThanOrEqualValue) {
     ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(numGreaterThanOrEqualValue);
  }


  /** set the *infiniteValue* attribute
   */
  public void setInfiniteValue(Object numInfiniteValue) {
     ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(numInfiniteValue);
  }

  /**set the *infiniteNegativeValue* attribute
   */
  public void setInfiniteNegativeValue(Object numInfiniteNegativeValue) {
     ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(numInfiniteNegativeValue);
  }

  /** set the *noDataValue* attribute
   */
  public void setNoDataValue(Object numNoDataValue) {
     ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(numNoDataValue);
  }


  /** set the *type* attribute
   */
  public void setType(String strType) {
    if (!Utility.isValidIntegerType(strType)) {
      Log.error("invalid type for IntegerDataFormat");
      Log.error("returning null");
      return;
    }

     ((XMLAttribute) attribHash.get("type")).setAttribValue(strType);

  }

  /**
   * @return the current *type* attribute
   */
  public String getType()
  {
    return (String) ((XMLAttribute) attribHash.get("type")).getAttribValue();
  }

  /** set the *width* attribute
   */
  public void setWidth(Integer width) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(width);
     generateFormatPattern();
  }

  /**
   * @return the current *width* attribute
   */
  public Integer getWidth()
  {
    return (Integer) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

   // We need this here so that we will properly update the
   // templateNotation of the class. -b.t. 
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
      generateFormatPattern();
   }

  /** A convenience method.
   * @return the number of bytes this IntegerDataFormat holds.
   */
  public int numOfBytes() {
    return getWidth().intValue();
  }

  /**
   *  Returns the class value for the hexadecimal type.
   *
  */

  public static String  typeHexadecimal(){
    return Constants.INTEGER_TYPE_HEX;
  }

  /**
   * @Returns the class value for the octal type.
    */
  public static String typeOctal() {
    return Constants.INTEGER_TYPE_OCTAL;
  }

  /**
   * @Returns the class value for the (default) decimal type.
   */
  public static String typeDecimal() {
   return Constants.INTEGER_TYPE_DECIMAL;
  }

  /** Template is the MessageFormat that should be used to print out data
      within the slice of the array covered by this object. This method is
      used by the dataCube in its toXMLOutput method. 
   */
  // separate method to minimize the number of times we do this.
  private void generateFormatPattern ( ) {

     StringBuffer leftpattern = new StringBuffer();
     StringBuffer etemplate = new StringBuffer();

     int wsize = getWidth().intValue() - 1;

     if (wsize > 1)
        etemplate.append("#");

     while (wsize-- > 2)
        leftpattern.append("#");
     leftpattern.append("0");

     // finish building the template
     etemplate.append(leftpattern.toString());
     etemplate.append(";-"+leftpattern.toString());

     formatPattern = etemplate.toString();
  }

  // 
  // Private Methods
  //
  /** Special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {

    specificDataFormatName = "integer";

    //add attributes
    attribOrder.add(0,"width");
    attribOrder.add(0, "type");


    attribHash.put("type", new XMLAttribute(new String(Constants.INTEGER_TYPE_DECIMAL), Constants.STRING_TYPE));
    attribHash.put("width", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.8  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
 * Revision 1.7  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.6  2000/11/16 20:01:07  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:55:07  thomas
 * Trimmed import path to just needed classes (none!) -b.t.
 *
 * Revision 1.3  2000/10/27 21:20:06  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:18:52  kelly
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.1  2000/10/16 14:53:26  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
