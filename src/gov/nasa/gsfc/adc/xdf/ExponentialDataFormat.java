// XDF ExponentialDataFormat Class
// CVS $Id$

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


package gov.nasa.gsfc.adc.xdf;

import java.util.Hashtable;

/**
 *  describes exponential (ASCII) floating point numbers
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

  /** set the *infiniteNegativeValue* attribute
   */
  public void setInfiniteNegativeValue(Object numInfiniteNegativeValue) {
     ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(numInfiniteNegativeValue);
  }


  /** set the *noDataValue* attribute
   */
  public void setNoDataValue(Object numNoDataValue) {
     ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(numNoDataValue);
  }

  /** set the *width* attribute
   */
  public void setWidth(Object numWidth) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);

  }
  /**
   * @return the current *width* attribute
   */
  public Number getWidth()
  {
    return (Number) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /** set the *precision* attribute
   */
  public void setPrecision(Number precision) {
     ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);

  }
  /**
   * @return the current *precision* attribute
   */
  public Number getPrecision()
  {
    return (Number) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /**  A convenience method.
   * @return the number of bytes this ExponentialDataFormat holds.
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
 * Revision 1.6  2000/11/16 19:58:12  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:43:49  thomas
 * Re-arranged header info to standard format for package. -b.t.
 *
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
