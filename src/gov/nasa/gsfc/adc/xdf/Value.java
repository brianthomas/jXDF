
// XDF Value
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

/** Value.java
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

   /** No-argument constructor. 
    */
   public Value () {
      init();
   }

   //
   // Public Methods
   //

   /**getValue: get the *value* (PCDATA) attribute. 
   */
   public String getValue() {
      return (String) ((XMLAttribute) attribHash.get("value")).getAttribValue();
   }

   /**
    */

   /**setValue: set the *value* attribute. 
      @return: the current *value* attribute (PCDATA) 
   */
   public void setValue (String strValue)
   {
      ((XMLAttribute) attribHash.get("value")).setAttribValue(strValue);
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

       //set up the attribute hashtable key with the default initial value
       attribHash.put("value", new XMLAttribute(null, Constants.STRING_TYPE));

   };


}
/* Modification History:
 *
 * $Log$
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
