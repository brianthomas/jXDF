// XDF ExponentialDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.Hashtable;

// ExponentialDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 * ExponentialDataFormat.java:describes exponential (ASCII) floating point numbers
 * (e.g. scientific notation, IE10).
 * @version $Revision$
 */


public class ExponentialDataFormat extends DataFormat {
  //
  //Fields
  //
  public static final String PerlSprintfFieldExponent = "e";
  public static final String PerlRegexFieldExponent ="[Ed][+-]?";
  public static final String PerlRegexFieldFixed = "\\d";
  public static final String PerlRegexFieldInteger = "\\d";

  public static final int ExponentSize = 2;  //double check


  /** The no argument constructor.
   */
  public ExponentialDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
     specificDataFormatName = "exponent";
    //add attributes
    /**precision:
     * The precision of this exponential field from the portion to the
     * right of the '.' to the exponent that follows the 'E'.
     */
    attribOrder.add(0,"precision");
    /**width: The entire width of this exponential field, including the 'E'
     * and its exponential number.
     */
    attribOrder.add(0, "width");

    attribHash.put("width", new XMLAttribute( null, Constants.NUMBER_TYPE));
    attribHash.put("precision", new XMLAttribute(null, Constants.NUMBER_TYPE));

  }

  //
  //Set Methods
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
    return (Number) ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(numInfiniteValue);
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

  /**setWidth: set the *width* attribute
   * @return: the current *width* attribute
   */
  public Object setWidth(Object numWidth) {
    return (Number) ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);

  }
  /**getWidth
   * @return: the current *width* attribute
   */
  public Number getWidth()
  {
    return (Number) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /**setPrecision: set the *precision* attribute
   * @return: the current *precision* attribute
   */
  public Number setPrecision(Number precision) {
    return (Number) ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);

  }
  /**getPrecision
   * @return: the current *precision* attribute
   */
  public Number getPrecision()
  {
    return (Number) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /**numOfBytes: A convenience method.
   * @Return: the number of bytes this XDF::ExponentialDataFormat holds.
   */
  public int numOfBytes() {
    return getWidth().intValue();
  }


  public String templateNotation(String strEndian, String strEncoding) {
    return "A"+numOfBytes();
  }

  public String regexNotation() {

    int width = numOfBytes();
    int precision = getPrecision().intValue();
    String notation = "(";
    int beforeWhiteSpace = width - precision - 1;
    if (beforeWhiteSpace > 0)
      notation += "\\s{0," + beforeWhiteSpace + "}";
    int leadingLength = width-precision;
    notation +=PerlRegexFieldFixed + "{1," + leadingLength + "}\\.";
    notation +=PerlRegexFieldFixed + "{1," + precision + "}";
    notation +=PerlRegexFieldExponent;

    notation +=PerlRegexFieldInteger + "{1," + ExponentSize + "}";

    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {

  return  "%" + numOfBytes() + "." + getPrecision().intValue() + PerlSprintfFieldExponent  ;

}

  /** fortranNotation: The fortran style notation for this object.
   */
  public String fortranNotation() {
    return "E"+ numOfBytes() + "." + getPrecision().intValue();
  }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.3  2000/10/27 21:15:47  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:34:13  kelly
 * fixed the class name, etc. -k.z.
 *
 * Revision 1.1  2000/10/26 20:23:10  kelly
 * was ExponentDataFormat.java before. renamed it.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:19:55  kelly
 *
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.1  2000/10/16 14:54:16  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
