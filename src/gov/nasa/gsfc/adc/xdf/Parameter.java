
// XDF Parameter Class
// CVS $Id$

// Parameter.java Copyright (C) 2000 Brian Thomas,
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Parameter.java: An XDF::Parameter describes a scientific parameter assocated
 * with the Structure or Array object that it is contained in.
 * Parameter is a flexible container for holding what is essentially information
 * about data but is not needed to read/write/manipulate the data in a
 *  mathematical sense.
 * @version $Revision$
 */

 /**  Description of class attributes:
  * name--
  * The string description (short name) of this object.
  * description--
  * the string description (long name) of this object.
  * paramId--
  * a string holding the param Id of this object.
  * paramIdRef--
  * a  string holding the parameter id reference to another parameter.
  * datatype--
  * holds object reference to a single datatype (DataFormat) object for this axis.
  * units--
  * reference of the Units object of this parameter. The XDF::Units object
  * is used to hold the XDF::Unit objects.
  * noteList--
  * list reference to the Note objects held within this parameter.
  * valueList--
  * list reference to the Value objects held within in this parameter.
  */


public class Parameter extends BaseObject {

  //
  // Fields
  //

  /** This field stores object references to those value group objects
    * to which this object belongs
    */
  protected Set valueGroupOwnedHash = Collections.synchronizedSet(new HashSet());

  //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Parameter ()
  {
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Parameter ( Hashtable InitXDFAttributeTable )
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

    classXDFNodeName = "parameter";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"noteList");
    attribOrder.add(0,"valueList");
    attribOrder.add(0,"units");
    attribOrder.add(0,"datatype");
    attribOrder.add(0,"paramIdRef");
    attribOrder.add(0,"paramId");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

     //set up the attribute hashtable key with the default initial value
    attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("valueList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("units", new XMLAttribute(new Units(), Constants.OBJECT_TYPE));
    attribHash.put("datatype", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("paramIdRef", new XMLAttribute(null, Constants.STRING_TYPE));  //double check k.z.
    attribHash.put("paramId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  //
  //Get/Set Methods
  //

  /**set the *name* attribute
   */
  public void setName (String strName)
  {
    ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);
  }

   /**getName
   * @return: the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

   /**set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
    ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);

  }

  /**getDescription
   * @return: the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  /** set the *paramId* attribute
   */
  public void setParamId (String strParam)
  {
     ((XMLAttribute) attribHash.get("paramId")).setAttribValue(strParam);
  }

  /**getParamId
   * @return: the current *paramId* attribute
   */
  public String getParamId()
  {
    return (String) ((XMLAttribute) attribHash.get("paramId")).getAttribValue();
  }

  /** set the *paramIdRef* attribute
   */
  public void setParamIdRef (String strParam)
  {
    ((XMLAttribute) attribHash.get("paramIdRef")).setAttribValue(strParam);

  }

  /**getParamIdRef
   * @return: the current *paramIdRef* attribute
   */
  public String getParamIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("paramIdRef")).getAttribValue();
  }

  /**set the *units* attribute
   */
  public void setUnits (Units units)
  {
     ((XMLAttribute) attribHash.get("units")).setAttribValue(units);
  }

  /**getUnits
   * @return: the current *units* attribute
   */
  public Units getUnits()
  {
    return (Units) ((XMLAttribute) attribHash.get("units")).getAttribValue();
  }

  /**set the *datatype* attribute
   */
  public void setDatatype(String strDatatype)
  {
    if (Utility.isValidDatatype(strDatatype))
       ((XMLAttribute) attribHash.get("datatype")).setAttribValue(strDatatype);
    else
       Log.warnln("Datatype not valid, ignoring request to set.");
  }

  /**getDatatype
   * @return: the current *datatype* attribute
   */
  public String getDatatype()
  {
    return (String) ((XMLAttribute) attribHash.get("datatype")).getAttribValue();
  }

  /**set the *valueList* attribute
   */
  public void setValueList(List value) {
     ((XMLAttribute) attribHash.get("valueList")).setAttribValue(value);
  }

