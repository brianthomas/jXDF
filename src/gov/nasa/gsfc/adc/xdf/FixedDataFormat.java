// XDF FixedDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// FixedDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 * FixedDataFormat.java:describes (ASCII) fixed (floating point) numbers.
 * @version $Revision$
 */


public class FixedDataFormat extends DataFormat {
  //
  //Fields
  //
  public static final String PerlSprintfFieldFixed = "f";
  public static final String PerlRegexFieldFixed ="\\d";


  /** The no argument constructor.
   */
  public FixedDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
    classXDFNodeName = super.getClassXDFNodeName()+ "||" + "fixed";
    //add attributes
    /**precision:The precision of this fixed field which is the number of digits
     * to the right of the '.'.
     */
    attribOrder.add(0,"precision");

    /**width: The entire width of this fixed field
     */
    attribOrder.add(0, "width");

    attribHash.put("lessThanValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("lessThanOrEqualValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("greaterThanValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("greaterThanOrEqualValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("infiniteValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("infiniteNegativeValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("noDataValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("width", new XMLAttribute( new Integer(0), Constants.NUMBER_TYPE));
    attribHash.put("precision", new XMLAttribute(new Integer(0), Constants.NUMBER_TYPE));

  }

  //
  //Get/Set Methods
  //

  /**setLessThanValue: set the *lessThanValue* attribute
   * @return: the current *lessThanValue* attribute
   */
  public Number setLessThanValue(Number numLessThanValue) {
    return (Number) ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(numLessThanValue);
  }
  /**getLessThanValue
   * @return: the current *lessThanValue* attribute
   */
  public Number getLessThanValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("lessThanValue")).getAttribValue();
  }

  /**setLessThanValueOrEqualValue: set the *lessThanValueOrEqualValue* attribute
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public Number setLessThanOrEqualValue(Number numLessThanOrEqualValue) {
    return (Number) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(numLessThanOrEqualValue);
  }
  /**getlessThanOrEqualValue
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public Number getlessThanOrEqualValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).getAttribValue();
  }

  /**setgreaterThanValue: set the *greaterThanValue* attribute
   * @return: the current *greaterThanValue* attribute
   */
  public Number setGreaterThanValue(Number numGreaterThanValue) {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(numGreaterThanValue);
  }
  /**getGreaterThanValue
   * @return: the current *greaterThanValue* attribute
   */
  public Number getGreaterThanValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanValue")).getAttribValue();
  }

  /**setGreaterThanOrEqualValue: set the *greaterThanOrEqualValue* attribute
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public Number setGreaterThanOrEqualValue(Number numGreaterThanOrEqualValue) {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(numGreaterThanOrEqualValue);
  }
  /**getGreaterThanOrEqualValue
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public Number getGreaterThanOrEqualValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).getAttribValue();
  }

  /**setInfiniteValue: set the *infiniteValue* attribute
   * @return: the current *infiniteValue* attribute
   */
  public Number setInfiniteValue(Number numInfiniteValue) {
    return (Number) ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(numInfiniteValue);
  }
  /**getInfiniteValue
   * @return: the current *infiniteValue* attribute
   */
  public Number getInfiniteValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("infiniteValue")).getAttribValue();
  }

  /**setInfiniteNegativeValue: set the *infiniteNegativeValue* attribute
   * @return: the current *infiniteNegativeValue* attribute
   */
  public Number setInfiniteNegativeValue(Number numInfiniteNegativeValue) {
    return (Number) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(numInfiniteNegativeValue);
  }
  /**getInfiniteNegativeValue
   * @return: the current *infiniteNegativeValue* attribute
   */
  public Number getInfiniteNegativeValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).getAttribValue();
  }

  /**setNoDataValue: set the *noDataValue* attribute
   * @return: the current *noDataValue* attribute
   */
  public Number setNoDataValue(Number numNoDataValue) {
    return (Number) ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(numNoDataValue);
  }
  /**getNoDataValue
   * @return: the current *noDataValue* attribute
   */
  public Number getNoDataValue()
  {
    return (Number) ((XMLAttribute) attribHash.get("noDataValue")).getAttribValue();
  }

  /**setWidth: set the *width* attribute
   * @return: the current *width* attribute
   */
  public Number setWidth(Number numWidth) {
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
   * @Return: the number of bytes this XDF::FixedDataFormat holds.
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
    notation +="[+-]?" + PerlRegexFieldFixed + "{1," + leadingLength + "}\\.";
    notation +=PerlRegexFieldFixed + "{1," + precision + "}";

    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {

  return  "%" + numOfBytes() + "." + getPrecision().intValue() + PerlSprintfFieldFixed  ;

}

  /** fortranNotation: The fortran style notation for this object.
   */
  public String fortranNotation() {
    return "F"+ numOfBytes() + "." + getPrecision().intValue();
  }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/10/16 14:55:11  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
