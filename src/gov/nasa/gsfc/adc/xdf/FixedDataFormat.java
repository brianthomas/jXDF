

// XDF FixedDataFormat Class
// CVS $Id$

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

package gov.nasa.gsfc.adc.xdf;
  
import java.util.Hashtable;


/**
 *  describes (ASCII) fixed (floating point) numbers.
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
     specificDataFormatName = "fixed";
    //add attributes
    /**precision:The precision of this fixed field which is the number of digits
     * to the right of the '.'.
     */
    attribOrder.add(0,"precision");

    /**width: The entire width of this fixed field
     */
    attribOrder.add(0, "width");

    attribHash.put("width", new XMLAttribute( new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("precision", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));

  }

  //
  //Get/Set Methods
  //

  /** set the *lessThanValue* attribute
   * @return the current *lessThanValue* attribute
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
  public void setWidth(Integer numWidth) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);

  }
  /**
   * @return the current *width* attribute
   */
  public Integer getWidth()
  {
    return (Integer) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /** set the *precision* attribute
   */
  public void setPrecision(Integer precision) {
     ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);

  }
  /**
   * @return the current *precision* attribute
   */
  public Integer getPrecision()
  {
    return (Integer) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** A convenience method that return the number of bytes this FixedDataFormat holds.
   */
  public int numOfBytes() {
    return getWidth().intValue();
  }

  /**probably this method will go away
   */

  public String templateNotation(String strEndian, String strEncoding) {
    return "A"+numOfBytes();
  }

  /**probably this method will go away
   */

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

  /**probably this method will go away
   */

  public String sprintfNotation() {

  return  "%" + numOfBytes() + "." + getPrecision().intValue() + PerlSprintfFieldFixed  ;

}

/**probably this method will go away
   */

  public String fortranNotation() {
    return "F"+ numOfBytes() + "." + getPrecision().intValue();
  }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.7  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.6  2000/11/16 19:59:51  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:48:50  thomas
 * Rearranged header to follow package standard. -b.t.
 *
 * Revision 1.3  2000/10/27 21:19:30  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.1  2000/10/16 14:55:11  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
