
// XDF Axis Class
// CVS $Id$

// Axis.java Copyright (C) 2000 Brian Thomas,
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;


/** There must be one axis (or fieldAxis) for every dimension
 *  in the datacube. There are n indices for every axis (n>= 1).
 * Each axis declaration defines the values of ALL the indices
 * along that dimension. Values of the indices in that axis need
 * not follow any algorthm for progression BUT each must be unique
 * within the axis. A unit may be associated with the axis.
 * Note that the unit specified for the axis indices is not the
 * same as the unit of the data held within the data cube.
   @version $Revision$
*/

public class Axis extends BaseObjectWithXMLElementsAndValueList
implements AxisInterface 
{

   //
   // Fields
   //

   /* XML attribute names */
   private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
   private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("axisId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("axisIdRef");
   private static final String ALIGN_XML_ATTRIBUTE_NAME = new String("align");
   private static final String CONVERSION_XML_ATTRIBUTE_NAME = new String("conversion");
   private static final String DATAFORMAT_XML_ATTRIBUTE_NAME = new String("dataFormat");
   private static final String UNITS_XML_ATTRIBUTE_NAME = new String("axisUnits");
   private static final String VALUELIST_XML_ATTRIBUTE_NAME = new String("valueList");

   private int length;

   private Array parentArray;

   /** This field stores object references to those value group objects
      to which this axis object belongs
    */
   private Set valueGroupOwnedHash = Collections.synchronizedSet(new HashSet());


  //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Axis ()
  {
    init();

  }


  /** 
   *  Create an Axis with a desired dimension. 
   *  This axis is populated with the correct number of axis Values,
   *  with each axis Value being set to an integer equal to the internal index.
   *  Axis values can be later reset using setAxisValue () or setValueList().
   */
  public Axis (int dimension)
  {
     init();
     ValueListAlgorithm newValueListObj = new ValueListAlgorithm(0,1,dimension);
     setAxisValueList(newValueListObj);
  }


  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Axis ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  /** A combination of the prior constructors.
   */
  public Axis ( Hashtable InitXDFAttributeTable, int dimension )
  {
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);

      ValueListAlgorithm newValueListObj = new ValueListAlgorithm(0,1,dimension);
      setAxisValueList(newValueListObj);
  }

  //
  //Get/Set Methods
  //

  /** set the *name* attribute
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

   /** set the *description* attribute
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

   /** 
    *  Set how to convert values of the data in this axis. 
    */
   public void setConversion(Conversion value)
   {
        ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).setAttribValue(value);
   }

  /**
   *  Get how to convert values of the data in this axis. 
   * @return the current *dataFormat* object
   */
  public Conversion getConversion()
  {
     return (Conversion) ((Attribute) attribHash.get(CONVERSION_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Sets the data format for the label of this axis 
  */
  public void setLabelDataFormat(DataFormat dataFormat)
  {
     ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).setAttribValue(dataFormat);
  }

  /**
   * Gets the data format for the label of this axis.
   * @return the current *dataFormat* object
   */
  public DataFormat getLabelDataFormat()
  {
     return (DataFormat) ((Attribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *axisUnits* attribute
   */
  public void setAxisUnits (Units units)
  {
     ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).setAttribValue(units);
  }

  /**
   * @return the current *axisUnits* attribute
   */
  public Units getAxisUnits()
  {
      return (Units) ((Attribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *align* attribute
   */
  public void setAlign(String strName)
  {
      ((Attribute) attribHash.get(ALIGN_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
  }

   /**
   * @return the current *align* attribute
   */
  public String getAlign()
  {
     return (String) ((Attribute) attribHash.get(ALIGN_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


  /** set the *axisId* attribute
   */
  public void setAxisId (String strAxisId)
  {
     ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strAxisId);
  }

   /**
   * @return the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *axisIdRef* attribute
   */
  public void setAxisIdRef (String strAxisIdRef)
  {
     ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strAxisIdRef);

  }

   /**
   * @return the current *axisIdRef* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Set the values contained in this Axis from the passed List. The passed
      list may contain either Value or UnitDirection objects.
      @deprecated You should use add/remove methods instead.
   */
  public void setValueList(List listOfAxisValues) {
     length = 0;
     ((Attribute) attribHash.get(VALUELIST_XML_ATTRIBUTE_NAME)).setAttribValue(Collections.synchronizedList(new ArrayList()));
     Iterator iter = listOfAxisValues.iterator();
     while (iter.hasNext()) {
        Object value = iter.next();
        if (value instanceof Value) {
           addAxisValue((Value) value);
        } else {
           addAxisUnitDirection((UnitDirection) value);
        }
     }
  }

  /**  Set the values contained in this Axis from the passed ValueList. 
       The passed values are construed as axisValues (e.g. NOT UnitDirection 
       objects).
       @deprecated You should use add/remove methods instead.
    */ 
   public void setAxisValueList (ValueListInterface valueListObj)
   {
      resetAxisValues();
      addAxisValueList(valueListObj);
   }


  /** The valueList is the list of axisValues or UnitDirection objects held within this Axis. 
   * @return the current *valueList* attribute
   */
  public List getValueList() {
    return (List) ((Attribute) attribHash.get(VALUELIST_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the ValueGroupOwnedHash
  */
  public void setValueGroupOwnedHash(Set valueGroup)
  {
    valueGroupOwnedHash = valueGroup;
  }

  /** get the ValueGroupOwnedHash
  */
  public Set getValueGroupOwnedHash()
  {
    return valueGroupOwnedHash;
  }

  /**
       @return a list of units describing this axis. 
    */
   public List getUnits()
   {
      return (List) getAxisUnits().getUnits();
   }


  /** Get the length of this axis (eg number of axis value objects)
   *
   */
  public int getLength() {
    return length;
  }

  /** set the parentArray
  */
  public void setParentArray (Array parent)
  {
     parentArray = parent;
  }

  //
  // Other PUBLIC methods
  //

   /** Resets the axis value list to be empty and the axis length to 0. 
    */
   public void resetAxisValues () {
      setValueList(new ArrayList());
   }

   public boolean addAxisValueList (ValueListInterface valueListObj)
   {

      List values = valueListObj.getValues();

      // do we have any new values?
      if (values.size() > 0) {

         addValueListObj(valueListObj);

         // append in new values to Parameter obj 
         Iterator iter = values.iterator();
         while (iter.hasNext()) {
            Value thisValue = ((Value) iter.next());
            internalAddAxisValue(thisValue);
         }

         return true;

      } else {

         // safety, needed?
         hasValueListCompactDescription = false;
         Log.warnln("Warning: no Values appended, ValueList empty. Parameter unchanged.");
         return false;

      }

   }

  /** Add a Value object to this axis. Note that adding an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those Values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however). 
   * @param valueObj - Value object to be added
   * @return true on success, false on failure
   */

   public boolean addAxisValue(Value valueObj) {

      if (internalAddAxisValue(valueObj)) {

         // no compact description allowed now 
         resetValueListObjects();
         return true;
      }
      return false;

   }

   /**Set the value of this axis at the given index.
     Note that setting an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those Values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
    *  @param index, valueObj
    */

    public void setAxisValue(int index, Value valueObj) {

      if (valueObj == null) {
        Log.error("in Axis, setAxisValue(), the Value passed in is null");
        return;
      }

     // no compact description allowed now 
      resetValueListObjects();

      List values = getValueList();
      if (index < 0 || index >length) {  //invalid index
        Log.error("in Axis, setAxisValue(), the index is out of range");
        return;
      }

      if (index == length) {  //add value to the end of valueList
        Log.warn("the index is actually the current valueList length, bumping the length by 1");
        length++;
        values.add(valueObj);
      }
      else {  //replace the value
         values.remove(index);
         values.add(index, valueObj);
      }

    }


    /** Set the value of this axis at the given index.
  Note that setting an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
    * @param UnitDirectionObj, index
    */

    public void setAxisValue(int index, UnitDirection unitDirectionObj) {

      if (unitDirectionObj== null) {
        Log.error("in Axis, setAxisValue(), the unitDirectionObj passed in is null");
        return;
      }
      List values = getValueList();
      if (index < 0 || index >length) {  //invalid index
        Log.error("in Axis, setAxisValue(), the index is out of range");
        return;
      }

      // no compact description allowed now 
      resetValueListObjects();

      if (index == length) {  //add value to the end of valueList
        Log.warn("the index is actually the current valueList length, bumping the length by 1");
        length++;
        values.add(unitDirectionObj);
      }
      else {  //replace the value
         values.remove(index);
         values.add(index, unitDirectionObj);
      }

    }

  /**Add an UnitDirection object to this axis.
  Note that setting an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
   * @param unitDirectionObj - UnitDirection to be added
   * @return true on success, false on failure
   */

   public boolean addAxisUnitDirection (UnitDirection unitDirectionObj) {

      if (unitDirectionObj == null) {
         Log.warn("in Axis, addAxisUnitDirection(), the UnitDirection object passed in is null");
         return false;
      }

      // no compact description allowed now 
      resetValueListObjects();

      getValueList().add(unitDirectionObj);
      length++;  //bump up length

      // inform parent array of the change
      if ( parentArray != null) {
         parentArray.needToUpdateLongArrayMult = true;
      }

      return true;
   }

   /**removes a Value object from the list of values in this Axis object. 
  Note that removing an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
   * @param what - Value object to be removed
   * @return true on success, false on failure
   */
   public boolean removeAxisValue (Value what) {
      boolean isRemoveSuccess = removeFromList (what, getValueList(), VALUELIST_XML_ATTRIBUTE_NAME);
      if (isRemoveSuccess) { //decrement length if removal successful
        length--;
        // no compact description allowed now 
        resetValueListObjects();
      }

      return isRemoveSuccess;
  }



  /**removes an Value from the list of values in this Axis object. 
  Note that removing an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
   * @param index - list index number of the Value object to be removed
   * @return true on success, false on failure
   */
  public boolean removeAxisValue (int index) {
     boolean isRemoveSuccess = removeFromList (index, getValueList(), VALUELIST_XML_ATTRIBUTE_NAME);
      if (isRemoveSuccess) //decrement length if removal successful
      {
        length--;
        // no compact description allowed now 
        resetValueListObjects();
      }

      return isRemoveSuccess;
  }

  /**removes an UnitDirection from the list of values in this Axis object. 
  Note that removing an axisValue will
   * void any compact (valueList) description of the axis values when 
   * toXMLOutputStream is called (e.g. those values formerly inserted via the
   * addAxisValueList or setAxisValueList methods; these prior axisValues *do* 
   * remain within the Axis however).
   * @param what - UnitDirection to be removed
   * @return true on success, false on failure
   */
   public boolean removeAxisValue (UnitDirection what) {
      boolean isRemoveSuccess = removeFromList (what, getValueList(), VALUELIST_XML_ATTRIBUTE_NAME);
      if (isRemoveSuccess)  //decrement length if removal successful
      {
        length--;
        // no compact description allowed now 
        resetValueListObjects();
      }

      return isRemoveSuccess;
  }

  /**
   * Returns the Value or XDF: UnitDirection object at the specified index
     along this axis object.
   */

   public Object getAxisValue(int index) {
      if (index< 0 || index >length-1) {
        Log.error("in Axis, getAxisValue(), the index passed in is out of range");
        return null;
      }
      return getValueList().get(index);
   }

   /**
    * This is a convenience method which returns all of the values of this axis.
    */
   public List getAxisValues() {
      return getValueList();
   }

   /**Insert an Unit object into the L<Units> object
   * held in this object.
   * @param unit - Unit to be added
   * @return an Unit object
   */
  public boolean addUnit(Unit unit) {
    return  getAxisUnits().addUnit(unit);
  }

  /** Remove an Unit object from the Units object held in
   * this object
   * @param what - Unit to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    return getAxisUnits().removeUnit(what);
  }

  /**Remove an Unit object from the Units object held in
   * this object
   * @param index - list index number of the Unit object to be removed
   * @return true if successful, false if not
   */
  public boolean removeUnit(int index) {
    return getAxisUnits().removeUnit(index);
  }

  /**Insert a ValueGroup object into this object to group the axisValues.
   * @Value: group - ValueGroup to be added
   * @returnan a ValueGroup object reference
   */
  public boolean addValueGroup (ValueGroup group) {
      //add the group to the groupOwnedHash
      valueGroupOwnedHash.add(group);
      return true;

  }

  /**remove a ValueGroup object from this object
   * @Value: group - ValueGroup to be removed
   * @return true on success, false on failure
   */
  public boolean removeValueGroup(ValueGroup group) {
    return valueGroupOwnedHash.remove(group);
  }

  /**Return the axis index for the given (scalar) value.
   * Does not currently work for unitDirection objects that reside
   * on an axis.
   * @return index if successful,
   *          -1 if it cant find an index for the given value.
   *
   */

    public int getIndexFromAxisValue(Value valueObj) {
     List values = getValueList();
      int size = values.size();
      for (int i = 0; i< size; i++){
        if (((Value) values.get(i)).getValue().equals(valueObj.getValue())) //if found
          return i;

      }

      //not found
      return -1;

    }

  /**deep copy of the Axis object
   */
  public Object clone() throws CloneNotSupportedException {
    Axis cloneObj = (Axis) super.clone();

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

  /** Special method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
  {

    super.init();

     classXDFNodeName = "axis";

     // order matters! these are in *reverse* order of their
     // occurence in the XDF DTD
     attribOrder.add(0, VALUELIST_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, UNITS_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DATAFORMAT_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, CONVERSION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, ALIGN_XML_ATTRIBUTE_NAME);  //not sure what it is???
     attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);

     //set up the attribute hashtable key with the default initial values 
     attribHash.put(VALUELIST_XML_ATTRIBUTE_NAME, 
        new Attribute(  Collections.synchronizedList (new ArrayList (
                           Specification.getInstance().getDefaultDataArraySize())), Constants.LIST_TYPE
                        )
                  );
     attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(ALIGN_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));  //double check???

     //set up the axisUnits attribute
     Units unitsObj = new Units();
     attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new Attribute(unitsObj, Constants.OBJECT_TYPE));
     unitsObj.setXDFNodeName(UNITS_XML_ATTRIBUTE_NAME);

     attribHash.put(CONVERSION_XML_ATTRIBUTE_NAME,new Attribute(null, Constants.OBJECT_TYPE));

     attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME,new Attribute(null, Constants.OBJECT_TYPE));
// NO. leave this null so that it wont print out, we assume implicitly its string in the Sax parser 
//     this.setLabelDataFormat(new StringDataFormat()); // default is string with default length

     attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
     attribHash.put(NAME_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));

     // set the initial length;
     length = 0;

     // set the valueList field for BaseObjectWithXMLElementsAndValueList
     valueListXMLItemName = VALUELIST_XML_ATTRIBUTE_NAME;

  }

   //
   // Private Methods
   //

   // this is needed by add/set AxisValueList to prevent compact valueList
   // description from being reset. 
   private boolean internalAddAxisValue(Value valueObj) {

      if (valueObj == null) {
         Log.warn("in Axis, addAxisValue(), the Value passed in is null");
         return false;
      }

      // ok to add
      getValueList().add(valueObj);
      length++;  //bump up length

      // inform parent array of the change
      if ( parentArray != null) {
         parentArray.needToUpdateLongArrayMult = true;
      }

      return true;

   }



}  //end of Axis class

