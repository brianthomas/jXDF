
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
 * A Parameter describes a scientific parameter assocated
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
  * dataFormat--
  * holds object reference to a single DataFormat object for this object.
  * units--
  * reference of the Units object of this parameter. The Units object
  * is used to hold the Unit objects.
  * noteList--
  * list reference to the Note objects held within this parameter.
  * valueList--
  * list reference to the Value objects held within in this parameter.
  */


public class Parameter extends BaseObjectWithValueList {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("paramId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("paramIdRef");
   private static final String CONVERSION_XML_ATTRIBUTE_NAME = new String("conversion");
   private static final String DATAFORMAT_XML_ATTRIBUTE_NAME = new String("dataFormat");
   private static final String UNITS_XML_ATTRIBUTE_NAME = new String("units");
   private static final String NOTELIST_XML_ATTRIBUTE_NAME = new String("notes");
   private static final String VALUELIST_XML_ATTRIBUTE_NAME = new String("valueList");

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


  public Parameter (String nodeName, String value, String description) {
      init();
      if (nodeName != null)
	  setName (nodeName);
      if (value != null)
	  addValue (new Value (value));
      if (description != null)
	  setDescription (description);

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


  //
  //Get/Set Methods
  //

  /**set the *name* attribute
   */
  public void setName (String strName)
  {
    ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
  }

   /**
   * @return the current *name* attribute
   */
  public String getName()
  {
    return (String) ((Attribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /**set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
    ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);

  }

  /**
   * @return the current *description* attribute
   */
  public String getDescription() {
    return (String) ((Attribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *paramId* attribute
   */
  public void setParamId (String strParam)
  {
     ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strParam);
  }

  /**
   * @return the current *paramId* attribute
   */
  public String getParamId()
  {
    return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *paramIdRef* attribute
   */
  public void setParamIdRef (String strParam)
  {
    ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strParam);

  }

  /**
   * @return the current *paramIdRef* attribute
   */
  public String getParamIdRef()
  {
    return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *units* attribute
   */
  public void setUnits (Units units)
  {
     ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).setAttribValue(units);
  }

  /**
   * @return the current *units* attribute
   */
  public Units getUnits()
  {
    return (Units) ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** 
    *  Set how to convert values of the data in this parameter. 
    */
   public void setConversion(Conversion value)
   {
        ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }

  /**
   *  Get how to convert values of the data in this parameter. 
   * @return the current *dataFormat* object
   */
  public Conversion getConversion()
  {
     return (Conversion) ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /**  set the *dataFormat* attribute
       @return the current *dataFormat* attribute
    */
   public void setDataFormat (DataFormat dataFormat)
   {
       ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).setAttribValue(dataFormat);
   }

   /**
   * @return the current *dataFormat* attribute
   */
   public DataFormat getDataFormat()
   {
      return (DataFormat) ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).getAttribValue();
   }


  /** Set the list of values held by this Parameter from the passed list.
      @deprecated You should use the add/remove methods to manipulate this list.
   */
  public void setValueList(List listOfValues) {
     resetValues();
     Iterator iter = listOfValues.iterator();
     while (iter.hasNext()) {
        addValue((Value) iter.next());
     }
  }

  /**  Set the list of values held by this Parameter 
       using those held in the passed ValueList object.
    */
       // @deprecated You should use the add/remove methods to manipulate this list.
   public void setValueList (ValueListInterface valueListObj)
   {
      resetValues();
      addValueList(valueListObj);
   }

  /** Get the current list of values held within this Parameter. 
   * @return the current *valueList* attribute
   */
  public List getValueList() {
    return (List) ((Attribute) attribHash.get(VALUELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the list of notes within this object
   */
  public void setNotes (List notes) {
     ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
  }

  /**
   * @return the current list of notes in this object. 
   */
  public List getNotes() {
    return (List) ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
   * @return the current list of notes in this object. 
   * @depreciated Discontinued in favor of the getNotes method.
   */
  public List getNoteList() {
    return (List) ((Attribute) attribHash.get(NOTELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  //Other PUBLIC Methods
  //

  /** Append a list of values held by the passed ValueList into this Parameter 
    * object.
    */
   public boolean addValueList (ValueListInterface valueListObj) 
   {

      List values = valueListObj.getValues();

      // do we have any new values?
      if (values.size() > 0) {

         addValueListObj(valueListObj);

         // append in new values to Parameter obj 
         Iterator iter = values.iterator();
         while (iter.hasNext()) {
            Value thisValue = ((Value) iter.next());
            internalAddValue(thisValue);
         }

         return true;

      } else {

         // safety, needed?
         hasValueListCompactDescription = false;
         Log.warnln("Warning: no Values appended, ValueList empty. Parameter unchanged.");
         return false;

      }

   }

  /** Resets the value list in this parameter to be empty.
   */
  public void resetValues () {

     // bah. cant use iterator, get concurrent modification error then.. :P
     // remove old values by setting to empty list 
     ((Attribute) attribHash.get(VALUELIST_XML_ATTRIBUTE_NAME)).setAttribValue(Collections.synchronizedList(new ArrayList()));
     resetBaseValueListObjects();

  }


  /** Add a Value object to this Parameter. Note that adding a Value will
   * void any compact (valueList) description of the values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addValueList or setValueList methods; these prior Values *do* 
   * remain within the Parameter however). 
   * @param valueObj - Value object to be added
   * @return true on success, false on failure
   */

   public boolean addValue(Value valueObj) {

      if (internalAddValue(valueObj)) {

         // no compact description allowed now 
         resetBaseValueListObjects();
         return true;
      }
      return false;

   }

  /** removes an Value from the list of values in this Parameter object
   * @param what - Value to be removed
   * @return true on success, false on failure
   */
   public boolean removeValue(Value what) {
      boolean isRemoveSuccess = removeFromList(what, getValueList(), VALUELIST_XML_ATTRIBUTE_NAME);
      if (isRemoveSuccess) //decrement length if removal successful
      {
        // no compact description allowed now 
        resetBaseValueListObjects();
        return true;
      }
      return false;
  }


   /**Insert a ValueGroup object into this object to group the parameter values.
   * @param group - ValueGroup to be added
   * @return a ValueGroup object
   */
  public boolean addValueGroup (ValueGroup group) {
      //add the group to the groupOwnedHash
      valueGroupOwnedHash.add(group);
      return true;


  }

  /** remove a ValueGroup object from this object
   * @Value: group - ValueGroup to be removed
   * @return true on success, false on failure
   */
  public boolean removeValueGroup(ValueGroup group) {
    return valueGroupOwnedHash.remove(group);
  }


  /** removes an Value from the list of values in this Parameter object
   * @param index - list index number of the Value to be removed
   * @return true on success, false on failure
   */
  public boolean removeValue (int index) {
      boolean isRemoveSuccess = removeFromList(index, getValueList(), VALUELIST_XML_ATTRIBUTE_NAME);
      if (isRemoveSuccess) //decrement length if removal successful
      {
        // no compact description allowed now 
        resetBaseValueListObjects();
        return true;
      }
      return false;
  }

  /** A convinience methods.  returns a list of values in this parameter
   */
  public List getValues() {
    return getValueList();
  }

 /**  insert an Note object into the list of notes in this Parameter object
   * @param Note
   * @return an Note object
   */
  public boolean addNote(Note n) {
    getNoteList().add(n);
    return true;
  }

  /** removes an Note object from the list of notes in this Parameter object
   * @param Note to be removed
   * @return true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return removeFromList(what, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }



   /** removes an Note object from the list of notes in this Parameter object
   * @param index - list index number of the Note to be removed
   * @return true on success, false on failure
   */
  public boolean removeNote(int index) {
     return removeFromList(index, getNoteList(), NOTELIST_XML_ATTRIBUTE_NAME);
  }

  /** Insert an Unit object into the L<Units> object
   * held in this object.
   * @param Unit to be added
   * @return an Unit object if successfull, null if not.
   */
  public boolean addUnit(Unit unit) {
    return  getUnits().addUnit(unit);
  }

  /** Remove an Unit object from the Units object held in
   * this object
   * @param Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    return getUnits().removeUnit(what);
  }

  /** Remove an Unit object from the Units object held in
   * this object
   * @param list index number
   * @return true if successful, false if not
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
          cloneObj.valueGroupOwnedHash.add(((Group)iter.next()).clone());
        }
      }
    }
    return cloneObj;
  }

  // 
  // Protected Methods 
  // 
 
  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    super.init();

    classXDFNodeName = "parameter";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, NOTELIST_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, VALUELIST_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, DATAFORMAT_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, UNITS_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, CONVERSION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial value
    attribHash.put(NOTELIST_XML_ATTRIBUTE_NAME, new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put(VALUELIST_XML_ATTRIBUTE_NAME, new Attribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new Attribute(new Units(), Constants.OBJECT_TYPE));
    attribHash.put(CONVERSION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));
    attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));
    attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));  //double check k.z.
    attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(NAME_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));


     // set the valueList field for BaseObjectWithValueList
     valueListXMLItemName = VALUELIST_XML_ATTRIBUTE_NAME;

//     this.setUnits(new Unitless());

  };

   //
   // Private Methods
   //

   /* insert an Value object into the valueList
    * @param v - Value to be added
    * @return an Value object
    */
   private boolean internalAddValue(Value valueObj) {

      if (valueObj == null) {
         Log.warn("in Parameter, addValue(), the Value passed in is null");
         return false;
      }

      getValueList().add(valueObj);
      return true;
   }

}

