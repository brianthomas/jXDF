

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
    describes string data.
   @version $Revision$
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
    attribHash.put("length", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));
  }

  //
  //Set Methods
  //

  /**set the *lessThanValue* attribute
   */
  public void setLessThanValue(Object strLessThanValue) {
     ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(strLessThanValue);
  }

  /**set the *lessThanValueOrEqualValue* attribute
   */
  public void setLessThanOrEqualValue(Object strLessThanOrEqualValue) {
     ((XMLAttribute) attribHash.get("lessThanOrEqualValue")).setAttribValue(strLessThanOrEqualValue);
  }

  /**set the *greaterThanValue* attribute
   */
  public void setGreaterThanValue(Object strGreaterThanValue) {
    ((XMLAttribute) attribHash.get("greaterThanValue")).setAttribValue(strGreaterThanValue);
  }

  /**set the *greaterThanOrEqualValue* attribute
   */
  public void setGreaterThanOrEqualValue(Object strGreaterThanOrEqualValue) {
    ((XMLAttribute) attribHash.get("greaterThanOrEqualValue")).setAttribValue(strGreaterThanOrEqualValue);
  }

  /** set the *infiniteValue* attribute
   */
  public void setInfiniteValue(Object strInfiniteValue) {
    ((XMLAttribute) attribHash.get("infiniteValue")).setAttribValue(strInfiniteValue);
  }

  /**set the *infiniteNegativeValue* attribute
   */
  public void setInfiniteNegativeValue(Object strInfiniteNegativeValue) {
    ((XMLAttribute) attribHash.get("infiniteNegativeValue")).setAttribValue(strInfiniteNegativeValue);
  }

  /**set the *noDataValue* attribute
   */
  public void setNoDataValue(Object strNoDataValue) {
     ((XMLAttribute) attribHash.get("noDataValue")).setAttribValue(strNoDataValue);
  }

  /**setlength: set the *length* attribute
   */
  public void setLength(Integer numLength) {
     ((XMLAttribute) attribHash.get("length")).setAttribValue(numLength);
  }

  /**getLength
   * @return the current *length* attribute
   */
  public Integer getLength()
  {
    return (Integer) ((XMLAttribute) attribHash.get("length")).getAttribValue();
  }
  //
  //Other PUBLIC Methods
  //

  /** A convenience method.
   * @return the number of bytes this StringDataFormat holds.
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
 * Revision 1.9  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.8  2000/11/16 20:09:02  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.7  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
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
