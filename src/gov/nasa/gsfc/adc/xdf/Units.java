
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;

/**
 *  handles a list of units
 * @version $Revision$
 */

 public class Units extends BaseObject {
  //
  //Fields
  //

  private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
  private static final String FACTOR_XML_ATTRIBUTE_NAME = new String("factor");
  private static final String UNITSLIST_XML_ATTRIBUTE_NAME = new String("unitsList");
  protected String XDFNodeName;

  //double check
  protected static String unitDivideSymbol = "/";
  protected static String noUnitChildNodeName = "unitless";

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

  // 
  // Class methods
  //

  /**
   * @return Name of the child node to print in the toXMLOutputStream method when
   * an  Units object contains NO Unit child objects.
   */
  static public String getNoUnitChildXMLNodeName() {
    return noUnitChildNodeName;
  }


  //
  //Get/Set Methods
  //

  /**Change the XDF node name for this object.
     @param String
     @return the current XDF node name
   */
  public void setXDFNodeName(String strName) {
    XDFNodeName = strName;
  }


  /**set the *factor* attribute
   * @param Number
   * @return the current *factor* attribute
   */
  public void setFactor (Double factor) {
    ((Attribute) attribHash.get(FACTOR_XML_ATTRIBUTE_NAME)).setAttribValue(factor);
  }

  /**
   * @return the current *factor* attribute
   */
  public Double getFactor () {
    return (Double) ((Attribute) attribHash.get(FACTOR_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Set the *unitList* attribute
      @param List
      @deprecated You should use the add/remove methods to manipulate this list.
   */
  public void setUnitList(List units) {
    ((Attribute) attribHash.get(UNITSLIST_XML_ATTRIBUTE_NAME)).setAttribValue(units);
  }

  /**
   * @return the current *unitList* attribute
   */
  public List getUnitList() {
    return (List) ((Attribute) attribHash.get(UNITSLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** convenience method that returns the list of units this object holds
   *
   */
  public List getUnits() {
    return getUnitList();
  }

  //
  //Other PUBLIC Methods
  //

  /** Insert an Unit object into the list of units held in this object
   * @param Unit to be added
   * @return an Unit object if successfull, null if not.
   */
  public boolean addUnit(Unit unit) {
    getUnitList().add(unit);
    return true;
  }

   /** Remove an Unit object the list of units held in
   * this object
   * @param what - Unit to be removed
   * @return true if successful, false if not
   */
   public boolean removeUnit(Unit what) {
     return removeFromList(what, getUnitList(), UNITSLIST_XML_ATTRIBUTE_NAME);
  }

  /**Remove an Unit object from the list of units held in
   * this object
   * @param index - list index number of the Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(int index) {
     return removeFromList(index, getUnitList(), UNITSLIST_XML_ATTRIBUTE_NAME);
  }

  /** 
   * set the description* attribute
   */
  public void setDescription (String strDesc) {
      ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
  }

  /**
   * @return the current *description* attribute
   */
  public String getDescription() {
      return (String) ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
   * Assemble all the units in the list of units held in this object and return
   * it as a string
   */
   public String toString() {
    StringBuffer strValue = new StringBuffer();
    Number factor = getFactor();  //retrieve the *factor* attribute
    List units = getUnitList();   //retrieve the *unitList* attribute
    Unit unit;
    Number power;

    if ( factor != null) {
      strValue.append(factor.doubleValue());  //append *factor* value
    }

    int size = units.size();
    for ( int i = 0; i < size; i ++ ) {
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


  protected String basicXMLWriter (
                                   Writer outputWriter,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  throws java.io.IOException
  {
     return super.basicXMLWriter( outputWriter, indent, false, XDFNodeName, noUnitChildNodeName);
  }

  //
  // Protected Methods
  //

  /** Special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    super.init();

    classXDFNodeName = "units";
    XDFNodeName = classXDFNodeName;

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,DESCRIPTION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0,UNITSLIST_XML_ATTRIBUTE_NAME);
    attribOrder.add(0,FACTOR_XML_ATTRIBUTE_NAME);

    attribHash.put(UNITSLIST_XML_ATTRIBUTE_NAME, 
        new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put(FACTOR_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.DOUBLE_TYPE));
    attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
  }


 }  //end of Units Class

