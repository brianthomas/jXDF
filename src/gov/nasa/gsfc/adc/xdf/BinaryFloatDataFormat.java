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


public class BinaryFloatDataFormat extends NumberDataFormat {

  //
  // Fields
  //

  /* XML attribute names */
  private static final String BITS_XML_ATTRIBUTE_NAME = "bits";
 
  private int numOfBytes = 0;

  /* default attribute setting */
  public static final int DEFAULT_BINARY_FLOAT_BITS = 32;


  //
  // Constructors 
  //

  /** The no argument constructor.
   */
  public BinaryFloatDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  //
  // Get/Set Methods
  //

  /** set the *bits* attribute
   */
  public void setBits (Integer numBits) {

    int bits = numBits.intValue();
    if (Utility.isValidFloatBits(bits)) 
    {
       ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(numBits);
       updateNumOfBytes();
    } else {
      Log.warn("The requested number of bits:["+bits+"] for binary float is not allowed");
      Log.warnln("ignoring 'set' request.");
    }
  }

  /** set the *bits* attribute
   */
  public void setBits (int bits) {

     if (Utility.isValidFloatBits(bits))
     {
        ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(new Integer(bits));
        updateNumOfBytes();
     } else {
        Log.warn("The requested number of bits:["+bits+"] for binary float is not allowed");
        Log.warnln("ignoring 'set' request.");
     }
  }

  /**
   * @return the current *bits* attribute
   */
  public Integer getBits()
  {
     return (Integer) ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Other PUBLIC Methods
  //

  /** A convenience method.
   * @Return: the number of bytes this BinaryFloatDataFormat holds.
   */
  public int numOfBytes() 
  {
     return numOfBytes;
  }

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   // Note: we never have a need for ASCII formatting of these numbers
   // so this isnt needed.
/*
   public void setAttributes (Attributes attrs) {
      super.setAttributes(attrs);
      generateFormatPattern();
   }
*/

  //
  // Protected Methods
  //

  /** Special method used by constructor methods to
      easily build the XML attribute list for a given class.
   */

   protected void init() {

      super.init();

      specificDataFormatName = "binaryFloat";

      attribOrder.add(0, BITS_XML_ATTRIBUTE_NAME);  //add bits as the first attribute;

      attribHash.put(BITS_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_BINARY_FLOAT_BITS), Constants.INTEGER_TYPE));
      updateNumOfBytes();
   }

   private void updateNumOfBytes () {
      numOfBytes = getBits().intValue()/8;
   }

}

