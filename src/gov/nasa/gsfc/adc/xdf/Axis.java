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
 * within the axis. A unit may be associated with the axis.
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


  //
  //Get/Set Methods
  //

  /** set the *name* attribute
   */
  public void setName (String strName)
  {
     ((XMLAttribute) attribHash.get("name")).setAttribValue(strName);
  }

   /**
   * @return the current *name* attribute
   */
  public String getName()
  {
    return (String) ((XMLAttribute) attribHash.get("name")).getAttribValue();
  }

   /** set the *description* attribute
   */
  public void setDescription (String strDesc)
  {
     ((XMLAttribute) attribHash.get("description")).setAttribValue(strDesc);

  }

  /**
   * @return the current *description* attribute
   */
  public String getDescription() {
    return (String) ((XMLAttribute) attribHash.get("description")).getAttribValue();
  }

  /** set the *axisDatatype* attribute
   */
  public void setDatatype(String strDatatype)
  {
    if (Utility.isValidDatatype(strDatatype))
      ((XMLAttribute) attribHash.get("axisDatatype")).setAttribValue(strDatatype);
    else
      Log.warnln("Datatype is not valid, ignoring set request.");
  }

  /**
   * @return the current *axisDatatype* attribute
   */
  public String getDatatype()
  {
    return (String) ((XMLAttribute) attribHash.get("axisDatatype")).getAttribValue();
  }

  /** set the *axisUnits* attribute
   */
  public void setAxisUnits (Units units)
  {
     ((XMLAttribute) attribHash.get("axisUnits")).setAttribValue(units);
  }

  /**
   * @return the current *axisUnits* attribute
   */
  public Units getAxisUnits()
  {
    return (Units) ((XMLAttribute) attribHash.get("axisUnits")).getAttribValue();
  }

  /** set the *axisId* attribute
   */
  public void setAxisId (String strAxisId)
  {
     ((XMLAttribute) attribHash.get("axisId")).setAttribValue(strAxisId);

  }

   /**
   * @return the current *axisId* attribute
   */
  public String getAxisId()
  {
    return (String) ((XMLAttribute) attribHash.get("axisId")).getAttribValue();
  }

  /** set the *axisIdRef* attribute
   */
  public void setAxisIdRef (String strAxisIdRef)
  {
     ((XMLAttribute) attribHash.get("axisIdRef")).setAttribValue(strAxisIdRef);

  }

   /**
   * @return the current *axisIdRef* attribute
   */
  public String getAxisIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("axisIdRef")).getAttribValue();
  }

  /** set the *valueList* attribute
   */
  public void setValueList(List value) {
     ((XMLAttribute) attribHash.get("valueList")).setAttribValue(value);
  }

  /**
   * @return the current *valueList* attribute
   */
  public List getValueList() {
    return (List) ((XMLAttribute) attribHash.get("valueList")).getAttribValue();
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

  /** Get the length of this axis (eg number of axis value objects)
   *
   */
  public int getLength() {
    return length;
  }




  //
  //Other PUBLIC methods
  //

  /** Add a Value object to this axis.
   * @param valueObj - Value object to be added
   * @return ref to Value object if successful, null if not
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



   /**Set the value of this axis at the given index.
    *  @param index, valueObj
    */

    public void setAxisValue(int index, Value valueObj) {

      if (valueObj == null) {
        Log.error("in Axis, setAxisValue(), the Value passed in is null");
        return;
      }

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
   * @param unitDirectionObj - UnitDirection to be added
   * @return ref to UnitDirection object if successful, null if not
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

   /**removes a Value object from the list of values in this Axis object
   * @param what - Value to be removed
   * @return true on success, false on failure
   */
   public boolean removeAxisValue(Value what) {
      boolean isRemoveSuccess = removeFromList(what, getValueList(), "valueList");
      if (isRemoveSuccess)  //decrement length if removal successful
        length--;
      return isRemoveSuccess;
  }



  /**removes an Value from the list of values in this Axis object
   * @param index - list index number of the Value object to be removed
   * @return true on success, false on failure
   */
  public boolean removeAxisValue(int index) {
     boolean isRemoveSuccess = removeFromList(index, getValueList(), "valueList");
      if (isRemoveSuccess) //decrement length if removal successful
        length--;
      return isRemoveSuccess;
  }

  /**removes an UnitDirection from the list of values in this Axis object
   * @param what - UnitDirection to be removed
   * @return true on success, false on failure
   */
   public boolean removeAxisValue(UnitDirection what) {
      boolean isRemoveSuccess = removeFromList(what, getValueList(), "valueList");
      if (isRemoveSuccess)  //decrement length if removal successful
        length--;
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
    * This is a convenience method which returns all of the values (as strings) on this axis.
    */
   public String getAxisValues() {
    Log.error("in Axis, getAxisValues(), empty, returning null");
    return null;
   }

   /**Insert an Unit object into the L<Units> object
   * held in this object.
   * @param unit - Unit to be added
   * @return an Unit object
   */
  public Unit addUnit(Unit unit) {
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
  public ValueGroup addValueGroup (ValueGroup group) {
      //add the group to the groupOwnedHash
      valueGroupOwnedHash.add(group);
      return group;

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

  /** Special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  protected void init()
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
    attribHash.put("valueList", new XMLAttribute(Collections.synchronizedList(new ArrayList(Specification.getInstance().getDefaultDataArraySize())), Constants.LIST_TYPE));
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
 
}  //end of Axis class

/* Modification History:
 *
 * $Log$
 * Revision 1.16  2000/11/27 16:57:44  thomas
 * Made init method protected so that extending
 * Dataformats may make use of them. -b.t.
 *
 * Revision 1.15  2000/11/16 19:45:34  kelly
 * fixed documentation.
 *
 * Revision 1.13  2000/11/09 04:24:11  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.12  2000/11/08 22:30:12  thomas
 * Changed set methods to return void. -b.t.
 *
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
