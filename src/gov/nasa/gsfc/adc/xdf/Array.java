
// XDF Array Class
// CVS $Id$

// Array.java Copyright (C) 2000 Brian Thomas,
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
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


 /** DESCRIPTION
  *  XDF is the eXtensible Data Structure, which is an XML format designed to contain n-dimensional
  * scientific/mathematical data. XDF::Array is the basic container for the n-dimensional array data.
  * It gives access to the array data and its descriptors (such as the array axii, associated
  * parameters, notes, etc).
  *
  *
  * Here is a diagram showing the inter-relations between these components
  * of the XDF::Array in a 2-dimensional dataset with no fields.
  *
  *
  *    axisValue -----> "9" "8" "7" "6" "5" "A"  .   .  "?"
  *    axisIndex ----->  0   1   2   3   4   5   .   .   n
  *
  *                      |   |   |   |   |   |   |   |   |
  *    axisIndex--\      |   |   |   |   |   |   |   |   |
  *               |      |   |   |   |   |   |   |   |   |
  *    axisValue  |      V   V   V   V   V   V   V   V   V
  *        |      |
  *        V      V      |   |   |   |   |   |   |   |   |
  *      "star 1" 0 --> -====================================> axis 0
  *      "star 2" 1 --> -|          8.1
  *      "star 3" 2 --> -|
  *      "star 4" 3 --> -|
  *      "star 5" 4 --> -|
  *        "star 6" 5 --> -|       7
  *         .     . --> -|
  *         .     . --> -|
  *         .     . --> -|
  *       "??"    m --> -|
  *                      |
  *                      v
  *                    axis 1
  *
  *
  */


  /**attribute description:
   * name--
   * The STRING description (short name) of this Array.
   * description--
   * scalar string description (long name) of this Array.
   * arrayId
   * A scalar string holding the array Id of this Array.
   * axisList--
   * a SCALAR (ARRAY REF) of the list of Axis> objects held within this array.
   * paramList--
   * reference of the list of Parameter> objects held within in this Array.
   * notes --
   * reference of the object holding the list of Note objects held within this object.
   * dataCube
   * object ref of the DataCube> object which is a matrix holding the mathematical data
   * of this array.
   * dataFormat
   * object ref of the DataFormat> object.
   * units
   * object ref of the Units> object of this array. The XDF::Units object
   * is used to hold the XDF::Unit objects.
   * fieldAxis
   * OBJECT REF of the FieldAxis> object.
   */

  public class Array extends BaseObject{

  //
  //Fields
  //

  /** This field stores object references to those parameter group objects
   * to which this array object belongs
  */
  protected Set paramGroupOwnedHash = Collections.synchronizedSet(new HashSet());

  //the list of locators whose parentArray is this Array object
  protected List locators = new Vector();
  protected boolean hasFieldAxis = false;

  //
  // Constructors
  //

  /** The no argument constructor.
   */
  public Array ()
  {
    // init the XML attributes (to defaults)
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public Array ( Hashtable InitXDFAttributeTable )
  {
    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set Methods
  //

  /**setName: set the *name* attribute
   * @return: the current *name* attribute
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

   /**setDescription: set the *description* attribute
   * @return: the current *description* attribute
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

  /**setParamList: set the *paramList* attribute
   * @return: the current *paramList* attribute
   */
  public void setParamList(List param) {
     ((XMLAttribute) attribHash.get("paramList")).setAttribValue(param);
  }

  /**getParamList
   * @return: the current *paramList* attribute
   */
  public List getParamList() {
    return (List) ((XMLAttribute) attribHash.get("paramList")).getAttribValue();
  }

   /**setUnits: set the *units* attribute
   * @return: the current *units* attribute
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

  /**setDataFormat: Sets the data format *type* for this array (an XDF::DataFormat object
   * is held in the attribute $obj->dataFormat, its type is accessible
   * Takes a SCALAR object reference as its argument. Allowed objects to pass
   * to this method include BinaryIntegerDataFormat>, BinaryFloatDataFormat>,
   * ExponentDataFormat>, FixedDataFormat>, IntegerDataFormat>,
   * or StringDataFormat>.
   * RETURNS an object reference
  */
  public void setDataFormat(DataFormat dataFormat)
  {
     ((XMLAttribute) attribHash.get("dataFormat")).setAttribValue(dataFormat);
  }

  /**getDataFormat
   * @return: the current *dataFormat* attribute
   */
  public DataFormat getDataFormat()
  {
     return (DataFormat) ((XMLAttribute) attribHash.get("dataFormat")).getAttribValue();
  }

  /** setNotesObject
   */
  public void setNotesObject (Notes notes)
  {
     ((XMLAttribute) attribHash.get("notes")).setAttribValue(notes);
  }

  /** getNotesObject
     @return: the current *Notes* attribute object
   */
  public Notes getNotesObject()
  {
     return (Notes) ((XMLAttribute) attribHash.get("notes")).getAttribValue();
  }

   /**setAxisList: set the *axisList* attribute
   * @return: the current *axisList* attribute
   */
  public void setAxisList(List axis) {
     ((XMLAttribute) attribHash.get("axisList")).setAttribValue(axis);
  }

  /**getAxisList
   * @return: the current *axisList* attribute
   */
  public List getAxisList() {
    return (List) ((XMLAttribute) attribHash.get("axisList")).getAttribValue();
  }

  /**setXMLDataIOStyle: set the *xmlDataIOStyle* attribute
   * note we have to nsure that _parentArray is properly updated
   * @return: the current *xmlDataIOStyle* attribute
   */
  public void setXMLDataIOStyle(XMLDataIOStyle xmlDataIOStyle)
  {

     if (xmlDataIOStyle == null)
     {
        Log.error("in Array.setXMLDataIOStyle(), param passed in is null, ");
        Log.errorln("xmlDataIOStyle attribute not updated");
        return; // bail
     }

     //set the parent array to this object
     xmlDataIOStyle.setParentArray(this);

     // set the xmlattribute
     ((XMLAttribute) attribHash.get("xmlDataIOStyle")).setAttribValue(xmlDataIOStyle);

  }

  /**getXMLDataIOStyle
   * @return: the current *xmlDataIOStyle* attribute
   */
  public XMLDataIOStyle getXMLDataIOStyle()
  {
    return (XMLDataIOStyle) ((XMLAttribute) attribHash.get("xmlDataIOStyle")).getAttribValue();
  }

  /* setDataCube: set the *dataCube* attribute
   * @return: the current *dataCube* attribute
   * double check, we should not allow user to set the datacube
   */
//
// Hurm.. This is a dangerous method. All sorts of array meta-data arent updated
// properly when this is used. Commenting it out for now. -b.t.
//

  protected void setDataCube(DataCube dataCube)
  {
     ((XMLAttribute) attribHash.get("dataCube")).setAttribValue(dataCube);
  }


  /** getDataCube
      @return: the current *DataCube* attribute
   */
  public DataCube getDataCube()
  {
     return (DataCube) ((XMLAttribute) attribHash.get("dataCube")).getAttribValue();
  }

   /** Set the *noteList* attribute
      @return: the current *noteList* attribute
    */
   public void setNoteList(List note) {
     ((XMLAttribute) attribHash.get("noteList")).setAttribValue(note);
   }

   /**getNoteList
      @return: the current *noteList* attribute
   */
   public List getNoteList() {
      return (List) ((XMLAttribute) attribHash.get("noteList")).getAttribValue();
   }

   /**getDimension: set the dimension of the DataCube> held within this Array.
   */
   public int getDimension() {
     return getDataCube().getDimension();
   }

   //
   // Other Public Methods
   //

   /**creatLocator: Create one instance of an Locator> object for this array.
    *
    */
   public Locator createLocator() {
      Locator locatorObj = new Locator(this);

      //add this locator to the list of locators this Array object monitors
      locators.add(locatorObj);
      return locatorObj;
   }

   /** addParamGroup: Insert an XDF::ParameterGroup object into this object.
   * @param: ParameterGroup to be added
   * @return:an XDF::ParameterGroup object reference on success, null on failure.
   */
  public ParameterGroup addParamGroup (ParameterGroup group) {
    if (group !=null) {
      //add the group to the groupOwnedHash
      paramGroupOwnedHash.add(group);
      return group;
    }
    else {
      Log.warn("in Array.addParamGroup(). ParameterGroup passed in is null");
      return null;
    }
  }

  /**removeParamGroup: Remove an XDF::ParameterGroup object from the hashset--paramGroupOwnedHash
   * @param: ParameterGroup to be removed
   * @return: true on success, false on failure
   */
  public boolean removeParamGroup(ParameterGroup group) {
    if (group == null) {
      Log.warn("in Array.removeParamGroup().  ParameterGroup passed in is null");
      return false;
    }
    return paramGroupOwnedHash.remove(group);
  }

   /** addAxis: insert an Axis object into the list of axes held by this object.
       @param: Axis to be added
       @return: an Axis object on success, null on failure
   */
   public Axis addAxis(Axis axis) {

     if (!canAddAxisObjToArray(axis)) //check if the axis can be added
        return null;

     getDataCube().incrementDimension(axis );  //increment the DataCube dimension by 1

     getAxisList().add(axis);

     //update the locators that is related to this Array object
     int stop = locators.size();
     for (int i = 0; i < stop; i++) {
       ((Locator) locators.get(i)).addAxis(axis);
     }

     updateNotesLocationOrder(); // reset to the current order of the axes
     return axis;
  }

   /**removeAxis: removes an XDF::Axis object from axisList
   * @param: Axis to be removed
   * @return: true on success and decrement the dimension,
   *          false on failure and keep the dimension unchanged
   * double check the implication on datacube
   */
  public boolean removeAxis(Axis what) {
    boolean isRemoveSuccess = removeFromList(what, getAxisList(), "axisList");
    if (isRemoveSuccess)   //remove successful
       getDataCube().decrementDimension();   //decrease the dimension by 1
    return isRemoveSuccess;
  }

  /**removeAxis: removes an XDF::Axis object from AxisList
   * @param: list index number
   *  @return: true on success and decrement the dimension,
   *           false on failure and keep the dimension unchanged
   */
  public boolean removeAxis(int index) {
    boolean isRemoveSuccess = removeFromList(index, getAxisList(), "axisList");
    if (isRemoveSuccess)   //remove successful
       getDataCube().decrementDimension();   //decrease the dimension by 1
    return isRemoveSuccess;
  }

  /**addUnit: Insert an XDF::Unit object into the Units> object
   * held in this object.
   * @param: Unit to be added
   * @return: an XDF::Unit object if successfull, null if not.
   */
  public Unit addUnit(Unit unit) {
    if (unit == null) {
      Log.warn("in Array.addUnit(), the Unit passed in is null");
      return null;
    }
    Units u = getUnits();
    if (u == null) {
      u = new Units();
      setUnits(u);
    }
    return  u.addUnit(unit);
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: Unit to be removed
   * @return: true if successful, false if not
   */
  public boolean removeUnit(Unit what) {
    Units u = getUnits();
    if (u !=null) {
      if (u.getUnitList().size()==0)
        setUnits(null);
      return u.removeUnit(what);
    }
    else
      return false;
  }

  /**removeUnit: Remove an XDF::Unit object from the XDF::Units object held in
   * this object
   * @param: list index number
   * @return: true if successful, false if not
   */
  public boolean removeUnit(int index) {
   Units u = getUnits();
    if (u !=null) {
      if (u.getUnitList().size()==0)
        setUnits(null);
      return u.removeUnit(index);
    }
    else
      return false;
  }

  /** addParameter: insert an XDF::Parameter object into the paramList
   * @param: XDF::Parameter
   * @return: an XDF::Parameter object on success, null on failure
   */
  public Parameter addParameter(Parameter p) {
    if (p == null) {
      Log.warn("in Structure.addParameter, Parameter passed in is null");
      return null;
    }
    getParamList().add(p);
    return p;
  }
  /**removeParameter: removes an XDF::Parameter object from paramList
   * @param: Parameter to be removed
   * @return: true on success, false on failure
   */
  public boolean removeParameter(Parameter what) {
    return  removeFromList(what, getParamList(), "paramList");
  }

  /**removeParameter: removes an XDF::Parameter object from paramList
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeParameter(int index) {
    return removeFromList(index, getParamList(), "paramList");
  }

  /**A convenience method [same as $ArrayObj->dataCube()->maxDimensionIndex].
   * Returns a SCALAR ARRAY REF of SCALARS (non-negative INTEGERS) which are the maximum index
   * values along each dimension (FieldAxis and Axis objects).
   */
  public int[] getMaxDataIndices () {
    return getDataCube().getMaxDataIndex();

  }

 /** addNote: insert a Note object into the list of notes in this Array object
   * @param: Note
   * @return: a Note object on success, null on failure
   */
  public Note addNote(Note n) {
/*
    if (n == null) {
      Log.warn("in Array.addNote(), the Note passed in is null");
      return null;
    }
*/
    return getNotesObject().addNote(n);
    //return n;
  }

  /**removeNote: removes an XDF::Note object from the list of notes in this Array object
   * @param: Note to be removed
   * @return: true on success, false on failure
   */
   public boolean removeNote(Note what) {
     return (boolean) getNotesObject().removeNote(what); // removeFromList(what, getNoteList(), "noteList");
  }


   /**removeNote: removes an XDF::Note object from the list of notes in this Array object
   * @param: list index number
   * @return: true on success, false on failure
   */
  public boolean removeNote(int index) {
     return (boolean) getNotesObject().removeNote(index); // removeFromList(index, getNoteList(), "noteList");
  }

  /**getNotes: Convenience method which returns a list of the notes held by
   * this object.
   */
  public List getNotes() {
    return (List) getNotesObject().getNoteList();
  }

  /**appendData: Append the string value onto the requested datacell
   * (via DataCube> LOCATOR REF).
   */
  public String  appendData (Locator locator, String strValue) throws SetDataException{
    return getDataCube().appendData(locator, strValue);
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */


   public double  setData (Locator locator, double numValue) throws SetDataException {
    try {
      return getDataCube().setData(locator, numValue);
    }
    catch (SetDataException e) {
      throw e;
    }
  }

  public int  setData (Locator locator, int numValue) throws SetDataException {
    try {
      return getDataCube().setData(locator, numValue);
    }
    catch (SetDataException e) {
      throw e;
    }
  }


  /** setData: Set the SCALAR value of the requested datacell
   * (via DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
  public String setData (Locator locator, String strValue) throws SetDataException{
    try {
      return getDataCube().setData(locator, strValue);
    }
    catch (SetDataException e) {
      throw e;
    }
  }

  public String getStringData(Locator locator) throws NoDataException {
    try {
      return getDataCube().getStringData(locator);
    }
    catch (NoDataException e) {
      throw e;
    }
  }

  public int getIntData(Locator locator) throws NoDataException {
    try {
      return getDataCube().getIntData(locator);
    }
    catch (NoDataException e) {
      throw e;
    }
  }

  public double getDoubleData(Locator locator) throws NoDataException {
    try {
      return getDataCube().getDoubleData(locator);
    }
    catch (NoDataException e) {
      throw e;
    }
  }

  /** removeData : Remove the requested data from the indicated datacell
   *  (via DataCube LOCATOR REF) in the XDF::DataCube held in this Array.
   * (NOT CURRENTLY IMPLEMENTED).
   */

   public boolean  removeData (Locator locator) {
    return getDataCube().removeData(locator);
  }


  /**getDataFormatList: Get the dataFormatList for this array.
   *
   */
  public DataFormat[] getDataFormatList() {
    FieldAxis fieldAxis = getFieldAxis();
    if (fieldAxis !=null)
      return fieldAxis.getDataFormatList();
    else {  //not fieldAxis
      DataFormat[] d = new DataFormat[1];
      d[0] = getDataFormat();
      return d;
    }
  }
  /** addFieldAxis: A convenience method (same as setFieldAxis()).
   * Changes the FieldAxis object in this Array to the indicated one.
   * @return: reference to fieldAxis if successful, null if not.
   */
  public FieldAxis addFieldAxis(FieldAxis fieldAxis) {
    if (!canAddAxisObjToArray(fieldAxis))
      return null;

    if (getFieldAxis() !=null) {
      List axisList = getAxisList();
      axisList.remove(0);
      axisList.add(0, fieldAxis);  //replace the old fieldAxis with the new one
    }
    else {  //add fieldAxis and increment dimension
      getAxisList().add(0, fieldAxis);
      getDataCube().incrementDimension(fieldAxis);
    }
    hasFieldAxis = true;

    //update the locators that are related to this Array object
    int stop = locators.size();
     for (int i = 0; i < stop; i++) {
       ((Locator) locators.get(i)).addAxis(fieldAxis);
     }

    //array doenst hold a dataformat anymore
    //each field along the fieldAxis should have dataformat
    setDataFormat(null);
    return fieldAxis;
  }
  public FieldAxis getFieldAxis() {
    List axisList = getAxisList();
    if (axisList.size() == 0){  //empty axisList
      return null;
    }

    Object axisObj = axisList.get(0);

    if (axisObj instanceof FieldAxis)
      return (FieldAxis) axisObj;
    else
      return null;
  }

  public FieldAxis setFieldAxis(FieldAxis fieldAxis) {
    return addFieldAxis(fieldAxis);
  }

  public boolean hasFieldAxis() {
    return hasFieldAxis;
  }

  //
  // PRIVATE Methods
  //

  /** a special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "array";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"notes");
    // attribOrder.add(0,"noteList");
    attribOrder.add(0,"dataCube");
    attribOrder.add(0,"xmlDataIOStyle");
    attribOrder.add(0, "axisList");
    attribOrder.add(0, "dataFormat");
    attribOrder.add(0, "units");
    attribOrder.add(0, "paramList");
    attribOrder.add(0,"description");
    attribOrder.add(0,"name");

    //set up the attribute hashtable key with the default initial value
    attribHash.put("notes", new XMLAttribute(new Notes(), Constants.OBJECT_TYPE));
    // attribHash.put("noteList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("dataCube", new XMLAttribute(new DataCube(this), Constants.OBJECT_TYPE));


    //default is TaggedXMLDataIOStyle, xmlDataOStyle.parentArray = this
    attribHash.put("xmlDataIOStyle", new XMLAttribute(new TaggedXMLDataIOStyle(this), Constants.OBJECT_TYPE));
    attribHash.put("dataFormat", new XMLAttribute(null, Constants.OBJECT_TYPE));
    attribHash.put("units", new XMLAttribute(null, Constants.OBJECT_TYPE));
    attribHash.put("axisList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("paramList", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("description", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("name", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  /**canAddAxisObjToArray: check if we can add this Axis or FieldAxis Object
   * to the array
   * 1- check to see that it has an id
   * 2- we SHOULD also check that the id is unique but DONT currently.
  */
  private boolean canAddAxisObjToArray(AxisInterface axisToAdd) {
    if (axisToAdd.getAxisId() == null) {
      Log.error("Can't add Axis Object without axisId attribute defined");
      return false;
    }
    return true;
  }


   // Need to do this operation after every axis add
   // reset to the current order of the axes.
   // Note: IF there where lots of axes in an object
   // then this could become a real processing bottleneck
   private void updateNotesLocationOrder () {

      // ArrayList axisList = (ArrayList) getAxisList();
      List axisList = getAxisList();
      ArrayList axisIdList = new ArrayList();

      // assemble the list of axisId's
      Iterator iter = axisList.iterator();
      while (iter.hasNext() ) {
         AxisInterface axisObj = (AxisInterface) iter.next();
         String axisIdRef = axisObj.getAxisId();
         ((ArrayList) axisIdList).add(axisIdRef);
      }

      Notes notesObj = getNotesObject();
      notesObj.setLocationOrderList(axisIdList);
   }

   public Object clone() throws CloneNotSupportedException {
    Array cloneObj = (Array) super.clone();
    //deep clone for DataCube
    cloneObj.setDataCube((DataCube) this.getDataCube().clone());

    //there are no locators that are related to the cloned Array object
    cloneObj.locators = new Vector();

    //set the parentArray correctly for child object
    cloneObj.getDataCube().setParentArray(cloneObj);
    cloneObj.getXMLDataIOStyle().setParentArray(cloneObj);

    synchronized (this.paramGroupOwnedHash) {
      synchronized(cloneObj.paramGroupOwnedHash) {
        cloneObj.paramGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.paramGroupOwnedHash.size()));
        Iterator iter = this.paramGroupOwnedHash.iterator();
        while (iter.hasNext()) {
          cloneObj.paramGroupOwnedHash.add(iter.next());
        }
      }
    }

    return cloneObj;

   }  //end of clone
}  //end of Array class

/**
  * Modification History:
  * $Log$
  * Revision 1.14  2000/11/08 19:22:06  thomas
  * Minor fix: import statement had 2 ArrayList entries. -b.t.
  *
  * Revision 1.13  2000/11/06 21:11:52  kelly
  * added deep cloning  --k.z.
  *
  * Revision 1.12  2000/11/02 19:44:29  thomas
  * Added Notes object , removed old noteList XML attribute.
  * Updated all add/remove/etc Notes methods. -b.t.
  *
  * Revision 1.11  2000/11/02 18:06:33  thomas
  * Updated file to have void return from set methods.
  * Removed setParamOwnedHash and setDataCube
  * methods. -b.t.
  *
  * Revision 1.10  2000/10/31 21:39:06  kelly
  * --completed appendData for String data
  * --getFormatList() is returning DataFormat[] now instead of List, faster.  -k.z.
  *
  * Revision 1.9  2000/10/30 18:15:28  kelly
  * minor fix
  *
  * Revision 1.8  2000/10/27 21:05:37  kelly
  * --Units are initialized as null now.
  * --added exception for get/set data.
  * --created modification history.  --k.z.
  *
  *
  */

