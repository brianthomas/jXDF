
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
import java.util.Hashtable;
import java.io.OutputStream;

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
  private String parentClassXDFNodeName;


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
    specificDataFormatName = "string";
    attribOrder.add(0, "length");  //add length as the first attribute;
    attribHash.put("length", new XMLAttribute(new Integer(0), Constants.NUMBER_TYPE));
  }

  //
  //Set Methods
  //

  /**setLessThanValue: set the *lessThanValue* attribute
   * @return: the current *lessThanValue* attribute
   */
  public Object setLessThanValue(Object strLessThanValue) {
    return (String) ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(strLessThanValue);
  }

  /**setLessThanValueOrEqualValue: set the *lessThanValueOrEqualValue* attribute
   * @return: the current *lessThanOrEqualValue* attribute
   */
  public Object setLessThanOrEqualValue(Object strLessThanOrEqualValue) {
    return (String) ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(strLessThanOrEqualValue);
  }

  /**setgreaterThanValue: set the *greaterThanValue* attribute
   * @return: the current *greaterThanValue* attribute
   */
  public Object setGreaterThanValue(Object strGreaterThanValue) {
    return (String) ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(strGreaterThanValue);
  }

  /**setGreaterThanOrEqualValue: set the *greaterThanOrEqualValue* attribute
   * @return: the current *greaterThanOrEqualValue* attribute
   */
  public Object setGreaterThanOrEqualValue(Object strGreaterThanOrEqualValue) {
    return (String) ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(strGreaterThanOrEqualValue);
  }

  /**setInfiniteValue: set the *infiniteValue* attribute
   * @return: the current *infiniteValue* attribute
   */
  public Object setInfiniteValue(Object strInfiniteValue) {
    return (String) ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(strInfiniteValue);
  }

  /**setInfiniteNegativeValue: set the *infiniteNegativeValue* attribute
   * @return: the current *infiniteNegativeValue* attribute
   */
  public Object setInfiniteNegativeValue(Object strInfiniteNegativeValue) {
    return (String) ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(strInfiniteNegativeValue);
  }

  /**setNoDataValue: set the *noDataValue* attribute
   * @return: the current *noDataValue* attribute
   */
  public Object setNoDataValue(Object strNoDataValue) {
    return (String) ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(strNoDataValue);
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

  /** override the base object method to add a little tailoring
   */
}
/* Modification History:
 *
 * $Log$
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
