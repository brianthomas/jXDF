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
 *  describes binary floating point numbers.
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

    attribHash.put("bits", new XMLAttribute(new Integer(DefaultBinaryFloatBits), Constants.INTEGER_TYPE));
  }

  //
  //Set Methods
  //

  /** set the *lessThanValue* attribute
   */
  public void setLessThanValue(Object numLessThanValue) {
    if (numLessThanValue.getClass().getName().endsWith("Number"))
       ((XMLAttribute) attribHash.get("lessThanValue")).setAttribValue(numLessThanValue);
    else
      Log.warnln("Couldnt set the lessThanValue, ignoring request.");
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
   *
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

  /** set the *bits* attribute
   */
  public void setBits(Integer numBits) {

    int bits = numBits.intValue();
    if ((bits == 32) || (bits == 64)) //check that bits are either 32 or 64
       ((XMLAttribute) attribHash.get("bits")).setAttribValue(numBits);
    else {
      Log.warn("number of bits for binary float has to be either 32 or 64");
      Log.warnln("ignoring 'set' request.");
    }
  }

  /**
   * @return the current *bits* attribute
   */
  public Integer getBits()
  {
    return (Integer) ((XMLAttribute) attribHash.get("bits")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** A convenience method.
   * @Return: the number of bytes this BinaryFloatDataFormat holds.
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
 * Revision 1.6  2000/11/20 22:05:50  thomas
 * plit up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.5  2000/11/16 19:51:46  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.4  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
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
