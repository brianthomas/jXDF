

// XDF FixedDataFormat Class
// CVS $Id$

// FixedDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 *  describes (ASCII) fixed (floating point) numbers.
 * @version $Revision$
 */


public class FixedDataFormat extends DataFormat {

   //
   // Fields
   // 
   private String Template;  

  //
  // Constructors
  //
  /** The no argument constructor.
   */
  public FixedDataFormat ()  //DataFormat no-arg constructor should be been called
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

  /** set the *width* attribute
   */
  public void setWidth (Integer numWidth) {
     if(numWidth.intValue() >= 0) { 
       ((XMLAttribute) attribHash.get("width")).setAttribValue(numWidth);
       generateFormatPattern();
     } else 
       Log.warnln("FixedDataFormat.setWidth() cannot take value less than 0. Ignoring request.");
  }

  /**
   * @return the current *width* attribute
   */
  public Integer getWidth()
  {
    return (Integer) ((XMLAttribute) attribHash.get("width")).getAttribValue();
  }

  /** set the *precision* attribute
   */
  public void setPrecision (Integer precision) {

     if(precision.intValue() >= 0) { 
       ((XMLAttribute) attribHash.get("precision")).setAttribValue(precision);
       generateFormatPattern();
     } else
       Log.warnln("FixedDataFormat.setPrecision() cannot take value less than 0. Ignoring request.");
 
  }

  /**
   * @return the current *precision* attribute
   */
  public Integer getPrecision()
  {
    return (Integer) ((XMLAttribute) attribHash.get("precision")).getAttribValue();
  }

  //
  // Other PUBLIC Methods
  //

  /** A convenience method that return the number of bytes this FixedDataFormat holds.
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

     int psize = getPrecision().intValue();
     int wsize = getWidth().intValue() - psize - 1;

     if (wsize > 1)
        etemplate.append("#");

     while (wsize-- > 2)
        leftpattern.append("#");
     leftpattern.append("0.");

     while (psize-- > 0)
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
     specificDataFormatName = "fixed";
    //add attributes
    /**precision:The precision of this fixed field which is the number of digits
     * to the right of the '.'.
     */
    attribOrder.add(0,"precision");

    /**width: The entire width of this fixed field
     */
    attribOrder.add(0, "width");

    attribHash.put("width", new XMLAttribute( new Integer(0), Constants.INTEGER_TYPE));
    attribHash.put("precision", new XMLAttribute(new Integer(0), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }


}

/* Modification History:
 *
 * $Log$
 * Revision 1.9  2000/11/27 17:14:04  thomas
 * added bounds checking on width and precision. -b.t.
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
 * Revision 1.6  2000/11/16 19:59:51  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:48:50  thomas
 * Rearranged header to follow package standard. -b.t.
 *
 * Revision 1.3  2000/10/27 21:19:30  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.1  2000/10/16 14:55:11  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
