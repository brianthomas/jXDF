
// XDF Value
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

/** Value.java
 * @version $Revision$
  */

public class Value extends BaseObject{


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
   public String setValue (String strValue)
   {
      return (String) ((XMLAttribute) attribHash.get("value")).setAttribValue(strValue);
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
 * Revision 1.4  2000/10/26 20:41:30  thomas
 * Inserted needed code to bring this to initial version. -b.t.
 *
 * Revision 1.3  2000/10/11 18:41:41  kelly
 * added modification history.  -k.z.
 *
 */
