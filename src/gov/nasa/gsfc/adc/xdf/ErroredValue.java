
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

/** ErroredValue describes a single scalar (number or string)
    that has an associated error value. Parameter uses this object
    to store its (mathematical) value.
    @version $Revision$
*/
public class  ErroredValue extends Value {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME = "upperErrorValue";
   private static final String LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME = "lowerErrorValue";

   //
   // Constructors
   //

   /** Passed string sets the *value* attribute (PCDATA)
       of this object.
    */
   public ErroredValue (String strValue) {
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

  /** set the upperErrorValue and lowerErrorValue attributes
      to the same value.
   */
   public void setErrorValue (Number errorValue)
   {
      setUpperErrorValue(errorValue);
      setLowerErrorValue(errorValue);
   }

   /** This returns the *lowerErrorValue*. 
    */
   public String getErrorValue() 
   {
      return getLowerErrorValue();
   }

   /** A convenience method which returns a String array holding 
       the value of the lowerErrorValue and upperErrorValue attributes. 
    */
   public String[] getErrorValues () {
      String values[] = new String [2];
      values[0] = getLowerErrorValue();
      values[1] = getUpperErrorValue();
      return values;
   }

  /** get the *upperErrorValue* attribute.
   */
   public String getUpperErrorValue() {
      return (String) ((XMLAttribute) attribHash.get(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *upperErrorValue* attribute.
    */
   public void setUpperErrorValue (Number upperErrorValue)
   {
      ((XMLAttribute) attribHash.get(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(upperErrorValue);
   }

   /** get the *lowerErrorValue* attribute.
   */
   public String getLowerErrorValue() {
      return (String) ((XMLAttribute) attribHash.get(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *lowerErrorValue* attribute.
    */
   public void setLowerErrorValue (Number lowerErrorValue)
   {
      ((XMLAttribute) attribHash.get(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(lowerErrorValue);
   }

   //
   // Protected Methods
   //

   /** A special method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init ()
   {

       super.init();

       // append more attributes
       attribOrder.add(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME);

       //set up the attribute hashtable key with the default initial value
       attribHash.put(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

   }
}
/* Modification History:
 *
 * $Log$
 * Revision 1.8  2001/06/12 16:03:58  huang
 * initialize a variable
 *
 * Revision 1.7  2001/05/04 20:24:13  thomas
 * added super.init() to init() method.
 *
 * Revision 1.6  2001/02/07 18:08:55  thomas
 * removed duplicitous setErrorValue method.
 *
 * Revision 1.5  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.4  2001/01/19 17:23:59  thomas
 * Fixed class to match DTD standard. Now there
 * is no attribute called "errorValue". -b.t.
 *
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
