
// XDF Unit Class
// CVS $Id$

// Unit.java Copyright (C) 2000 Brian Thomas,
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

import java.util.Hashtable;

/**
 * Unit.java: describes a unit within a given units object
 * @version $Revision$
 */

 /** attribute description:
  * power--
  * The power of this unit. Takes a SCALAR number value.
  * value--
  * The value of this unit (e.g. "m" or "cm" or "km", etc)
  */

 public class Unit extends BaseObject{
  //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Unit ()
  {
    init();
  }

  public Unit(String value) {
    init();
    setValue(value);
  }


  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Unit ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "unit";  //XDF node name

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"value");
    attribOrder.add(0,"power");

    attribHash.put("value", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("power", new XMLAttribute(null, Constants.NUMBER_TYPE));

  };

  //
  //Get/Set methods
  //

  /**set the *power* attribute
   * @param: Number
   * @return: the current *power* attribute
   */
  public void setPower(Number power) {
     ((XMLAttribute) attribHash.get("power")).setAttribValue(power);
  }

  /**getPower
   * @return: the current *power* attribute
   */
  public Number getPower() {
    return (Number) ((XMLAttribute) attribHash.get("power")).getAttribValue();
  }

  /**set the *value* attribute
   * @param: String
   * @return: the current *value* attribute
   */
  public void setValue(String value) {
     ((XMLAttribute) attribHash.get("value")).setAttribValue(value);
  }

  /**getValue
   * @return: the current *value* attribute
   */
  public String getValue() {
    return (String) ((XMLAttribute) attribHash.get("value")).getAttribValue();
  }

// I dont think this is even used. commented out for now. -b.t. 
  /*update: special for Value Objects
   * set its value attribute to the contents of passed string
   * overload the update in BaseObject which takes the Hashtable as param
   */
/*
  public String update(String value) {
    return setValue(value);
  }
*/
 }
