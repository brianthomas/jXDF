// XDF IntegerDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

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



/**
 * IntegerDataFormat.java:IntegerDataFormat class describes the data format
 * of objects which require such description (XDF::Field, XDF::Array).
 * @version $Revision$
 */


public class IntegerDataFormat extends DataFormat {
  //
  //Fields
  //


  //This is used by the 'decimal' type
  public static final String PerlSprintfFieldInteger = "d";

  // using long octal format. Technically, should be an error
  // to have Octal on Exponent and Fixed formats but we will
  // return the value as regular number
  public static final String OctalPerlSprintfFieldInteger = "lo";

  // using long hex format. Should be an error
  public static final String HexPerlSprintfFieldInteger = "lx";
  public static final String PerlRegexFieldInteger = "\\d";


  /** The no argument constructor.
   */
  public IntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
     specificDataFormatName = "integer";
    //add attributes
    attribOrder.add(0,"width");
    attribOrder.add(0, "type");


    attribHash.put("type", new XMLAttribute(new String(Constants.INTEGER_TYPE_DECIMAL), Constants.STRING_TYPE));
    attribHash.put("width", new XMLAttribute(new Integer(0), Constants.NUMBER_TYPE));

  }

  //
  //Get/Set Methods
  //

  /**setLessThanValue: set the *lessThanValue* attribute
   * @return: the current *lessThanValue* attribute
   */
  public Object setLessThanValue(Object numLessThanValue) {
    return (Number) ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(numLessThanValue);
  }


  /**setLessThanValueOrEqualValue: set the *lessThanValueOrEqualValue* attribute
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public Object setLessThanOrEqualValue(Object numLessThanOrEqualValue) {
    return (Number) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(numLessThanOrEqualValue);
  }


  /**setgreaterThanValue: set the *greaterThanValue* attribute
   * @return: the current *greaterThanValue* attribute
   */
  public Object setGreaterThanValue(Object numGreaterThanValue) {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(numGreaterThanValue);
  }

  /**setGreaterThanOrEqualValue: set the *greaterThanOrEqualValue* attribute
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public Object setGreaterThanOrEqualValue(Object numGreaterThanOrEqualValue) {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(numGreaterThanOrEqualValue);
  }


  /**setInfiniteValue: set the *infiniteValue* attribute
   * @return: the current *infiniteValue* attribute
   */
  public Object setInfiniteValue(Object numInfiniteValue) {
    return (Object) ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(numInfiniteValue);
  }

  /**setInfiniteNegativeValue: set the *infiniteNegativeValue* attribute
   * @return: the current *infiniteNegativeValue* attribute
   */
  public Object setInfiniteNegativeValue(Object numInfiniteNegativeValue) {
    return (Number) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(numInfiniteNegativeValue);
  }

  /**setNoDataValue: set the *noDataValue* attribute
   * @return: the current *noDataValue* attribute
   */
  public Object setNoDataValue(Object numNoDataValue) {
    return (Number) ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(numNoDataValue);
  }


  /**setType: set the *type* attribute
   * @return: the current *type* attribute if success, null if not
   */
  public String setType(String strType) {
    if (!Utility.isValidIntegerType(strType)) {
      Log.error("invalid type for IntegerDataFormat");
      Log.error("returning null");
      return null;
    }

    return (String) ((XMLAttribute) attribHash.get("type")).setAttribValue(strType);

  }
  /**getType
   * @return: the current *type* attribute
   */
  public String getType()
  {
    return (String) ((XMLAttribute) attribHash.get("type")).getAttribValue();
  }

  /**setWidth: set the *width* attribute
   * @return: the current *width* attribute
   */
  public Number setWidth(Number width) {
    return (Number) ((XMLAttribute) attribHash.get("width")).setAttribValue(width);

  }
  /**getWidth
   * @return: the current *width* attribute
   */
  public Number getWidth()
  {
    return (Number) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }
  //
  //Other PUBLIC Methods
  //

  /**numOfBytes: A convenience method.
   * @Return: the number of bytes this XDF::IntegerDataFormat holds.
   */
  public int numOfBytes() {
    return getWidth().intValue();
  }

  /** typeHexadecimal
   *  Returns the class value for the hexadecimal type.
   *
  */

  public static String  typeHexadecimal(){
    return Constants.INTEGER_TYPE_HEX;
  }

  /**typeOctal:
   * @Returns the class value for the octal type.
    */
  public static String typeOctal() {
    return Constants.INTEGER_TYPE_OCTAL;
  }

  /** typeDecimal
   * @Returns the class value for the (default) decimal type.
   */
  public static String typeDecimal() {
   return Constants.INTEGER_TYPE_DECIMAL;
  }

  //pass in param??? double check???
  public String templateNotation(String strEndian, String strEncoding) {
     return "A"+getWidth();
  }

  public String regexNotation() {
    int width = numOfBytes();
    String symbol;
    if (getType().equals(Constants.INTEGER_TYPE_DECIMAL))
      symbol = "\\.";
    else
      symbol = PerlRegexFieldInteger;

    String notation = "(";
    int beforeWhiteSpace = width - 1;
    if (beforeWhiteSpace > 0)
      notation += "\\s{0," + beforeWhiteSpace + "}";
    notation +=symbol + "{1," + width + "}";
    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {
    String fieldSymbol=null;
    String type = getType();
    if (type.equals(Constants.INTEGER_TYPE_DECIMAL))
      fieldSymbol = PerlSprintfFieldInteger;
    if (type.equals(Constants.INTEGER_TYPE_OCTAL))
      fieldSymbol = OctalPerlSprintfFieldInteger;
    if (type.equals(Constants.INTEGER_TYPE_HEX))
      fieldSymbol = HexPerlSprintfFieldInteger;
    return "%" + getWidth()+ fieldSymbol;
}

  /** fortranNotation: The fortran style notation for this object.
   */
  public String fortranNotation() {
    return "I"+ getWidth();
  }


}
/* Modification History:
 *
 * $Log$
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
