
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

/*
     private static final String LESSTHANVALUE_XML_ATTRIBUTE_NAME = new String("lessThanValue");
     private static final String LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("lessThanOrEqualValue");
     private static final String GREATERTHANVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanValue");
     private static final String GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanOrEqualValue");
     private static final String NODATAVALUE_XML_ATTRIBUTE_NAME = new String("noDataValue");
*/
     private static final String INFINITEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteValue");
     private static final String INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteNegativeValue");
     private static final String NOTANUMBERVALUE_XML_ATTRIBUTE_NAME = new String("notANumberValue");
     private static final String OVERFLOWVALUE_XML_ATTRIBUTE_NAME = new String("overflowValue");
     private static final String UNDERFLOWVALUE_XML_ATTRIBUTE_NAME = new String("underflowValue");
     private static final String DISABLEDVALUE_XML_ATTRIBUTE_NAME = new String("disabledValue");

     //
     // ABSTRACT methods
     //

     public abstract int numOfBytes(); //return the number of bytes


     //
     // Get/Set Methods
     //

     /** set the *infiniteValue* attribute
      */
     public void setInfiniteValue (Number strDesc)
     {
        ((Attribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }

     /*
      * @return the current *infiniteValue* attribute
      */
     public Object getInfiniteValue() {
       return ((Attribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }

     /** set the *infiniteNegativeValue* attribute
      */
     public void setInfiniteNegativeValue (Number strDesc)
     {
        ((Attribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }

     /*
      * @return the current *infiniteNegativeValue* attribute
      */
     public Object getInfiniteNegativeValue() {
       return ((Attribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }

     /** set the *noDataValue* attribute
      */
     public void setNoDataValue(Object number) {
  
        if (Utility.isValidNumberObject(number))
           ((Attribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(number);
        else
           Log.warnln("Could'nt set the noDataValue as it is not a valid number object. Ignoring request.");
  
     }

     /** set the *notANumberValue* attribute
      */
     public void setNotANumberValue (Object strDesc)
     {
        ((Attribute) attribHash.get(NOTANUMBERVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }

     /*
      * @return the current *notANumberValue* attribute
      */
     public Object getNotANumberValue() {
        return ((Attribute) attribHash.get(NOTANUMBERVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
  
     /** set the *overflowValue* attribute
      */
     public void setOverflowValue (Object strDesc)
     {
        ((Attribute) attribHash.get(OVERFLOWVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
  
     /*
      * @return the current *overflowValue* attribute
      */
     public Object getOverflowValue() {
        return ((Attribute) attribHash.get(OVERFLOWVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
  
     /** set the *underflowValue* attribute
      */
     public void setUnderflowValue (Object strDesc)
     {
        ((Attribute) attribHash.get(UNDERFLOWVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
  
     /*
      * @return the current *underflowValue* attribute
      */
     public Object getUnderflowValue() {
        return ((Attribute) attribHash.get(UNDERFLOWVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
  
     /** set the *disabledValue* attribute
      */
     public void setDisabledValue (Object strDesc)
     {
        ((Attribute) attribHash.get(DISABLEDVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
  
     /*
      * @return the current *disabledValue* attribute
      */
     public Object getDisabledValue() {
        return ((Attribute) attribHash.get(DISABLEDVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }


}

