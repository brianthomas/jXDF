// XDF BinaryFloatDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.Hashtable;

// BinaryFloatDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 * BinaryFloatDataFormat.java:describes binary floating point numbers.
 * @version $Revision$
 */


public class BinaryFloatDataFormat extends DataFormat {
  //
  //Fields
  //
  public static final String PerlSprintfFieldBinaryFloat = "s";
  public static final String PerlRegexFieldBinaryFloat = "\\.";
  public static final int DefaultBinaryFloatBits = 32;


  /** The no argument constructor.
   */
  public BinaryFloatDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
    specificDataFormatName = "binaryFloat";
    attribOrder.add(0, "bits");  //add bits as the first attribute;

    attribHash.put("bits", new XMLAttribute(new Integer(DefaultBinaryFloatBits), Constants.NUMBER_TYPE));
  }

  //
  //Set Methods
  //

  /**setLessThanValue: set the *lessThanValue* attribute
   * @return: the current *lessThanValue* attribute
   */
  public Object setLessThanValue(Object numLessThanValue) {
    if (numLessThanValue.getClass().getName().endsWith("Number"))
      return (Number) ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(numLessThanValue);
    else
      return null;
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

  /**setBits: set the *bits* attribute
   * @return: the current *bits* attribute if successful, null if not
   */
  public Number setBits(Number numBits) {

    int bits = numBits.intValue();
    if ((bits == 32) || (bits == 64)) //check that bits are either 32 or 64
      return (Number) ((XMLAttribute) attribHash.get("bits")).setAttribValue(numBits);
    else {
      Log.error("number of bits for binary float has to be either 32 or 64");
      Log.error("ignore 'set' request, returning null");
      return null;
    }
  }
  /**getbits
   * @return: the current *bits* attribute
   */
  public Number getBits()
  {
    return (Number) ((XMLAttribute) attribHash.get("bits")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /**numOfBytes: A convenience method.
   * @Return: the number of bytes this XDF::BinaryFloatDataFormat holds.
   */
  public int numOfBytes() {
    return getBits().intValue()/8;
  }

  //pass in param??? double check???
  public String templateNotation(String strEndian, String strEncoding ) {
    return "d";   //we always use double to prevent perl rounding
                  // that can occur for using the 32-bit "f"
  }

  public String regexNotation() {
    String notation = "(";
    int width = numOfBytes();
    int beforeWhiteSpace = width - 1;
    if (beforeWhiteSpace > 0)
      notation += "\\s{0," + beforeWhiteSpace + "}";
    notation +=PerlRegexFieldBinaryFloat + "{1," + width + "}";
    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {

  return  "%" + numOfBytes() + PerlSprintfFieldBinaryFloat;

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
 * Revision 1.3  2000/10/27 21:10:29  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:15:53  kelly
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.1  2000/10/16 14:50:48  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
