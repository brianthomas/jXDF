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
       ((XMLAttribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(numBits);
    else {
      Log.warn("The requested number of bits:["+bits+"] for binary float is not allowed");
      Log.warnln("ignoring 'set' request.");
    }
  }

  /** set the *bits* attribute
   */
  public void setBits (int bits) {

     if (Utility.isValidFloatBits(bits))
        ((XMLAttribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(new Integer(bits));
     else {
        Log.warn("The requested number of bits:["+bits+"] for binary float is not allowed");
        Log.warnln("ignoring 'set' request.");
     }
  }

  /**
   * @return the current *bits* attribute
   */
  public Integer getBits()
  {
     return (Integer) ((XMLAttribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Other PUBLIC Methods
  //

  /** A convenience method.
   * @Return: the number of bytes this BinaryFloatDataFormat holds.
   */
  public int numOfBytes() {
     return getBits().intValue()/8;
  }

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   // Note: we never have a need for ASCII formatting of these numbers
   // so this isnt needed.
/*
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
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

      attribHash.put(BITS_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_BINARY_FLOAT_BITS), Constants.INTEGER_TYPE));
   }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/05/04 20:20:09  thomas
 * added super.init() in init() method. This may have to be undone
 * in the future.  Consider this to be a 'side-ways' change.
 *
 * Revision 1.8  2001/02/07 18:42:25  thomas
 * Changes to enable binary read/writing. Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.7  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
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
