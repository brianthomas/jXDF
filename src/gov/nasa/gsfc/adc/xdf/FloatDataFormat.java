

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


public class FloatDataFormat extends DataFormat {

   //
   // Fields
   // 
   private String Template;  

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


  /** Set the entire width of this floating point field, including the 'E'
      and its exponential number if they exist (eg the FloatDataFormat 
      exponent attribute is non-zero).
   */
  public void setWidth(Integer numWidth) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);
     generateFormatPattern();
  }

  /** The entire width of this floating point field, including the 'E'
      and the exponential number if they exist (eg the FloatDataFormat 
      exponent attribute is non-zero).
      @return the current *width* attribute
   */
  public Integer getWidth()
  {
    return (Integer) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /** Set the precision of this floating point field from the portion to the  
      right of the '.' and to the left end which may be terminated by an 'E'
      (this happens in the case that a non-zero exponent attribute exists).
   */
  public void setPrecision(Integer precision) {
     ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);
     generateFormatPattern();
  }

  /** Get the precision of this floating point field from the portion to the  
      right of the '.' to the end of the number (or just to the left of the 
      'E' in the case that an exponent exists).
      @return the current *precision* attribute
   */
  public Integer getPrecision()
  {
    return (Integer) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  /** Set the exponent size of this floating point field. If this is non-zero, then
      the number will be read/written in scientific notation. This attribute declares
      the size of the number which is to the right of the 'E'.
   */
  public void setExponent(Integer size) {
     ((XMLAttribute) attribHash.get("exponent")).setAttribValue(size);
     generateFormatPattern();
  }

  /** Get the size of the exponent of this floating point field.
      Non-zero values of this attribute indicate that the number is to be read/written
      in 'E' or scientific notation.
      @return the current *exponent* attribute
   */
  public Integer getExponent()
  {
    return (Integer) ((XMLAttribute) attribHash.get("exponent")).getAttribValue();
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
   // templateNotation of the class. -b.t. 
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

  /** Special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {
     specificDataFormatName = "float";
    //add attributes

    /**precision:The precision of this float field which is the number of digits
     * to the right of the '.' and left of the 'E', if it exists.
     */
    attribOrder.add(0,"precision");

    /**width: The entire width of this float field
     */
    attribOrder.add(0, "width");

    /**exponent: The width of any exponent (indicates scientific notation 
       is used)
     */
    attribOrder.add(0, "exponent");

    attribHash.put("width", new XMLAttribute( new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("precision", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("exponent", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.1  2001/01/29 19:32:15  thomas
 * Class created from FixedDataFormat and ExponentialDataFormat. -b.t.
 *
 *
 */
