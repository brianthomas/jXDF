
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
      return (String) ((Attribute) attribHash.get(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *upperErrorValue* attribute.
    */
   public void setUpperErrorValue (Number upperErrorValue)
   {
      ((Attribute) attribHash.get(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(upperErrorValue);
   }

   /** get the *lowerErrorValue* attribute.
   */
   public String getLowerErrorValue() {
      return (String) ((Attribute) attribHash.get(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *lowerErrorValue* attribute.
    */
   public void setLowerErrorValue (Number lowerErrorValue)
   {
      ((Attribute) attribHash.get(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(lowerErrorValue);
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
       attribHash.put(UPPER_ERROR_VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
       attribHash.put(LOWER_ERROR_VALUE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

   }
}

