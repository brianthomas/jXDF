

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

import org.xml.sax.AttributeList;

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

      ((XMLAttribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(strType);
   }

   /**
       @return the current *type* attribute
    */
   public String getType()
   {
      return (String) ((XMLAttribute) attribHash.get(TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *width* attribute
    */
   public void setWidth(Integer width) {

      if (Utility.isValidNumberObject(width)) 
      {
         ((XMLAttribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).setAttribValue(width);
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
      return (Integer) ((XMLAttribute) attribHash.get(WIDTH_XML_ATTRIBUTE_NAME)).getAttribValue();
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
   public void setXMLAttributes (AttributeList attrs) {
      super.setXMLAttributes(attrs);
      generateFormatPattern();
   }

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

    attribHash.put( TYPE_XML_ATTRIBUTE_NAME, new XMLAttribute(DEFAULT_TYPE, Constants.STRING_TYPE));
    attribHash.put( WIDTH_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_WIDTH), Constants.INTEGER_TYPE));

    generateFormatPattern();

  }

}

/* Modification History:
 *
 * $Log$
 * Revision 1.11  2001/06/19 19:29:22  thomas
 * *** empty log message ***
 *
 * Revision 1.10  2001/05/04 20:20:09  thomas
 * added super.init() in init() method. This may have to be undone
 * in the future.  Consider this to be a 'side-ways' change.
 *
 * Revision 1.9  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
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
 * Revision 1.6  2000/11/16 20:01:07  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.4  2000/11/08 19:55:07  thomas
 * Trimmed import path to just needed classes (none!) -b.t.
 *
 * Revision 1.3  2000/10/27 21:20:06  kelly
 * get rid of classXDFNodeName, added specificDataFormatName to suit its
 * supper class DataFormat *toXDF*.  -k.z.
 *
 * Revision 1.2  2000/10/26 20:18:52  kelly
 * get methods are now in superclass DataFormat, implement set methods itself -k.z.
 *
 * Revision 1.1  2000/10/16 14:53:26  kelly
 * created and pretty much completed the class. --k.z.
 *
 */
