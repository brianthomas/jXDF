
// XDF  ErroredValue
// CVS $Id$

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

/**  ErroredValue describes a single scalar (number or string)
  * that has an associated error value. Parameter uses this object
  * to store its (mathematical) value.
   @version $Revision$
  */
public class  ErroredValue extends Value {

   //
   // Constructors
   //

   /** Passed string sets the *value* attribute (PCDATA)
       of this object.
    */
   public  ErroredValue (String strValue) {
      super(strValue); // set value attribute from passed argument
   }

   //
   //no-arg constructor
   //
    public  ErroredValue () {
      super();
   }
   //
   // Public Methods
   //

  /** get the *upperErrorValue* attribute.
   */
   public String getUpperErrorValue() {
      return (String) ((XMLAttribute) attribHash.get("upperErrorValue")).getAttribValue();
   }

   /** set the *upperErrorValue* attribute.
    */
   public void setUpperErrorValue (Number upperErrorValue)
   {
      ((XMLAttribute) attribHash.get("upperErrorValue")).setAttribValue(upperErrorValue);
   }

   /** get the *lowerErrorValue* attribute.
   */
   public String getLowerErrorValue() {
      return (String) ((XMLAttribute) attribHash.get("lowerErrorValue")).getAttribValue();
   }

   /** set the *lowerErrorValue* attribute.
    */
   public void setLowerErrorValue (Number lowerErrorValue)
   {
      ((XMLAttribute) attribHash.get("lowerErrorValue")).setAttribValue(lowerErrorValue);
   }

   /** get the *lowerErrorValue* attribute.
   */
   public String getErrorValue() {
      return (String) ((XMLAttribute) attribHash.get("errorValue")).getAttribValue();
   }

   /** set the *errorValue* attribute.
    */
   public void setErrorValue (Number ErrorValue)
   {
      ((XMLAttribute) attribHash.get("errorValue")).setAttribValue(ErrorValue);
   }




   //
   // Private Methods
   //

   /** A special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   private void init()
   {
      // append more attributes
      attribOrder.add("upperErrorValue");
      attribOrder.add("lowerErrorValue");
      attribOrder.add("errorValue");


       //set up the attribute hashtable key with the default initial value
       attribHash.put("upperErrorValue", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("lowerErrorValue", new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put("errorValue", new XMLAttribute(null, Constants.STRING_TYPE));

   }
}
/* Modification History:
 *
 * $Log$
 * Revision 1.3  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.2  2000/11/16 19:57:29  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.1  2000/11/02 20:34:17  kelly
 * created the class
 *
 *
 */
