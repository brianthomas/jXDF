// XDF BinaryIntegerDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

// BinaryIntegerDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 * BinaryIntegerDataFormat.java:describes binary integer numbers.
 * @version $Revision$
 */


public class BinaryIntegerDataFormat extends DataFormat {
  //
  //Fields
  //
  public static final String PerlSprintfFieldBinaryInteger = "s";
  public static final String PerlRegexFieldBinaryInteger = "\\.";
  public static final int DefaultBinaryIntegerBits = 32;
  public static final boolean DefaultBinaryIntegerSigned = true;


  /** The no argument constructor.
   */
  public BinaryIntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
    classXDFNodeName = super.getClassXDFNodeName()+ "||" + "binaryInteger";
    //add attributes
    attribOrder.add(0,"bits");
    attribOrder.add(0, "signed");

    attribHash.put("lessThanValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("lessThanOrEqualValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("greaterThanValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("greaterThanOrEqualValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("infiniteValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("infiniteNegativeValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("noDataValue", new XMLAttribute(null, Constants.NUMBER_TYPE));
    attribHash.put("bits", new XMLAttribute(new Integer(DefaultBinaryIntegerBits), Constants.NUMBER_TYPE));
    attribHash.put("signed", new XMLAttribute(new Boolean(DefaultBinaryIntegerSigned), Constants.BOOLEAN_TYPE));

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

  /**setBits: set the *bits* attribute
   * @return: the current *bits* attribute
   */
  public Number setBits(Number numBits) {
    return (Number) ((XMLAttribute) attribHash.get("bits")).setAttribValue(numBits);

  }
  /**getbits
   * @return: the current *bits* attribute
   */
  public Number getBits()
  {
    return (Number) ((XMLAttribute) attribHash.get("bits")).getAttribValue();
  }

  /**setSigned: set the *signed* attribute
   * @return: the current *signed* attribute
   */
  public Boolean setSigned(Boolean signed) {
    return (Boolean) ((XMLAttribute) attribHash.get("signed")).setAttribValue(signed);

  }
  /**getSigned
   * @return: the current *signed* attribute
   */
  public Boolean getSigned()
  {
    return (Boolean) ((XMLAttribute) attribHash.get("signed")).getAttribValue();
  }
  //
  //Other PUBLIC Methods
  //

  /**numOfBytes: A convenience method.
   * @Return: the number of bytes this XDF::BinaryIntegerDataFormat holds.
   */
  public int numOfBytes() {
    return getBits().intValue()/8;
  }

  //pass in param??? double check???
  public String templateNotation(String strEndian, String strEncoding) {
    if (numOfBytes() >4) {
      Log.error("XDF::BinaryInteger cant handle > 32 bit Integer Numbers");
      Log.error("returning null");
      return null;
    }

    if (!Utility.isValidEndian(strEndian)) {
      Log.error("not a valid endian, returning null");
      return null;
    }

    // we hardwired 'BigEndian" response here. Bad!
    if (strEndian.equals(Constants.BIG_ENDIAN))
      return "N";
    else
      return "V";
  }

  public String regexNotation() {
    String notation = "(";
    int width = numOfBytes();
    int beforeWhiteSpace = width - 1;
    if (beforeWhiteSpace > 0)
      notation += "\\s{0," + beforeWhiteSpace + "}";
    notation +=PerlRegexFieldBinaryInteger + "{1," + width + "}";
    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {

  return  "%" + numOfBytes() + PerlSprintfFieldBinaryInteger;

}

  /** fortranNotation: The fortran style notation for this object.
   */
  public void fortranNotation() {
    Log.error("There is not FORTRAN representation for binary data");
  }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/10/16 14:51:24  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
