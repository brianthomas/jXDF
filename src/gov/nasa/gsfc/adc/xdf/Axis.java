// XDF Axis Class
// CVS $Id$

// Axis.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

package gov.nasa.gsfc.adc.xdf;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;


/** There must be one axis (or fieldAxis) for every dimension
 *  in the datacube. There are n indices for every axis (n>= 1).
 * Each axis declaration defines the values of ALL the indices
 * along that dimension. Values of the indices in that axis need
 * not follow any algorthm for progression BUT each must be unique
 * within the axis. A unit may be assocated with the axis.
 * Note that the unit specified for the axis indices is not the
 * same as the unit of the data held within the data cube.
   @version $Revision$
*/

public class Axis extends BaseObject implements AxisInterface {

 //
 //Fields
 //

  protected int length;

  /** This field stores object references to those value group objects
   * to which this axis object belongs
  */
  protected Set valueGroupOwnedHash = Collections.synchronizedSet(new HashSet());

 //
  // Constructor and related methods
  //

  /** The no argument constructor.
   */
  public Axis ()
  {
    init();

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


 /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "axis";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"valueList");
    attribOrder.add(0,"axisUnits");
    attribOrder.add(0,"axisDatatype");
    attribOrder.add(0,"align");  //not sure what it is???
    attribOrder.add(0,"axisIdRef");
    attribOrder.add(0,"axisId");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

     //set up the attribute hashtable key with the default initial value

     //set the minimum array size(essentially the size of the axis)
    attribHash.put("valueList", new XMLAttribute(Collections.synchronizedList(new ArrayList(super.sDefaultDataArraySize)), Constants.LIST_TYPE));
    attribHash.put("axisIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("axisId", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("align", new XMLAttribute(null, Constants.STRING_TYPE));  //double check???

    //set up the axisUnits attribute
    Units unitsObj = new Units();
    attribHash.put("axisUnits", new XMLAttribute(unitsObj, Constants.OBJECT_TYPE));
    unitsObj.setXDFNodeName("axisUnits");

    attribHash.put("axisDatatype",new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

    length = 0;

  }


  //
  //Get/Set Methods
  //

  /**setName: set the *name* attribute
   * @return: the current *name* attribute
   */
  public String setName (String strName)
  {
    return (String) ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);

  }

