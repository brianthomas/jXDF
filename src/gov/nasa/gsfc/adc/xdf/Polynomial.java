

// XDF Polynomial Class
// CVS $Id$

// Polynomial.java Copyright (C) 2003 Brian Thomas,
// XML Group/GSFC-NASA, Code 630.1, Greenbelt MD, 20771

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

/**
   An XDF::Polynomial is a class that describes a type of algorithm. 
   It is used in other XDF objects to describe/generate numerical values.
   @version $Revision$
 */


public class Polynomial extends BaseObject {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String SIZE_XML_ATTRIBUTE_NAME = new String("size");
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private static final String LOG_XML_ATTRIBUTE_NAME = new String("logarithm");
   private static final String REVERSE_XML_ATTRIBUTE_NAME = new String("reverse");

   //
   // Constructors
   //

   /** The no argument constructor.
    */
   public Polynomial ()  //DataFormat no-arg constructor should be been called
   {
      init();
   }

   //
   // Set Methods
   //

   /** get the *size* attribute.
    */
   public String getSize() {
      return (String) ((Attribute) attribHash.get(SIZE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *size* attribute.
    */
   public void setSize (String size)
   {
      ((Attribute) attribHash.get(SIZE_XML_ATTRIBUTE_NAME)).setAttribValue(size);
   }

   /** get the *logarithm* attribute.
    */
   public String getLogarithm() {
      return (String) ((Attribute) attribHash.get(LOG_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *logarithm* attribute.
    */
   public void setLogarithm (String log)
   {
      ((Attribute) attribHash.get(LOG_XML_ATTRIBUTE_NAME)).setAttribValue(log);
   }

   /** get the *reverse* attribute.
    */
   public String getReverse() {
      return (String) ((Attribute) attribHash.get(REVERSE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *reverse* attribute.
    */
   public void setReverse (String value)
   {
      ((Attribute) attribHash.get(REVERSE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }

   /** Get the *coefficients* attribute.
       This is a space delimited string.
    */
   public String getCoefficients() {
      return (String) ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** Set the *coefficients* attribute.
       This is a space delimited string.
    */
   public void setCoefficients (String value)
   {
      ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }


   //
   // Other PUBLIC Methods
   //

   //
   // Private Methods 
   //

   //
   // Protected Methods 
   //

   /** Special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init() {

     super.init();

     classXDFNodeName = "polynomial";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD

     attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, REVERSE_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, LOG_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, SIZE_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial value
     attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(SIZE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(REVERSE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(LOG_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

     setCoefficients("0");
     setSize("1");

   }

}

