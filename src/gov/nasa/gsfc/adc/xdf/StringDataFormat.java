// XDF StringDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.*;

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



/**
 * StringDataFormat.java:describes string data.
 * @version $Revision$
 */


public class StringDataFormat extends DataFormat {
  //
  //Fields
  //
  public static final String PerlSprintfFieldString = "s";
  public static final String PerlRegexFieldString = ".";


  /** The no argument constructor.
   */
  public StringDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
    classXDFNodeName = super.getClassXDFNodeName()+ "||" + "string";
    attribOrder.add(0, "length");  //add length as the first attribute;

    attribHash.put("lessThanValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("lessThanOrEqualValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("greaterThanValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("greaterThanOrEqualValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("infiniteValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("infiniteNegativeValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("noDataValue", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("length", new XMLAttribute(new Integer(0), Constants.NUMBER_TYPE));
  }

  //
  //Get/Set Methods
  //

  /**setLessThanValue: set the *lessThanValue* attribute
   * @return: the current *lessThanValue* attribute
   */
  public String setLessThanValue(String strLessThanValue) {
    return (String) ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(strLessThanValue);
  }
  /**getLessThanValue
   * @return: the current *lessThanValue* attribute
   */
  public String getLessThanValue()
  {
    return (String) ((XMLAttribute) attribHash.get("lessThanValue")).getAttribValue();
  }

  /**setLessThanValueOrEqualValue: set the *lessThanValueOrEqualValue* attribute
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public String setLessThanOrEqualValue(String strLessThanOrEqualValue) {
    return (String) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(strLessThanOrEqualValue);
  }
  /**getlessThanOrEqualValue
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public String getlessThanOrEqualValue()
  {
    return (String) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).getAttribValue();
  }

  /**setgreaterThanValue: set the *greaterThanValue* attribute
   * @return: the current *greaterThanValue* attribute
   */
  public String setGreaterThanValue(String strGreaterThanValue) {
    return (String) ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(strGreaterThanValue);
  }
  /**getGreaterThanValue
   * @return: the current *greaterThanValue* attribute
   */
  public String getGreaterThanValue()
  {
    return (String) ((XMLAttribute) attribHash.get("greaterThanValue")).getAttribValue();
  }

  /**setGreaterThanOrEqualValue: set the *greaterThanOrEqualValue* attribute
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public String setGreaterThanOrEqualValue(String strGreaterThanOrEqualValue) {
    return (String) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(strGreaterThanOrEqualValue);
  }
  /**getGreaterThanOrEqualValue
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public String getGreaterThanOrEqualValue()
  {
    return (String) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).getAttribValue();
  }

  /**setInfiniteValue: set the *infiniteValue* attribute
   * @return: the current *infiniteValue* attribute
   */
  public String setInfiniteValue(String strInfiniteValue) {
    return (String) ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(strInfiniteValue);
  }
  /**getInfiniteValue
   * @return: the current *infiniteValue* attribute
   */
  public String getInfiniteValue()
  {
    return (String) ((XMLAttribute) attribHash.get("infiniteValue")).getAttribValue();
  }

  /**setInfiniteNegativeValue: set the *infiniteNegativeValue* attribute
   * @return: the current *infiniteNegativeValue* attribute
   */
  public String setInfiniteNegativeValue(String strInfiniteNegativeValue) {
    return (String) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(strInfiniteNegativeValue);
  }
  /**getInfiniteNegativeValue
   * @return: the current *infiniteNegativeValue* attribute
   */
  public String getInfiniteNegativeValue()
  {
    return (String) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).getAttribValue();
  }

  /**setNoDataValue: set the *noDataValue* attribute
   * @return: the current *noDataValue* attribute
   */
  public String setNoDataValue(String strNoDataValue) {
    return (String) ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(strNoDataValue);
  }
  /**getNoDataValue
   * @return: the current *noDataValue* attribute
   */
  public String getNoDataValue()
  {
    return (String) ((XMLAttribute) attribHash.get("noDataValue")).getAttribValue();
  }

  /**setlength: set the *length* attribute
   * @return: the current *length* attribute
   */
  public Number setLength(Number numLength) {
    return (Number) ((XMLAttribute) attribHash.get("length")).setAttribValue(numLength);
  }
  /**getLength
   * @return: the current *length* attribute
   */
  public Number getLength()
  {
    return (Number) ((XMLAttribute) attribHash.get("length")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /**numOfBytes: A convenience method.
   * @Return: the number of bytes this XDF::StringDataFormat holds.
   */
  public int numOfBytes() {
    return getLength().intValue();
  }

  public String templateNotation(String strEndian, String strEncoding)  {
    return "A" + numOfBytes();
  }

  public String regexNotation() {
    String notation = "(";
    int width = numOfBytes();
    int beforeWhiteSpace = width - 1;
    if (beforeWhiteSpace > 0)
      notation += "\\s{0," + beforeWhiteSpace + "}";
    notation +=PerlRegexFieldString + "{1," + width + "}";
    notation +=")";
    return notation;
  }

  /** sprintfNotation: returns sprintf field notation
   *
   */
  public String sprintfNotation() {

  return  "%" + numOfBytes() + PerlSprintfFieldString;

}

  /** fortranNotation: The fortran style notation for this object.
   */
  public String fortranNotation() {
    return "A"+ numOfBytes();
  }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/10/16 14:49:11  kelly
 * pretty much completed the class.  --k.z.
 *
 * Revision 1.1  2000/10/11 19:09:17  kelly
 * created the class
 *
 */
