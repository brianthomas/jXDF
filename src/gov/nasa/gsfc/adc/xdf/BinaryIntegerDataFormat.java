// XDF BinaryIntegerDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

import java.util.Hashtable;

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
 *  describes binary integer numbers.
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
     specificDataFormatName = "binaryInteger";
    //add attributes
    attribOrder.add(0,"bits");
    attribOrder.add(0, "signed");
    attribHash.put("bits", new XMLAttribute(new Integer(DefaultBinaryIntegerBits), Constants.NUMBER_TYPE));
    attribHash.put("signed", new XMLAttribute("yes", Constants.STRING_TYPE));

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

  /** set the *bits* attribute
   */
  public void setBits(Number numBits) {
     ((XMLAttribute) attribHash.get("bits")).setAttribValue(numBits);

  }
  /**
   * @return the current *bits* attribute
   */
  public Number getBits()
  {
    return (Number) ((XMLAttribute) attribHash.get("bits")).getAttribValue();
  }

  /** set the *signed* attribute
   */
  public void setSigned(String strSigned) {

    if (!strSigned.equals("yes")  && !strSigned.equals("yes") ) {
      Log.error("*signed* attribute can only be set to yes or no");
      Log.error("tend to set as" + strSigned);
      Log.error("invalid. ignoring request");
      return;
    }

    ((XMLAttribute) attribHash.get("signed")).setAttribValue(strSigned);

  }

  /**
   * @return the current *signed* attribute
   */
  public String getSigned()
  {
    return (String) ((XMLAttribute) attribHash.get("signed")).getAttribValue();
  }
  //
  //Other PUBLIC Methods
  //

  /** A convenience method.
   * @Return: the number of bytes this BinaryIntegerDataFormat holds.
   */
  public int numOfBytes() {
    return getBits().intValue()/8;
  }

  //pass in param??? double check???
  public String templateNotation(String strEndian, String strEncoding) {
    if (numOfBytes() >4) {
      Log.error("BinaryInteger cant handle > 32 bit Integer Numbers");
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
 * Revision 1.6  2000/11/16 19:51:59  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/10/27 21:11:00  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.3  2000/10/26 20:16:34  kelly
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.2  2000/10/17 21:59:19  kelly
 * removed boolean type, declare *signed" attribute as String.  -k.z.
 *
 * Revision 1.1  2000/10/16 14:51:24  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
