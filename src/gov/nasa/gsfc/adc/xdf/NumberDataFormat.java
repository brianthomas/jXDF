
// XDF NumberDataFormat Class
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

// NumberDataFormat.java Copyright (C) 2000 Brian Thomas,
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
 *  Provides methods for number-based data formats.
 *  @version $Revision$
 */


public abstract class NumberDataFormat extends DataFormat {

   //
   // ABSTRACT methods
   //

   public abstract int numOfBytes(); //return the number of bytes


   //
   //Set Methods
   //

   /** set the *lessThanValue* attribute
    */
/*
   public void setLessThanValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
        Log.warnln("Could'nt set the lessThanValue as it is not a valid number object. Ignoring request.");

   }

   /** set the *lessThanValueOrEqualValue* attribute
    */
/*
   public void setLessThanOrEqualValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
         Log.warnln("Could'nt set the lessThanOrEqualValue as it is not a valid number object. Ignoring request.");

   }

   /** set the *greaterThanValue* attribute
    */
/*
   public void setGreaterThanValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
         Log.warnln("Could'nt set the greaterThanValue as it is not a valid number object. Ignoring request.");

   }

   /** set the *greaterThanOrEqualValue* attribute
    */
/*
   public void setGreaterThanOrEqualValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
         Log.warnln("Could'nt set the greaterThanOrEqualValue as it is not a valid number object. Ignoring request.");
   }

   /** set the *infiniteValue* attribute
    */
/*
   public void setInfiniteValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
         Log.warnln("Could'nt set the infiniteValue as it is not a valid number object. Ignoring request.");

   }

   /** set the *infiniteNegativeValue* attribute
    */
/*
   public void setInfiniteNegativeValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else 
         Log.warnln("Could'nt set the infiniteNegativeValue as it is not a valid number object. Ignoring request.");

   }

   /** set the *noDataValue* attribute
    */
/*
   public void setNoDataValue(Object number) {

      if (Utility.isValidNumberObject(number))
         ((Attribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
      else
         Log.warnln("Could'nt set the noDataValue as it is not a valid number object. Ignoring request.");

   }
 */

}

/* Modification History:
 *
 * $Log$
 * Revision 1.3  2001/09/13 21:39:25  thomas
 * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
 *
 * Revision 1.2  2001/04/27 21:30:22  thomas
 * accomodating DTD, no longer have set/get methods for
 * lessthan, greaterThan, etc in dataformat classes.
 *
 * Revision 1.1  2001/02/07 18:52:52  thomas
 * Inital Version. Concat's set methods that
 * the number-based (ascii) dataformats need into
 * one class. -b.t.
 *
 *
 */

