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


public class BinaryIntegerDataFormat extends NumberDataFormat {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String BITS_XML_ATTRIBUTE_NAME = "bits";
  private static final String SIGNED_XML_ATTRIBUTE_NAME = "signed";

  private int numOfBytes = 0;

  /* default attribute settings */
  public static final int DEFAULT_BINARY_INTEGER_BITS = 16;
  public static final String DEFAULT_BINARY_INTEGER_SIGNED = "yes";


  //
  // Constructors
  //

  /** The no argument constructor.
   */
  public BinaryIntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }

  //
  // Get/Set Methods
  //

  /** set the *bits* attribute
   */
  public void setBits(Integer numBits) {

     if( numBits != null) 
     {
        if(Utility.isValidIntegerBits(numBits.intValue())) { 
           ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(numBits);
           updateNumOfBytes(); 
        } else { 
           Log.warnln(numBits.toString()+" is not a valid number of BinaryInteger bits, ignoring set request.");
        }
     } else { 
        Log.warnln("Cannot set BinaryInteger bits to null, ignoring set request.");
     }

  }

  /** set the *bits* attribute
  */
  public void setBits (int numBits) {

        if(Utility.isValidIntegerBits(numBits)) { 
           ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).setAttribValue(new Integer(numBits));
           updateNumOfBytes(); 
        } else { 
           Log.warnln(numBits+" is not a valid number of BinaryInteger bits, ignoring set request.");
        }

  }

  /**
   * @return the current *bits* attribute.  
   */
  public Integer getBits()
  {
     return (Integer) ((Attribute) attribHash.get(BITS_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


    public void setSigned (boolean signed) {
	if (signed)
	    ((Attribute) attribHash.get(SIGNED_XML_ATTRIBUTE_NAME)).setAttribValue("yes");
	else
	    ((Attribute) attribHash.get(SIGNED_XML_ATTRIBUTE_NAME)).setAttribValue("no");
    }


    /*
    public boolean getSigned()
    {
	String ignedStr = (String) ((Attribute) attribHash.get(SIGNED_XML_ATTRIBUTE_NAME)).getAttribValue();
	if (signedStr.equals("yes"))
	    return true;
	else
	    return false;
    }
    */


  /** set the *signed* attribute
   */
  public void setSigned(String strSigned) {

    if (!Utility.isValidBinaryIntegerSigned(strSigned)) {
      Log.warnln("BinaryInteger signed attribute can only be set to 'yes' or 'no'. Ignoring set request.");
      return;
    }

    ((Attribute) attribHash.get(SIGNED_XML_ATTRIBUTE_NAME)).setAttribValue(strSigned);

  }

  /**
   * @return the current *signed* attribute
   */
  public String getSigned()
  {
     return (String) ((Attribute) attribHash.get(SIGNED_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   //
   // Other PUBLIC Methods
   //

   /** A convenience method.
     @Return: the number of bytes this BinaryIntegerDataFormat holds.
   */
   public int numOfBytes() {
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
      build the XML attribute list for a given class.
   */
   protected void init() {

      super.init();

      specificDataFormatName = "binaryInteger";
     //add attributes
     attribOrder.add(0, BITS_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, SIGNED_XML_ATTRIBUTE_NAME);

     attribHash.put( BITS_XML_ATTRIBUTE_NAME, 
                      new Attribute(new Integer(DEFAULT_BINARY_INTEGER_BITS), Constants.INTEGER_TYPE));
     attribHash.put( SIGNED_XML_ATTRIBUTE_NAME, 
                      new Attribute(DEFAULT_BINARY_INTEGER_SIGNED, Constants.STRING_TYPE));

     updateNumOfBytes(); 
  }

  private void updateNumOfBytes () {
     numOfBytes = getBits().intValue()/8;
  }

}

