
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
  private static final String SYSTEM_XML_ATTRIBUTE_NAME = new String("system");
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
    ((Attribute) attribHash.get("factor")).setAttribValue(factor);
  }

  /**
   * @return the current *factor* attribute
   */
  public Double getFactor () {
    return (Double) ((Attribute) attribHash.get("factor")).getAttribValue();
  }

  /**set the *system* attribute
     @param String
     @return the current *system* attribute
   */
  public void setSystem (String system) {
    ((Attribute) attribHash.get(SYSTEM_XML_ATTRIBUTE_NAME)).setAttribValue(system);
  }

  /**
   * @return the current *system* attribute
   */
  public String getSystem () {
    return (String) ((Attribute) attribHash.get(SYSTEM_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Set the *unitList* attribute
      @param List
      @deprecated You should use the add/remove methods to manipulate this list.
   */
  public void setUnitList(List units) {
    ((Attribute) attribHash.get("unitList")).setAttribValue(units);
  }

  /**
   * @return the current *unitList* attribute
   */
  public List getUnitList() {
    return (List) ((Attribute) attribHash.get("unitList")).getAttribValue();
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
     return removeFromList(what, getUnitList(), "unitList");
  }

  /**Remove an Unit object from the list of units held in
   * this object
   * @param index - list index number of the Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(int index) {
     return removeFromList(index, getUnitList(), "unitList");
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

    resetAttributes();

    classXDFNodeName = "units";
    XDFNodeName = classXDFNodeName;

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,DESCRIPTION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0,"unitList");
    attribOrder.add(0,SYSTEM_XML_ATTRIBUTE_NAME);
    attribOrder.add(0,"factor");

    attribHash.put("unitList", new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put(SYSTEM_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put("factor", new Attribute(null, Constants.DOUBLE_TYPE));
    attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
  }


 }  //end of Units Class

 /* Modification History:
 *
 * $Log$
 * Revision 1.25  2001/10/02 20:17:09  thomas
 * minimal change to XMLattrib internals
 *
 * Revision 1.24.2.1  2001/10/02 20:13:05  thomas
 * minimal internal change to XML attrib
 *
 * Revision 1.24  2001/09/20 20:10:38  huang
 * fixed a bug in set/getDescription()
 *
 * Revision 1.23  2001/09/19 17:51:32  thomas
 * made some set*List methods deprecated
 *
 * Revision 1.22  2001/09/13 21:39:25  thomas
 * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
 *
 * Revision 1.21  2001/09/06 15:56:41  thomas
 * changed basicXMLWriter to return String (nodeName)
 *
 * Revision 1.20  2001/09/05 22:00:58  thomas
 * removed toXMLoutputstream, toXMLWriter. Made it basicXMLWriter
 *
 * Revision 1.19  2001/07/31 21:09:04  thomas
 * bug fix, needed toXMLWriter method.
 *
 * Revision 1.18  2001/07/19 21:59:44  thomas
 * yanked XMLDeclAttribs from toXMLOutputStream (only needed
 * in the XDF class)
 *
 * Revision 1.17  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.16  2001/06/26 21:22:26  huang
 * changed return type to boolean for all addObject()
 *
 * Revision 1.15  2001/05/24 17:24:24  huang
 * added/modified constructors and other convenience methods
 *
 * Revision 1.14  2001/05/22 21:27:43  huang
 * added description set/get methods
 *
 * Revision 1.13  2001/05/10 21:43:06  thomas
 * added resetAttributes to init().
 *
 * Revision 1.12  2001/05/02 18:16:39  thomas
 * Minor changes related to API standardization effort.
 *
 * Revision 1.11  2000/11/27 16:57:45  thomas
 * Made init method protected so that extending
 * Dataformats may make use of them. -b.t.
 *
 * Revision 1.10  2000/11/20 22:03:48  thomas
 * Split up Attribute type NUMBER_TYPE into
 * INTEGER_TYPE and DOUBLE_TYPE. This allows for
 * some needed handling in the SaxDocHandler when
 * parsing data for the formatted read. Put prior NUMBER_TYPE
 * attributes into appropriate new category. -b.t.
 *
 * Revision 1.9  2000/11/16 20:09:57  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.8  2000/11/09 04:24:12  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.7  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.6  2000/11/08 20:17:09  thomas
 * Trimmed down import path to just needed classes -b.t
 *
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


