

// XDF Value
// CVS $Id$

// Value.java Copyright (C) 2000 Brian Thomas,
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

/** Holds 'values' (either mathematical or string). ErroredValue inherits
    from this object (but unlike this class only holds math values); 
    This object is used at every indice on an Axis object to denote the 
    coordinate value of a given index. The Value class can hold a scalar value.
    To hold a vector (unit direction) value use UnitDirection class instead.
    @version $Revision$
 */

public class Value extends BaseObject {

   // 
   // Fields
   //

   /* XML attribute names */
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private static final String INEQUALITY_XML_ATTRIBUTE_NAME = new String("inequality");
   private static final String SPECIAL_XML_ATTRIBUTE_NAME = new String("special");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("valueId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("valueIdRef");

   //
   // Constructors
   //

   /** Passed string sets the *value* attribute (PCDATA)
       of this object.
    */
   public Value (String strValue) {
      init();
      setValue(strValue); // set value attribute from passed argument
   }

   /** Passed integer sets the *value* attribute (PCDATA)
       of this object.
    */
   public Value (Integer intValue) {
      this(intValue.toString());
   }

   /** Passed integer sets the *value* attribute (PCDATA)
       of this object.
    */
   public Value (int intValue) {
      this(Integer.toString(intValue));
   }

   /** Passed float sets the *value* attribute (PCDATA)
       of this object.
    */
   public Value (Double doubleValue) {
      this(doubleValue.toString());
   }

   /** Passed integer sets the *value* attribute (PCDATA)
       of this object.
    */
   public Value (double doubleValue) {
      this(Double.toString(doubleValue));
   }

   //
   //no-arg constructor
   //
    public Value () {
      init();
   }
   //
   // Public Methods
   //

   /** get the *value* (PCDATA) attribute.
   */
   public String getValue() {
      return (String) ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *value* attribute.
    */
   public void setValue (String strValue)
   {
      ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

  /** get the *valueId* attribute.
   */
   public String getValueId() {
      return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *valueId* attribute.
    */
   public void setValueId (String strValueId)
   {
      ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strValueId);
   }

   /** get the *valueIdRef* attribute.
   */
   public String getValueIdRef() {
      return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *valueRef* attribute.
   */
   public void setValueIdRef (String strValueRef)
   {
      ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strValueRef);
   }

    /** get the *inequality*   attribute.
    */
   public String getInequality() {
      return (String) ((Attribute) attribHash.get(INEQUALITY_XML_ATTRIBUTE_NAME)).getAttribValue();
   }


   /** set the *inequality* attribute.
    */
   public void setInequality (String strInequality)
   {
      if (Utility.isValidValueInequality(strInequality))
        ((Attribute) attribHash.get(INEQUALITY_XML_ATTRIBUTE_NAME)).setAttribValue(strInequality);
   }

  /** get the *special* attribute.
   */
   public String getSpecial() {
      return (String) ((Attribute) attribHash.get(SPECIAL_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *special* attribute.
    */
   public void setSpecial (String strSpecial)
   {
      if (Utility.isValidValueSpecial(strSpecial))
         ((Attribute) attribHash.get(SPECIAL_XML_ATTRIBUTE_NAME)).setAttribValue(strSpecial);
      else 
         Log.warnln("Warning: can't set special attribute in value object to:"+strSpecial);
   }


   //
   // Protected Methods
   //

   /** A special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

       resetAttributes();

       classXDFNodeName = "value";

       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
       attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, INEQUALITY_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, SPECIAL_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);

       //set up the attribute hashtable key with the default initial value
       attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(INEQUALITY_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(SPECIAL_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
   }


}

