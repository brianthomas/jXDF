

// XDF FloatDataFormat Class
// CVS $Id$

// FloatDataFormat.java Copyright (C) 2000 Brian Thomas,
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
  
import org.xml.sax.AttributeList;


/**
  Describes floating point numbers written as ASCII.
  Two different output styles are supported. When the exponent attribute
  is non-zero, numbers are read/written in FORTRAN 'E' format, in all other 
  cases an 'F' style read/write format is used.
  Definitions of number fields are for example:

  130050.0000001E-034

         |----|  |--| 
           P      X 
  |-----------------|
         W

  where 'W' indicates the width of the 'width' attribute.
  'P' indicates the width of the 'precision' attribute.
  'X' indicates the width of the 'exponent' attribute.

  The 'E' only exists when there are a positive non-zero 
  number of 'X'. For example, a FloatDataFormat with the
  attributes width=8, precision=5 and exponent=0 would describe
  the following number: "11.00014" 

  @version $Revision$

 */


public class FloatDataFormat extends NumberDataFormat {

   //
   // Fields
   // 
//   private String Template;  

   /* XML attribute names */
   private static final String WIDTH_XML_ATTRIBUTE_NAME = "width";
   private static final String PRECISION_XML_ATTRIBUTE_NAME = "precision";
   private static final String EXPONENT_XML_ATTRIBUTE_NAME = "exponent";
 
   /* default attribute settings */
   public static final int DEFAULT_WIDTH = 0;
   public static final int DEFAULT_PRECISION = 0;
   public static final int DEFAULT_EXPONENT = 0;

   //
   // Constructors
   //

   /** The no argument constructor.
    */
   public FloatDataFormat ()  //DataFormat no-arg constructor should be been called
   {
      init();
   }

   //
   // Get/Set Methods
   //

   /** Set the entire width of this floating point field, including the 'E'
       and its exponential number if they exist (eg the FloatDataFormat 
       exponent attribute is non-zero).
    */
   public void setWidth(Integer numWidth) {

       if (Utility.isValidNumberObject(numWidth))
       {
          ((XMLAttribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).setAttribValue(numWidth);
          generateFormatPattern();
       } else 
         Log.warnln("Invalid value for FloatDataFormat.setWidth(). Ignoring set request.");

   }

   /** The entire width of this floating point field, including the 'E'
       and the exponential number if they exist (eg the FloatDataFormat 
       exponent attribute is non-zero).
       @return the current *width* attribute
    */
   public Integer getWidth()
   {
       return (Integer) ((XMLAttribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the precision of this floating point field from the portion to the  
       right of the '.' and to the left end which may be terminated by an 'E'
       (this happens in the case that a non-zero exponent attribute exists).
    */
   public void setPrecision(Integer precision) {

      if (Utility.isValidNumberObject(precision))
      {
         ((XMLAttribute) attribHash.get(PRECISION_XML_ATTRIBUTE_NAME)).setAttribValue(precision);
         generateFormatPattern();
      } else 
         Log.warnln("Invalid value for FloatDataFormat.setPrecision(). Ignoring set request.");

  }

  /** Get the precision of this floating point field from the portion to the  
      right of the '.' to the end of the number (or just to the left of the 
      'E' in the case that an exponent exists).
      @return the current *precision* attribute
   */
  public Integer getPrecision()
  {
    return (Integer) ((XMLAttribute) attribHash.get(PRECISION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** Set the exponent size of this floating point field. If this is non-zero, then
       the number will be read/written in scientific notation. This attribute declares
       the size of the number which is to the right of the 'E'.
    */
   public void setExponent(Integer size) {

      if (Utility.isValidNumberObject(size))
      {
         ((XMLAttribute) attribHash.get(EXPONENT_XML_ATTRIBUTE_NAME)).setAttribValue(size);
         generateFormatPattern();
      } else 
         Log.warnln("Invalid value for FloatDataFormat.setExponent(). Ignoring set request.");

   }

   /** Get the size of the exponent of this floating point field.
       Non-zero values of this attribute indicate that the number is to be read/written
       in 'E' or scientific notation.
       @return the current *exponent* attribute
    */
   public Integer getExponent()
   {
      return (Integer) ((XMLAttribute) attribHash.get(EXPONENT_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   //
   // Other PUBLIC Methods
   //

   /** A convenience method that return the number of bytes this FloatDataFormat holds.
    */
   public int numOfBytes() {
      return getWidth().intValue();
   }

   // We need this here so that we will properly update the
   // formatPattern of the class. -b.t. 
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
      generateFormatPattern();
   }

  //
  // Private Methods 
  //

  // separate method to minimize the number of times we do this.
  private void generateFormatPattern ( ) {

     StringBuffer leftpattern = new StringBuffer();
     StringBuffer rightpattern = new StringBuffer();
     StringBuffer etemplate = new StringBuffer();

     // precision is the size of the exponent excluding 'E'
     int psize = getPrecision().intValue();
     int esize = getExponent().intValue();
     // the width left of the '.'
     int leftsize = getWidth().intValue() - psize - esize - 1;

     while (leftsize-- > 2)
        etemplate.append("#");

     if (leftsize == 1)
        leftpattern.append("0");

     leftpattern.append(".");

     while (psize-- > 0)
        leftpattern.append("0");

     if(esize > 0)
        rightpattern.append("E");

     while (esize-- > 0)
        rightpattern.append("0");

     // finish building the template
     etemplate.append(leftpattern.toString()+rightpattern.toString());
     etemplate.append(";-"+leftpattern.toString()+rightpattern.toString());
   
     formatPattern = etemplate.toString();

  }

  /** Special protected method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  protected void init() {

     specificDataFormatName = "float";

    //add attributes, order matters 
    /**exponent: The width of any exponent (indicates scientific notation 
       is used)
     */
    attribOrder.add(0, EXPONENT_XML_ATTRIBUTE_NAME);

    /**precision:The precision of this float field which is the number of digits
     * to the right of the '.' and left of the 'E', if it exists.
     */
    attribOrder.add(0, PRECISION_XML_ATTRIBUTE_NAME);

    /**width: The entire width of this float field
     */
    attribOrder.add(0, WIDTH_XML_ATTRIBUTE_NAME);


    attribHash.put(WIDTH_XML_ATTRIBUTE_NAME, new XMLAttribute( new Integer(DEFAULT_WIDTH), Constants.INTEGER_TYPE));
    attribHash.put(PRECISION_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_PRECISION), Constants.INTEGER_TYPE));
    attribHash.put(EXPONENT_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_EXPONENT), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.2  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.1  2001/01/29 19:32:15  thomas
 * Class created from FixedDataFormat and ExponentialDataFormat. -b.t.
 *
 *
 */
