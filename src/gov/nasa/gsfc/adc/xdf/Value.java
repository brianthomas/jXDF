

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
   // Constructors
   //

   /** Passed string sets the *value* attribute (PCDATA)
       of this object.
    */

   public Value (String strValue) {
      init();
      setValue(strValue); // set value attribute from passed argument
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
      return (String) ((XMLAttribute) attribHash.get("value")).getAttribValue();
   }

   /** set the *value* attribute.
    */
   public void setValue (String strValue)
   {
      ((XMLAttribute) attribHash.get("value")).setAttribValue(strValue);
   }

  /** get the *valueId* attribute.
   */
   public String getValueId() {
      return (String) ((XMLAttribute) attribHash.get("valueId")).getAttribValue();
   }

   /** set the *valueId* attribute.
    */
   public void setValueId (String strValueId)
   {
      ((XMLAttribute) attribHash.get("valueId")).setAttribValue(strValueId);
   }

   /** get the *valueIdRef* attribute.
   */
   public String getValueIdRef() {
      return (String) ((XMLAttribute) attribHash.get("valueIdRef")).getAttribValue();
   }

   /** set the *valueRef* attribute.
   */
   public void setValueIdRef (String strValueRef)
   {
      ((XMLAttribute) attribHash.get("valueIdRef")).setAttribValue(strValueRef);
   }

    /** get the *inequality*   attribute.
    */
   public String getInequality() {
      return (String) ((XMLAttribute) attribHash.get("inequality")).getAttribValue();
   }


   /** set the *inequality* attribute.
    */
   public void setInequality (String strInequality)
   {
      if (Utility.isValidValueInequality(strInequality))
        ((XMLAttribute) attribHash.get("inequality")).setAttribValue(strInequality);
   }

  /** get the *special* attribute.
   */
   public String getSpecial() {
      return (String) ((XMLAttribute) attribHash.get("special")).getAttribValue();
   }

   /** set the *special* attribute.
    */
   public void setSpecial (String strSpecial)
   {
      if (Utility.isValidValueSpecial(strSpecial))
       ((XMLAttribute) attribHash.get("special")).setAttribValue(strSpecial);
   }


   //
   // Private Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {

       classXDFNodeName = "value";

       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
      attribOrder.add(0,"value");
      attribOrder.add(0,"inequality");
      attribOrder.add(0,"special");
      attribOrder.add(0,"valueIdRef");
      attribOrder.add(0,"valueId");


       //set up the attribute hashtable key with the default initial value
       attribHash.put("value", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("inequality", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("special", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("valueIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("valueId", new XMLAttribute(null, Constants.STRING_TYPE));
   }


}
/* Modification History:
 *
 * $Log$
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
