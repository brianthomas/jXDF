
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

  /**getType
   * @return: the current *type* attribute
   */
  public String getType()
  {
    return (String) ((XMLAttribute) attribHash.get("type")).getAttribValue();
  }

  /** set the *width* attribute
   */
  public void setWidth(Number width) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(width);
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
