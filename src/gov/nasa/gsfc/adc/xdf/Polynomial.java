

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
   An XDF::Polynomial is a class that describes a type of algorithm. 
   It is used in other XDF objects to describe/generate numerical values.
   @version $Revision$
 */


public class Polynomial extends BaseObject implements AlgorithmInterface {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String SIZE_XML_ATTRIBUTE_NAME = new String("size");
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private static final String LOG_XML_ATTRIBUTE_NAME = new String("logarithm");
   private static final String REVERSE_XML_ATTRIBUTE_NAME = new String("reverse");

   private List values;

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
   public Integer getSize() {
      return (Integer) ((Attribute) attribHash.get(SIZE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *size* attribute.
    */
   public void setSize (int size)
   {
      setSize(new Integer(size));
   }

   /** set the *size* attribute.
    */
   public void setSize (Integer size)
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
       Allows the user to retrieve the coefficents of this polynomial as a List
       of Double objects. Order of coefficents is lowest order first, to 
       highest order last.
    */
   public List getCoefficients() {
      String strCoeff = (String) ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
      String[] coeffs = strCoeff.split("\\s+");
      List coefficents = new Vector();

      //String reverse = getReverse(); 
      //boolean isReversed = (reverse != null) && reverse.equals("true") ? true : false;
      for(int i = 0; i < coeffs.length; i++) {
         try {
           Double number = new Double(coeffs[i]);
           coefficents.add(number);
         } catch (java.lang.NumberFormatException e) {
           Log.errorln("ERROR: bad polynomial coefficent data, cant parse string:"+coeffs[i]+" into double, ignoring");
         }
      }
      return coefficents;
   }

   /** Set the *coefficients* attribute.
       Allows the user to set the coefficents by passing a List of
       Double objects. Order of coefficents is lowest order first, to 
       highest order last.

    */
   public void setCoefficients (List coefficientList)
   {

       Iterator iter = coefficientList.iterator();
       StringBuffer buf = new StringBuffer();
       while (iter.hasNext()) {
          Object value = (Object) iter.next();
          try {
             Double dvalue = (Double) value;
             buf.append(dvalue.toString());
             buf.append(" ");
          } catch (ClassCastException e) {
             Log.errorln("ERROR: bad polynomial coefficent data, cant parse string:"+value.toString()+" into double, ignoring");
          }
       }

       setCoeffPCDATA(buf.toString());
//      ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }

   public List getValues() {
      return values;
   }

   //
   // Other PUBLIC Methods
   //

   /** Allows the user to set the coefficents by passing a string of
       numbers delimited by a space. Order of coefficents is lowest order first, to 
       highest order last.
    */
   public void setCoeffPCDATA (String value)
   {
      ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
 
      // now re-init our list of values
      initValuesFromParams();
   }

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
     attribHash.put(SIZE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.INTEGER_TYPE));
     attribHash.put(REVERSE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(LOG_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

     // set size BEFORE PCDATA
     setSize(new Integer(1));
     setCoeffPCDATA("0");

   }

   // 
   // Private Methods
   //

   // Hmm. is it REALLY needed that these be value objects now??
   // I doubt it...need to double check. Lots of memory being wasted
   // on storing value objects
   // ugh. This method is ugly and slow.
   private Value getValueAtStep (int step, double[] coeffs, String logarithm)
   {
      List coefficients = getCoefficients();

      double value = 0.0;
      for(int i=0; i < coeffs.length; i++) {
        value += coeffs[i] * Math.pow(step,i);
      }

      if(logarithm != null) 
         if(logarithm.equals("natural"))
           value = Math.log(value); // take the natural log 
         else 
           value = (Math.log(value)/Math.log(10)); // base 10 log 

      return new Value(value);
   }

   private void initValuesFromParams()
   {

      values = (List) new Vector(); 
      List coefficients = getCoefficients();
      int nrofCoeffs = coefficients.size();

      // safety
      if(nrofCoeffs < 1) 
      {
         return;
      }

      // initialize primative array of coefficents for faster calculation
      double[] coeffs = new double [nrofCoeffs];
      for( int i=0 ; i <nrofCoeffs; i++) {
        coeffs[i] = ((Double) coefficients.get(i)).doubleValue();
      }

      // now populate values list
      String reverse = getReverse();
      boolean isReversed = (reverse != null) && reverse.equals("true") ? true : false;
      int size = getSize().intValue();
      String logarithm = getLogarithm();

      for(int i = 0; i < size; i++) {
 
         Value thisValue = getValueAtStep(i, coeffs, logarithm);
         if(isReversed)
              values.add(0,thisValue);
         else
              values.add(thisValue);
      }

   }

}

