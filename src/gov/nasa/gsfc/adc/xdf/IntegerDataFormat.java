

// XDF IntegerDataFormat Class
// CVS $Id$

// IntegerDataFormat.java Copyright (C) 2000 Brian Thomas,
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

import org.xml.sax.Attributes;

/**
 * IntegerDataFormat.java:IntegerDataFormat class describes the data format
 * of objects which require such description (Field, Array).
 * @version $Revision$
 */


public class IntegerDataFormat extends NumberDataFormat {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String WIDTH_XML_ATTRIBUTE_NAME = "width";
   private static final String TYPE_XML_ATTRIBUTE_NAME = "type";
 
   /* default attribute settings */
   public static final int DEFAULT_WIDTH = 0;
   public static final String DEFAULT_TYPE = new String(Constants.INTEGER_TYPE_DECIMAL);

  //
  // Constructors
  //

  /** The no argument constructor.
   */
  public IntegerDataFormat ()  //DataFormat no-arg constructor should be been called
  {
    init();
  }


  /**
   * constructor with a width
   */
  public IntegerDataFormat (int width)  
  {
    init();
    setWidth (width);  
  }

   //
   // Get/Set Methods
   //

   /** set the *type* attribute
     */
   public void setType(String strType) {

      if (!Utility.isValidIntegerType(strType)) 
      {
         Log.warnln("Invalid type for IntegerDataFormat.getType(). Ignoring set request.");
         return;
      }

      ((Attribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(strType);
   }

   /**
       @return the current *type* attribute
    */
   public String getType()
   {
      return (String) ((Attribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *width* attribute
    */
   public void setWidth(Integer width) {

      if (Utility.isValidNumberObject(width)) 
      {
         ((Attribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).setAttribValue(width);
         generateFormatPattern();
      } else 
         Log.warnln("Invalid object for IntegerDataFormat.setWidth(). Ignoring set request.");

   }

   /** set the *width* attribute
    */
   public void setWidth (int width) {
      Integer value = new Integer(width);
      setWidth(value);
   }

   /**
       @return the current *width* attribute
    */
   public Integer getWidth()
   {
      return (Integer) ((Attribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   //
   // Other PUBLIC Methods
   //

   /** A convenience method.
       @return the number of bytes this IntegerDataFormat holds.
    */
   public int numOfBytes() {
      return getWidth().intValue();
   }

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   public void setAttributes (Attributes attrs) {
      super.setAttributes(attrs);
      generateFormatPattern();
   }

   /**
       @deprecated use the setAttributes method instead
    */
   public void setXMLAttributes (Attributes attrs) { this.setAttributes(attrs); } 

   //
   // Private Methods
   //

  /** This is the MessageFormat that should be used to print out data
      within the slice of the array covered by this object. This method is
      used by the dataCube in its toXMLOutput method. 
   */

  // separate method to minimize the number of times we do this.
  private void generateFormatPattern ( ) {

     StringBuffer leftpattern = new StringBuffer();
     StringBuffer etemplate = new StringBuffer();

     int wsize = getWidth().intValue() - 1;

     if (wsize > 1)
        etemplate.append("#");

     while (wsize-- > 2)
        leftpattern.append("#");
     leftpattern.append("0");

     // finish building the template
     etemplate.append(leftpattern.toString());
     etemplate.append(";-"+leftpattern.toString());

     formatPattern = etemplate.toString();
  }

  // 
  // Protected Methods
  //

  /** Special method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  protected void init() {
 
    super.init();

    specificDataFormatName = "integer";

    //add attributes
    attribOrder.add(0, TYPE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, WIDTH_XML_ATTRIBUTE_NAME);

    attribHash.put( TYPE_XML_ATTRIBUTE_NAME, new Attribute(DEFAULT_TYPE, Constants.STRING_TYPE));
    attribHash.put( WIDTH_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_WIDTH), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }

}