  /**getValueList
   * @return: the current *valueList* attribute
   */
  public List getValueList() {
    return (List) ((XMLAttribute) attribHash.get("valueList")).getAttribValue();
  }

  /**set the *noteList* attribute
   */
  public void setNoteList(List note) {
     ((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
  }

  /**getNoteList
   * @return: the current *noteList* attribute
   */
  public List getNoteList() {
    return (List) ((XMLAttribute) attribHash.get("noteList")).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** addValue: insert an XDF::Value object into the valueList
   * @param: XDF::Value
   * @return: an XDF::Value object on success, null on failure
   */
  public Value addValue(Value v) {
    if (v == null) {
      Log.warn("in Parameter.addValue(), the Value passed in is null");
      return null;
    }
    getValueList().add(v);
    return v;
  }

  /**removeValue: removes an XDF::Value from the list of values in this Parameter object
   * @param: Value to be removed
   * @return: true on success, false on failure
   */
   public boolean removeValue(Value what) {
     return removeFromList(what, getValueList(), "valueList");
  }


   /**Insert a ValueGroup object into this object to group the parameter values.
   * @Value: ValueGroup to be added
   * @return:a ValueGroup object reference on success, null on failure.
   */
  public ValueGroup addValueGroup (ValueGroup group) {
    if (group !=null) {
      //add the group to the groupOwnedHash
      valueGroupOwnedHash.add(group);
      return group;
    }
    else {
      Log.warn("in Parameter,addValueGroup(). ValueGroup passed in is null");
      return null;
    }
  }

  /**removeValueGroup: remove a ValueGroup object from this object
   * @Value: ValueGroup to be removed
   * @return: true on success, false on failure
   */
  public boolean removeValueGroup(ValueGroup group) {
    if (group == null) {
      Log.warn("in Axis,removeValueGroup().  ValueGroup passed in is null");
      return false;
    }
    return valueGroupOwnedHash.remove(group);
  }


  /**removeValue: removes an XDF::Value from the list of values in this Parameter object
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeValue(int index) {
     return removeFromList(index, getValueList(), "valueList");
  }

  /**getValues: A convinience methods.  returns a list of values in this parameter
   */
  public List getValues() {
    return getValueList();
  }

 /** addNote: insert an XDF::Note object into the list of notes in this Parameter object
   * @param: XDF::Note
   * @return: an XDF::Note object on success, null on failure
   */
  public Note addNote(Note n) {
    if (n == null) {
      Log.warn("in Parameter.addNote(), the Note passed in is null");
      return null;
    }
    getNoteList().add(n);
    return n;
  }

  /**removeNote: removes an XDF::Note object from the list of notes in this Parameter object
   * @param: Note to be removed
   * @return: true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), "noteList");
  }



   /**removeNote: removes an XDF::Note object from the list of notes in this Parameter object
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), "noteList");
  }

  /**getNotes: Convenience method which returns a list of the notes held by
   * this object.
   */
  public List getNotes() {
    return getNoteList();
  }

  /**addUnit: Insert an XDF::Unit object into the L<XDF::Units> object
   * held in this object.
   * @param: Unit to be added
   * @return: an XDF::Unit object if successfull, null if not.
   */
  public Unit addUnit(Unit unit) {
    return  getUnits().addUnit(unit);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: Unit to be removed
   * @return: true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    return getUnits().removeUnit(what);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: list index number
   * @return: true if successful, false if not
   */
  public boolean removeUnit(int index) {
    return getUnits().removeUnit(index);
  }

  public Object clone() throws CloneNotSupportedException {
    Parameter cloneObj = (Parameter) super.clone();

    //deep copy of the valueGroupOwnedHash
     synchronized (this.valueGroupOwnedHash) {
      synchronized(cloneObj.valueGroupOwnedHash) {
        cloneObj.valueGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.valueGroupOwnedHash.size()));
        Iterator iter = this.valueGroupOwnedHash.iterator();
        while (iter.hasNext()) {
          cloneObj.valueGroupOwnedHash.add(iter.next());
        }
      }
    }
    return cloneObj;
  }

 }
