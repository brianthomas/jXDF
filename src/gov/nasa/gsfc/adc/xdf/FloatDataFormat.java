

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
  
import org.xml.sax.Attributes;


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

   /* XML attribute names */
   private static final String WIDTH_XML_ATTRIBUTE_NAME = "width";
   private static final String PRECISION_XML_ATTRIBUTE_NAME = "precision";
   private static final String EXPONENT_XML_ATTRIBUTE_NAME = "exponent";
 
   /* default attribute settings */
   public static final int DEFAULT_WIDTH = 0;
   public static final int DEFAULT_PRECISION = 0;
   public static final int DEFAULT_EXPONENT = 0;

   // next 3 are used to determine isPrimativeFloat 
   protected static final int MAX_FLOAT_WIDTH = 9;
   protected static final int MAX_FLOAT_EXPONENT = 37;
   protected static final int MIN_FLOAT_EXPONENT = -44;

   private String negativeExponentFormatPattern;

   private boolean isPrimativeFloat = false; // does this dataformat object really 
                                             // require a primative double (by the Java Standard) 
                                             // to store the values without loss?


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
          ((Attribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).setAttribValue(numWidth);
          generateFormatPattern();
          determineIfIsPrimativeFloat();
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
       return (Integer) ((Attribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the precision of this floating point field from the portion to the  
       right of the '.' and to the left end which may be terminated by an 'E'
       (this happens in the case that a non-zero exponent attribute exists).
    */
   public void setPrecision(Integer precision) {

      if (Utility.isValidNumberObject(precision))
      {
          ((Attribute) attribHash.get(PRECISION_XML_ATTRIBUTE_NAME)).setAttribValue(precision);
          if (precision.intValue() > getWidth().intValue()) {
             int current_width = getWidth().intValue(); 
             int new_width = precision.intValue()+1;
             Log.infoln("Declared precision of "+precision+"is larger than current format width of "+current_width+", bumping up width to"+new_width);
             setWidth(new Integer(new_width));
          }
          generateFormatPattern();
          // determineIfIsPrimativeFloat();
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
    return (Integer) ((Attribute) attribHash.get(PRECISION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** Set the exponent size of this floating point field. If this is non-zero, then
       the number will be read/written in scientific notation. This attribute declares
       the size of the number which is to the right of the 'E'.
    */
   public void setExponent(Integer size) {

      if (Utility.isValidNumberObject(size))
      {
         ((Attribute) attribHash.get(EXPONENT_XML_ATTRIBUTE_NAME)).setAttribValue(size);
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
      Integer exponent = (Integer) ((Attribute) attribHash.get(EXPONENT_XML_ATTRIBUTE_NAME)).getAttribValue();
      if (exponent == null) 
         exponent = new Integer(DEFAULT_EXPONENT);
      return exponent;
   }

   /** Get the DecimalFormat pattern for nice output of negative float numbers  
       with exponents less than 0. This is needed because of the failings in the
       java DecimalFormat class which doenst allow grouping pattern separators when
       scientific format ('E') is used. 
    */
   public String getNegativeExponentFormatPattern() {
      return negativeExponentFormatPattern;
   }

   //
   // Other PUBLIC Methods
   //

   /** Indicates whether this DataFormat describes a primative float (if true). If the value
       is false then a primative double should be used to store these numbers. 
    */
   public boolean isPrimativeFloat() 
   { 
      return isPrimativeFloat;
   } 

   /** A convenience method that return the number of bytes this FloatDataFormat holds.
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

  // separate method to minimize the number of times we do this.
  private void generateFormatPattern ( ) {

     StringBuffer leftpattern = new StringBuffer();
     StringBuffer negleftpattern = new StringBuffer();
     StringBuffer rightpattern = new StringBuffer();
     StringBuffer negrightpattern = new StringBuffer();
     StringBuffer etemplate = new StringBuffer();
     StringBuffer negtemplate = new StringBuffer();

     // precision is the size of the exponent excluding 'E'
     int psize = getPrecision().intValue();
     int esize = getExponent().intValue();
     // the width left of the '.'
     int leftsize = getWidth().intValue() - psize - esize - 1;

     // padding spaces? not needed I think..
     // while (leftsize-- > 2)
     //   etemplate.append("#");

     if (leftsize == 1)
        leftpattern.append("0");

     leftpattern.append(".");
     negleftpattern.append(".");

     while (psize-- > 0) { 
        leftpattern.append("0");
        negleftpattern.append("0");
     }

     if(esize > 0) { 
        rightpattern.append("E");
        rightpattern.append("0");
        negrightpattern.append("E");
     }

     while (esize-- > 1)
     {
        rightpattern.append("0");
        negrightpattern.append("0");
     }

     // finish building the template
     etemplate.append(leftpattern.toString()+rightpattern.toString());
     etemplate.append(";-"+leftpattern.toString()+rightpattern.toString());
   
     negtemplate.append(negleftpattern.toString()+negrightpattern.toString());

     formatPattern = etemplate.toString();

     if (getExponent().intValue() > 0) {
        negativeExponentFormatPattern = negtemplate.toString();
     } else {
        negativeExponentFormatPattern = null;
     }

  }

  // determine whether or not this value will fit into a primative float
  // or if it needs to be in a primative double instead
  private void determineIfIsPrimativeFloat() {

     if (getWidth().intValue() > MAX_FLOAT_WIDTH
         || getExponent().intValue() > MAX_FLOAT_EXPONENT 
         || getExponent().intValue() < MIN_FLOAT_EXPONENT 
//         || getPrecision().intValue() > maxFloatPrecision 
        ) {
       isPrimativeFloat = false;
     } else {
       isPrimativeFloat = true;
     } 
  }
 
  // 
  // Protected Methods
  // 

  /** Special protected method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  protected void init() {

    super.init();

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


    attribHash.put(WIDTH_XML_ATTRIBUTE_NAME, new Attribute( new Integer(DEFAULT_WIDTH), Constants.INTEGER_TYPE));
    attribHash.put(PRECISION_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_PRECISION), Constants.INTEGER_TYPE));
    // attribHash.put(EXPONENT_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_EXPONENT), Constants.INTEGER_TYPE));
    attribHash.put(EXPONENT_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.INTEGER_TYPE));

    generateFormatPattern();

  }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/09/18 20:32:20  thomas
 * added back in setXMLAttribute(s) convience method, w/ deprecated statement
 *
 * Revision 1.8  2001/09/18 17:43:32  thomas
 * small change to prevent exponent attrib from writing out if undefined
 *
 * Revision 1.7  2001/09/13 22:18:37  thomas
 * added isPrimiativeFloat() method and some checking on width when precision is set
 *
 * Revision 1.6  2001/09/13 21:39:25  thomas
 * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
 *
 * Revision 1.5  2001/07/17 19:06:23  thomas
 * upgrade to use JAXP (SAX2) only. Namespaces NOT
 * implemented (yet).
 *
 * Revision 1.4  2001/06/25 15:14:34  thomas
 * added negativeExponentPattern for N <0 && N > -1
 * and an exponent specified.
 *
 * Revision 1.3  2001/05/04 20:20:09  thomas
 * added super.init() in init() method. This may have to be undone
 * in the future.  Consider this to be a 'side-ways' change.
 *
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
