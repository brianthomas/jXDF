// XDF ExponentialDataFormat Class
// CVS $Id$

// ExponentialDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 *  describes exponential (ASCII) floating point numbers
 * (e.g. scientific notation, IE10).
 * @version $Revision$
 */


public class ExponentialDataFormat extends DataFormat {

  //
  //Fields
  //

  /** The no argument constructor.
   */
  public ExponentialDataFormat ()  //DataFormat no-arg constructor should be been called
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

  /** Set the entire width of this exponential field, including the 'E'
      and its exponential number.
   */
  public void setWidth(Integer numWidth) {
     ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);
     generateFormatPattern();
  }

  /** The entire width of this exponential field, including the 'E'
      and its exponential number.
      @return the current *width* attribute
   */
  public Integer getWidth()
  {
    return (Integer) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /** Set the precision of this exponential field from the portion to the  
      right of the '.' and to the left of the 'E'.
   */
  public void setPrecision(Integer precision) {
     ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);
     generateFormatPattern();
  }

  /** Get the precision of this exponential field from the portion to the  
      right of the '.' to the left of the 'E'.
      @return the current *precision* attribute
   */
  public Integer getPrecision()
  {
    return (Integer) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  /** Set the exponent size of this exponential field. In other words the size of
      the number which is to the right of the 'E'.
   */
  public void setExponent(Integer size) {
     ((XMLAttribute) attribHash.get("exponent")).setAttribValue(size);
     generateFormatPattern();
  }

  /** Get the size of the exponent of this exponential field.
      @return the current *exponent* attribute
   */
  public Integer getExponent()
  {
    return (Integer) ((XMLAttribute) attribHash.get("exponent")).getAttribValue();
  }


  //
  //Other PUBLIC Methods
  //

  // We need this here so that we will properly update the
  // templateNotation of the class. -b.t. 
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
      generateFormatPattern();
   }

  /**  A convenience method.
   * @return the number of bytes this ExponentialDataFormat holds.
   */
  public int numOfBytes() {
    return getWidth().intValue();
  }

  // separate method to minimize the number of times we do this.
  private void generateFormatPattern ( ) {

     StringBuffer leftpattern = new StringBuffer();
     StringBuffer rightpattern = new StringBuffer();
     StringBuffer etemplate = new StringBuffer();

/*
     // Old method
     // precision is the size of the exponent excluding 'E'
     int psize = getPrecision().intValue();
     // width including 'E' and exponent
     int wsize = getWidth().intValue() - psize - 1;

     if(wsize > 2) 
        etemplate.append("#");

     leftpattern.append("0.");

     while (wsize-- > 3)
        leftpattern.append("0");

     rightpattern.append("E0");
     while (psize-- > 1)
        rightpattern.append("0");
*/

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

     rightpattern.append("E");

     while (esize-- > 0)
        rightpattern.append("0");

     // finish building the template
     etemplate.append(leftpattern.toString()+rightpattern.toString());
     etemplate.append(";-"+leftpattern.toString()+rightpattern.toString());
   
     formatPattern = etemplate.toString();

  }

  //
  // Private Methods
  //
  /** Special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init() {

    specificDataFormatName = "exponential";

    //add attributes
    attribOrder.add(0,"exponent");
    attribOrder.add(0,"precision");
    attribOrder.add(0, "width");

    attribHash.put("exponent", new XMLAttribute( new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("precision", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("width", new XMLAttribute( new Integer(0), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.11  2001/01/22 22:09:53  thomas
 * Node name was wrong(!) fixed to "exponential" as DTD likes it. -b.t.
 *
 * Revision 1.10  2001/01/17 18:29:19  thomas
 * Brought class up to 0.17 standard of width,
 * precision AND exponent attributes. -b.t.
 *
 * Revision 1.9  2000/11/22 22:05:21  thomas
 * Oops. forgot to remove debugging statement. done. =-b.t.
 *
 * Revision 1.8  2000/11/22 20:42:00  thomas
 * beaucoup changes to make formatted reads work.
 * DataFormat methods now store the "template" or
 * formatPattern that will be needed to print them
 * back out. Removed sprintfNotation, Perl regex and
 * Perl attributes from DataFormat classes. -b.t.
 *
 * Revision 1.7  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.6  2000/11/16 19:58:12  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:43:49  thomas
 * Re-arranged header info to standard format for package. -b.t.
 *
 * Revision 1.3  2000/10/27 21:15:47  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:34:13  kelly
 * fixed the class name, etc. -k.z.
 *
 * Revision 1.1  2000/10/26 20:23:10  kelly
 * was ExponentDataFormat.java before. renamed it.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:19:55  kelly
 *
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.1  2000/10/16 14:54:16  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
