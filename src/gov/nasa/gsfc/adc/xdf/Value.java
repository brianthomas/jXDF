
// XDF Value
// CVS $Id$

package gov.nasa.gsfc.adc.xdf;

/** Value.java
 * @version $Revision$
  */

public class Value extends BaseObject{

   protected String Value;
   public Value (String strValue) {
      Value = strValue;
   }

   public String getValue() {
      return Value;
   }

}
/* Modification History:
 *
 * $Log$
 * Revision 1.3  2000/10/11 18:41:41  kelly
 * added modification history.  -k.z.
 *
 */
