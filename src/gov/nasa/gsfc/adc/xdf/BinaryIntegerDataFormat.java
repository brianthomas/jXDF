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
  int DefaultBinaryIntegerBits = 64;
  String DefaultBinaryIntegerSigned = "yes";


  /** The no argument constructor.
   */
  public BinaryIntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
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
  public void setBits(Integer numBits) {
     ((XMLAttribute) attribHash.get("bits")).setAttribValue(numBits);

  }
  /**
   * @return the current *bits* attribute.  
   */
  public Integer getBits()
  {
    return (Integer) ((XMLAttribute) attribHash.get("bits")).getAttribValue();
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

   //
   // Private Methods
   //

  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
   private void init() {
      specificDataFormatName = "binaryInteger";
     //add attributes
     attribOrder.add(0,"bits");
     attribOrder.add(0, "signed");

     attribHash.put("bits", new XMLAttribute(new Integer(DefaultBinaryIntegerBits), Constants.INTEGER_TYPE));
     attribHash.put("signed", new XMLAttribute(DefaultBinaryIntegerSigned, Constants.STRING_TYPE));

  }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.8  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
 * Revision 1.7  2000/11/20 22:05:50  thomas
 * plit up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
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
