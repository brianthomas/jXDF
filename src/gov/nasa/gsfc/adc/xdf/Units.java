// XDF Units Class
// CVS $Id$


// Units.java Copyright (C) 2000 Brian Thomas,
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
import java.util.*;
import java.io.*;

/**
 * Units.java:
 * @version $Revision$
 */

 public class Units extends BaseObject {
  //
  //Fields
  //

  protected String XDFNodeName;

  //double check
  protected static String unitDivideSymbol = "/";
  protected static String classNoUnitChildNodeName = "unitless";

 //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Units ()
  {
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Units ( Hashtable InitXDFAttributeTable )
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

    classXDFNodeName = "units";
    XDFNodeName = classXDFNodeName;

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"unitList");
    attribOrder.add(0,"system");
    attribOrder.add(0,"factor");

    attribHash.put("unitList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("system", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("factor", new XMLAttribute(null, Constants.NUMBER_TYPE));
  }
  //
  //Get/Set Methods

  /**setXDFNodeName: change the XDF node name for this object.
   * @param: String
   * @return: the current XDF node name
   */
  public String setXDFNodeName(String strName) {
    XDFNodeName = strName;
    return XDFNodeName;
  }

  /**setFactor: set the *factor* attribute
   * @param: Number
   * @return: the current *factor* attribute
   */
  public Number setFactor (Number factor) {
    Log.info("in Units.setFactor()");
    return (Number) ((XMLAttribute) attribHash.get("factor")).setAttribValue(factor);
  }

  /**getFactor
   * @return: the current *factor* attribute
   */
  public Number getFactor () {
    return (Number) ((XMLAttribute) attribHash.get("factor")).getAttribValue();
  }

  /**setSystem: set the *system* attribute
   * @param: String
   * @return: the current *system* attribute
   */
  public String setSystem (String system) {
    return (String) ((XMLAttribute) attribHash.get("system")).setAttribValue(system);
  }

  /**getSystem
   * @return: the current *system* attribute
   */
  public String getSystem () {
    return (String) ((XMLAttribute) attribHash.get("system")).getAttribValue();
  }

  /**setUnitList: set the *unitList* attribute
   * @param: List
   * @return: the current *unitList* attribute
   */
  public List setUnitList(List units) {
    return (List)((XMLAttribute) attribHash.get("unitList")).setAttribValue(units);
  }

  /**getUnitList
   * @return: the current *unitList* attribute
   */
  public List getUnitList() {
    return (List) ((XMLAttribute) attribHash.get("unitList")).getAttribValue();
  }

  /**getUnits: convenience method that returns the list of units this object holds
   *
   */
  public List getUnits() {
    return getUnitList();
  }

  /** getClassNoUnitChildNodeName
   * return: Name of the child node to print in the toXMLOutputStream method when
   * an  XDF::Units object contains NO XDF::Unit child objects.
   */
  public String getClassNoUnitChildName() {
    return classNoUnitChildNodeName;
  }

  //
  //Other PUBLIC Methods
  //

  /**addUnit: Insert an XDF::Unit object into the list of units held in this object
   * @param: Unit to be added
   * @return: an XDF::Unit object if successfull, null if not.
   */
  public Unit addUnit(Unit unit) {
    if (unit == null) {
      Log.warn("in Units.addUnit(), the Unit passed in is null");
      return null;
    }
    getUnitList().add(unit);
    return unit;
  }

   /**removeUnit: Remove an XDF::Unit object the list of units held in
   * this object
   * @param: Unit to be removed
   * @return: true if successful, false if not
   */
   public boolean removeUnit(Unit what) {
     return removeFromList(what, getUnitList(), "unitList");
  }

  /**removeUnit: Remove an XDF::Unit object from the list of units held in
   * this object
   * @param: list index number
   * @return: true if successful, false if not
   */
  public boolean removeUnit(int index) {
     return removeFromList(index, getUnitList(), "unitList");
  }

  /**value
   * assemble all the units in the list of units held in this object and return
   * it as a string
   */
   public String value() {
    StringBuffer strValue = new StringBuffer();
    Number factor = getFactor();  //retrieve the *factor* attribute
    List units = getUnitList();   //retrieve the *unitList* attribute
    Unit unit;
    Number power;

    if ( factor != null) {
      strValue.append(factor.doubleValue());  //append *factor* value
    }

    for ( int i = 0; i < units.size(); i ++ ) {
      unit = (Unit) units.get(i);
      strValue.append(unit.getValue());  //append *value* attribute of Unit
      power = unit.getPower();
      if (power !=null) {  //append *power* attribute of Unit
        strValue.append("**");
        strValue.append(power.floatValue());
        strValue.append("  ");
      }
    }

    Log.debug("exiting Units.value(): value = " + strValue.toString());
    return strValue.toString();
  }


  public void toXMLOutputStream  (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent
                                 )

  {
    super.toXMLOutputStream( outputstream,
                             XMLDeclAttribs,
                             indent,
                             false,
                             XDFNodeName,
                             classNoUnitChildNodeName
                           );
  }


 }  //end of Units Class

 /* Modification History:
 *
 * $Log$
 * Revision 1.5  2000/11/08 19:18:07  thomas
 * Changed the name of toXDF* methods to toXML* to
 * better reflect the nature of the output (its not XDF
 * unless you call th emethod from strcuture object;
 * otherwise, it wont validate as XDF; it is still XML
 * however). -b.t.
 *
 * Revision 1.4  2000/10/27 21:22:48  kelly
 * fixed bug in *toXML*  -k.z.
 *
 * Revision 1.3  2000/10/11 14:37:17  kelly
 * complete value(), toXDFOutputStream(), added more documentation.
 * this file is considered done  -k.z.
 *
 */