   /**getName
   * @return: the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

   /**setDescription: set the *description* attribute
   * @return: the current *description* attribute
   */
  public String setDescription (String strDesc)
  {
    return (String) ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);

  }

  /**getDescription
   * @return: the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  /**setAxisDataType: set the *axisDatatype* attribute
   * @return: the current *axisDatatype* attribute
   */
  public String setDatatype(String strDatatype)
  {
    if (Utility.isValidDatatype(strDatatype))
      return (String) ((XMLAttribute) attribHash.get("axisDatatype")).setAttribValue(strDatatype);
    else
      return null;
  }

  /**getAxisDatatype
   * @return: the current *axisDatatype* attribute
   */
  public String getDatatype()
  {
    return (String) ((XMLAttribute) attribHash.get("axisDatatype")).getAttribValue();
  }

  /**setAxisUnits: set the *axisUnits* attribute
   * @return: the current *axisUnits* attribute
   */
  public Units setAxisUnits (Units units)
  {
    return (Units) ((XMLAttribute) attribHash.get("axisUnits")).setAttribValue(units);
  }

  /**getAxisUnits
   * @return: the current *axisUnits* attribute
   */
  public Units getAxisUnits()
  {
    return (Units) ((XMLAttribute) attribHash.get("axisUnits")).getAttribValue();
  }

  /**setAxisId: set the *axisId* attribute
   * @return: the current *axisId* attribute
   */
  public String setAxisId (String strAxisId)
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).setAttribValue(strAxisId);

  }

   /**getAxisId
   * @return: the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).getAttribValue();
  }

  /**setAxisIdRef: set the *axisIdRef* attribute
   * @return: the current *axisIdRef* attribute
   */
  public String setAxisIdRef (String strAxisIdRef)
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).setAttribValue(strAxisIdRef);

  }

   /**getAxisIdRef
   * @return: the current *axisIdRef* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).getAttribValue();
  }

  /**setValueList: set the *valueList* attribute
   * @return: the current *valueList* attribute
   */
  public List setValueList(List value) {
    return (List)((XMLAttribute) attribHash.get("valueList")).setAttribValue(value);
  }

  /**getValueList
   * @return: the current *valueList* attribute
   */
  public List getValueList() {
    return (List) ((XMLAttribute) attribHash.get("valueList")).getAttribValue();
  }

  /** setValueGroupOwnedHash
  */
  public Set setValueGroupOwnedHash(Set valueGroup)
  {
    valueGroupOwnedHash = valueGroup;
    return valueGroupOwnedHash;
  }

  /** getValueGroupOwnedHash
  */
  public Set getValueGroupOwnedHash()
  {
    return valueGroupOwnedHash;
  }

  /**getLength: Get the length of this axis (eg number of axis value objects)
   *
   */
  public int getLength() {
    return length;
  }




  //
  //Other PUBLIC methods
  //

  /** addAxisValue: Add an XDF::Value object to this axis.
   * @param: ref to Value object
   * @return: ref to Value object if successful, null if not
   */

   public Value addAxisValue(Value valueObj) {
    if (valueObj == null) {
      Log.warn("in Axis, addAxisValue(), the Value passed in is null");
      return null;
    }
    getValueList().add(valueObj);
    length++;  //bump up length
    return valueObj;
   }



   /**setAxisValue: Set the value of this axis at the given index.
    *  @param: Value, index
    * @return: Value ref if successful, null if not
    */

    public Value setAxisValue(int index, Value valueObj) {
      if (valueObj == null) {
        Log.error("in Axis, setAxisValue(), the Value passed in is null");
        return null;
      }
      List values = getValueList();
      if (index < 0 || index >length) {  //invalid index
        Log.error("in Axis, setAxisValue(), the index is out of range");
        return null;
      }

      if (index == length) {  //add value to the end of valueList
        Log.warn("the index is actually the current valueList length, bumping the length by 1");
        length++;
        values.add(valueObj);
        return valueObj;
      }
      else {  //replace the value
         values.remove(index);
         values.add(index, valueObj);
         return valueObj;
      }

    }


    /**setAxisValue: Set the value of this axis at the given index.
    * @param: UnitDirection, index
    * @return: UnitDirection ref if successful, null if not
    */

    public UnitDirection setAxisValue(int index, UnitDirection unitDirectionObj) {
      if (unitDirectionObj== null) {
        Log.error("in Axis, setAxisValue(), the unitDirectionObj passed in is null");
        return null;
      }
      List values = getValueList();
      if (index < 0 || index >length) {  //invalid index
        Log.error("in Axis, setAxisValue(), the index is out of range");
        return null;
      }

      if (index == length) {  //add value to the end of valueList
        Log.warn("the index is actually the current valueList length, bumping the length by 1");
        length++;
        values.add(unitDirectionObj);
        return unitDirectionObj;
      }
      else {  //replace the value
         values.remove(index);
         values.add(index, unitDirectionObj);
         return unitDirectionObj;
      }

    }

  /** addAxisUnitDirection: Add an XDF::UnitDirection object to this axis.
   * @param: ref to UnitDirection object
   * @return: ref to UnitDirection object if successful, null if not
   */

   public UnitDirection addAxisUnitDirectoin(UnitDirection unitDirectionObj) {
    if (unitDirectionObj == null) {
      Log.warn("in Axis, addAxisUnitDirection(), the UnitDirection object passed in is null");
      return null;
    }
    getValueList().add(unitDirectionObj);
    length++;  //bump up length
    return unitDirectionObj;
   }

   /**removeAxisValue: removes an XDF::Value from the list of values in this Axis object
   * @param: Value to be removed
   * @return: true on success, false on failure
   */
   public boolean removeAxisValue(Value what) {
      boolean isRemoveSuccess = removeFromList(what, getValueList(), "valueList");
      if (isRemoveSuccess)  //decrement length if removal successful
        length--;
      return isRemoveSuccess;
  }



  /**removeAxisValue: removes an XDF::Value from the list of values in this Axis object
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeAxisValue(int index) {
     boolean isRemoveSuccess = removeFromList(index, getValueList(), "valueList");
      if (isRemoveSuccess) //decrement length if removal successful
        length--;
      return isRemoveSuccess;
  }

  /**removeAxisValue: removes an XDF::UnitDirection from the list of values in this Axis object
   * @param: UnitDirection to be removed
   * @return: true on success, false on failure
   */
   public boolean removeAxisValue(UnitDirection what) {
      boolean isRemoveSuccess = removeFromList(what, getValueList(), "valueList");
      if (isRemoveSuccess)  //decrement length if removal successful
        length--;
      return isRemoveSuccess;
  }

  /**getAxisValue
   * Returns the axis XDF::Value or XDF: UnitDirection object at the specified index.
   */

   public Object getAxisValue(int index) {
      if (index< 0 || index >length-1) {
        Log.error("in Axis, getAxisValue(), the index passed in is out of range");
        return null;
      }
      return getValueList().get(index);
   }

   /**getAxisValues
    * This is a convenience method which returns all of the values (as strings) on this axis.
    */
   public String getAxisValues() {
    Log.error("in Axis, getAxisValues(), empty, returning null");
    return null;
   }

   /**addUnit: Insert an XDF::Unit object into the L<XDF::Units> object
   * held in this object.
   * @param: Unit to be added
   * @return: an XDF::Unit object if successfull, null if not.
   */
  public Unit addUnit(Unit unit) {
    if (unit == null) {
      Log.warn("in Parameter.addUnit(), the Unit passed in is null");
      return null;
    }
    return  getAxisUnits().addUnit(unit);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: Unit to be removed
   * @return: true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    return getAxisUnits().removeUnit(what);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: list index number
   * @return: true if successful, false if not
   */
  public boolean removeUnit(int index) {
    return getAxisUnits().removeUnit(index);
  }

  /**addValueGroup: Insert a ValueGroup object into this object to group the axisValues.
   * @Value: ValueGroup to be added
   * @return:an XDF::ValueGroup object reference on success, null on failure.
   */
  public ValueGroup addValueGroup (ValueGroup group) {
    if (group !=null) {
      //add the group to the groupOwnedHash
      valueGroupOwnedHash.add(group);
      return group;
    }
    else {
      Log.warn("in Axis,addValueGroup(). ValueGroup passed in is null");
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

  /**getIndexFromValue:  Return the axis index for the given (scalar) value.
   * Does not currently work for unitDirection objects that reside
   * on an axis.
   * @return: index if successful,
   *          -1 if it cant find an index for the given value.
   *
   */

    public int getIndexFromAxisValue(Value valueObj) {
      if (valueObj == null) {
        Log.error("in Axis, getIndexFromAxisValue, Value passed in is null, returning -1");
        return -1;
      }

      List values = getValueList();
      for (int i = 0; i< values.size(); i++){
        if (((Value) values.get(i)).getValue().equals(valueObj.getValue())) //if found
          return i;

      }

      //not found
      return -1;

    }

  public Object clone() throws CloneNotSupportedException {
    Axis cloneObj = (Axis) super.clone();

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

}  //end of Axis class

/* Modification History:
 *
 * $Log$
 * Revision 1.11  2000/11/08 19:24:56  thomas
 * Trimmed import statements to just needed classes. -b.t.
 *
 * Revision 1.10  2000/11/06 21:17:42  kelly
 * added clone()  -k.z.
 *
 * Revision 1.9  2000/11/02 17:56:18  kelly
 * minor fix
 *
 * Revision 1.8  2000/10/31 21:48:46  kelly
 * minor fix
 *
 * Revision 1.7  2000/10/30 18:19:02  kelly
 * Axis now implements AxisInterface
 *
 * Revision 1.6  2000/10/26 20:09:20  kelly
 * dataType as String, fixed its get/set methods -k.z.
 *
 * Revision 1.5  2000/10/25 21:20:29  thomas
 * Bug fix. AxisDataType is really string. -b.t.
 *
 * Revision 1.4  2000/10/24 21:33:29  thomas
 * Made the class Cloneable. -b.t.
 *
 * Revision 1.3  2000/10/11 18:43:32  kelly
 * finished most of the class, but getAxisValues() is not implemented,
 * getIndexFromValue() doesnt work for UnitDirection --k.z.
 *
 */
