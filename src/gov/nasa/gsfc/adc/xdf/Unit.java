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
 *  describes a unit within a given units object
 * @version $Revision$
 */

 /** attribute description:
  * power--
  * The power of this unit. Takes a SCALAR number value.
  * value--
  * The value of this unit (e.g. "m" or "cm" or "km", etc)
  */

public class Unit extends BaseObject {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");
   private static final String POWER_XML_ATTRIBUTE_NAME = new String("power");


  //
  // Constructor
  //

  /** The no argument constructor.
   */
  public Unit ()
  {
      this (null, null);
  }

  public Unit(String value) {
      this (value,null);
  }

  public Unit(String value, double power) {
      this (value, new Double(power));
  }

  public Unit(String value, Double power) {
    init();
    if (value != null)
       setValue(value);
    if (power != null)
       setPower(power);
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

  //
  //Get/Set methods
  //

  /**set the *power* attribute
   *
   * @return the current *power* attribute
   */
  public void setPower (Double power) {
     ((XMLAttribute) attribHash.get(POWER_XML_ATTRIBUTE_NAME)).setAttribValue(power);
  }

  /**
   * @return the current *power* attribute
   */
  public Double getPower() {
    return (Double) ((XMLAttribute) attribHash.get(POWER_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *value* attribute
   * @return the current *value* attribute
   */
  public void setValue(String value) {
     ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**
   * @return the current *value* attribute
   */
  public String getValue() {
    return (String) ((XMLAttribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Protected Methods
  //
  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    resetXMLAttributes();

    classXDFNodeName = "unit";  //XDF node name

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, POWER_XML_ATTRIBUTE_NAME);

    attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(POWER_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.DOUBLE_TYPE));

  };

 }

 /* Modification History:
 *
 * $Log$
 * Revision 1.12  2001/05/22 19:35:59  huang
 * added several constructor methods
 *
 * Revision 1.11  2001/05/10 21:42:26  thomas
 * added resetXMLAttributes to init().
 *
 * Revision 1.10  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.9  2000/11/27 16:57:45  thomas
 * Made init method protected so that extending
 * Dataformats may make use of them. -b.t.
 *
 * Revision 1.8  2000/11/20 22:03:48  thomas
 * Split up XMLAttribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.7  2000/11/16 20:09:50  kelly
 * fixed documentation.  -k.z.
 *
 */



