

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
      return (String) ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *value* attribute.
    */
   public void setValue (String strValue)
   {
      ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strValue);
   }

  /** get the *valueId* attribute.
   */
   public String getValueId() {
      return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *valueId* attribute.
    */
   public void setValueId (String strValueId)
   {
      ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strValueId);
   }

   /** get the *valueIdRef* attribute.
   */
   public String getValueIdRef() {
      return (String) ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *valueRef* attribute.
   */
   public void setValueIdRef (String strValueRef)
   {
      ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strValueRef);
   }

    /** get the *inequality*   attribute.
    */
   public String getInequality() {
      return (String) ((XMLAttribute) attribHash.get(INEQUALITY_XML_ATTRIBUTE_NAME)).getAttribValue();
   }


   /** set the *inequality* attribute.
    */
   public void setInequality (String strInequality)
   {
      if (Utility.isValidValueInequality(strInequality))
        ((XMLAttribute) attribHash.get(INEQUALITY_XML_ATTRIBUTE_NAME)).setAttribValue(strInequality);
   }

  /** get the *special* attribute.
   */
   public String getSpecial() {
      return (String) ((XMLAttribute) attribHash.get(SPECIAL_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *special* attribute.
    */
   public void setSpecial (String strSpecial)
   {
      if (Utility.isValidValueSpecial(strSpecial))
         ((XMLAttribute) attribHash.get(SPECIAL_XML_ATTRIBUTE_NAME)).setAttribValue(strSpecial);
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

       resetXMLAttributes();

       classXDFNodeName = "value";

       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
       attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, INEQUALITY_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, SPECIAL_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);

       //set up the attribute hashtable key with the default initial value
       attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(INEQUALITY_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(SPECIAL_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
   }


}
/* Modification History:
 *
 * $Log$
 * Revision 1.13  2001/07/02 18:02:08  thomas
 * added warning message if setSpecial wont allow value to be set.
 *
 * Revision 1.12  2001/06/19 15:38:01  thomas
 * added convience constructor methods
 *
 * Revision 1.11  2001/05/10 21:43:06  thomas
 * added resetXMLAttributes to init().
 *
 * Revision 1.10  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.9  2001/01/19 22:33:52  thomas
 * ValueIdRef was misspelled!!! Now methods, attributes are
 * correct. -b.t.
 *
 * Revision 1.8  2000/11/16 20:11:01  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.7  2000/11/08 20:24:00  thomas
 * Inserted copywrite, fixed some documentation. -b.t.
 *
 * Revision 1.6  2000/11/02 20:33:49  kelly
 * finished the class
 *
 * Revision 1.5  2000/11/02 18:39:24  thomas
 * Made changes. forget what.. -b.t.
 *
 * Revision 1.4  2000/10/26 20:41:30  thomas
 * Inserted needed code to bring this to initial version. -b.t.
 *
 * Revision 1.3  2000/10/11 18:41:41  kelly
 * added modification history.  -k.z.
 *
 */
