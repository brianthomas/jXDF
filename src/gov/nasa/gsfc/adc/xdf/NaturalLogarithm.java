

// XDF NaturalLogarithm Class
// CVS $Id$

// NaturalLogarithm.java Copyright (C) 2003 Brian Thomas,
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
   An XDF::NaturalLogarithm is a class that defines an multiplication component for an 
   XDF::ConversionComponent object.
   @version $Revision$
 */


public class NaturalLogarithm extends BaseObject implements ConversionComponentInterface {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");

   //
   // Constructors
   //

   /** The no argument constructor.
    */
   public NaturalLogarithm ()  //DataFormat no-arg constructor should be been called
   {
      init();
   }

   //
   // Set Methods
   //

   //
   // Other PUBLIC Methods
   //

   /** Evaluate a value using this conversion object. 
       @return the converted value.
    */
   public double evaluate (double value) {
       return Math.log(value);
   }

   /** Evaluate a value using this conversion object. 
       @return the converted value.
    */
   public Double evaluate (Double value) {
       // yech. Isnt there a better way to do this??
       return new Double(this.evaluate(value.doubleValue()));
   }


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

     classXDFNodeName = "naturalLogarithm";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial value
     attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.DOUBLE_TYPE));


   }

}

