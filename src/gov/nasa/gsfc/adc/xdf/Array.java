
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
  * scientific/mathematical data. Array is the basic container for the n-dimensional array data.
  * It gives access to the array data and its descriptors (such as the array axii, associated
  * parameters, notes, etc).
  * /

  /**
  * Here is a diagram showing the inter-relations between these components
  * of the Array in a 2-dimensional dataset with no fields.
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
   * a SCALAR (ARRAY REF) of the list of Axis objects held within this array.
   * paramList--
   * reference of the list of Parameter objects held within in this Array.
   * notes --
   * reference of the object holding the list of Note objects held within this object.
   * dataCube
   * object ref of the DataCube object which is a matrix holding the mathematical data
   * of this array.
   * dataFormat
   * object ref of the DataFormat object.
   * units
   * object ref of the Units object of this array. The Units object
   * is used to hold the Unit objects.
   * fieldAxis
   * object ref of the FieldAxis object.
   */

  public class Array extends BaseObject 
  {

     //
     // Fields
     //

     /* XML attribute names */
     private static final String NAME_XML_ATTRIBUTE_NAME = new String("name");
     private static final String DESCRIPTION_XML_ATTRIBUTE_NAME = new String("description");
     private static final String ID_XML_ATTRIBUTE_NAME = new String("arrayId");
     private static final String APPENDTO_XML_ATTRIBUTE_NAME = new String("appendTo");
     private static final String LESSTHANVALUE_XML_ATTRIBUTE_NAME = new String("lessThanValue");
     private static final String LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("lessThanOrEqualValue");
     private static final String GREATERTHANVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanValue");
     private static final String GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME = new String("greaterThanOrEqualValue");
     private static final String INFINITEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteValue");
     private static final String INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME = new String("infiniteNegativeValue");
     private static final String NODATAVALUE_XML_ATTRIBUTE_NAME = new String("noDataValue");
     private static final String PARAMETERLIST_XML_ATTRIBUTE_NAME = new String("paramList");
     private static final String UNITS_XML_ATTRIBUTE_NAME = new String("units");
     private static final String DATAFORMAT_XML_ATTRIBUTE_NAME = new String("dataFormat");
     private static final String AXISLIST_XML_ATTRIBUTE_NAME = new String("axisList");
     private static final String XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME = new String("xmlDataIoStyle");
     private static final String DATACUBE_XML_ATTRIBUTE_NAME = new String("dataCube");
     private static final String NOTES_XML_ATTRIBUTE_NAME = new String("notes");
 
     /** This field stores object references to those parameter group objects
        to which this array object belongs
      */
     protected Set paramGroupOwnedHash = Collections.synchronizedSet(new HashSet());

     /**the list of locators whose parentArray is this Array object
      */
     protected List locatorList = new Vector();
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
   
     /** set the *name* attribute
      */
     public void setName (String strName)
     {
        ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).setAttribValue(strName);
     }
   
     /** 
      * @return the current *name* attribute
      */
     public String getName()
     {
       return (String) ((XMLAttribute) attribHash.get(NAME_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
      /** set the *description* attribute
      */
     public void setDescription (String strDesc)
     {
        ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
      /*
      * @return the current *description* attribute
      */
     public String getDescription() {
       return (String) ((XMLAttribute) attribHash.get(DESCRIPTION_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** set the *appendTo* attribute
      */
     public void setAppendTo (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(APPENDTO_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
   
      /*
      * @return the current *appendTo* attribute
      */
     public String getAppendTo() {
       return (String) ((XMLAttribute) attribHash.get(APPENDTO_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *lessThanValue* attribute
      */
     public void setLessThanValue (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *lessThanValue* attribute
      */
     public String getLessThanValue() {
       return (String) ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *lessThanOrEqualValue* attribute
      */
     public void setLessThanOrEqualValue (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *lessThanOrEqualValue* attribute
      */
     public String getLessThanOrEqualValue() {
       return (String) ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *greaterThanValue* attribute
      */
     public void setGreaterThanValue (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *greaterThanValue* attribute
      */
     public String getGreaterThanValue() {
       return (String) ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *greaterThanOrEqualValue* attribute
      */
     public void setGreaterThanOrEqualValue (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
     /*
      * @return the current *greaterThanOrEqualValue* attribute
      */
     public String getGreaterThanOrEqualValue() {
       return (String) ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *infiniteValue* attribute
      */
     public void setInfiniteValue (String strDesc)
     { 
        ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
     /*
      * @return the current *infiniteValue* attribute
      */
     public String getInfiniteValue() {
       return (String) ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *infiniteNegativeValue* attribute
      */
     public void setInfiniteNegativeValue (String strDesc)
     {
        ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
     /*
      * @return the current *infiniteNegativeValue* attribute
      */
     public String getInfiniteNegativeValue() {
       return (String) ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** set the *noDataValue* attribute
      */
     public void setNoDataValue (String strDesc)
     {
        ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
     /*
      * @return the current *noDataValue* attribute
      */
     public String getNoDataValue() {
       return (String) ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
   
     /** set the *arrayId* attribute
      */
     public void setArrayId(String strDesc)
     {
        ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
      /*
      * @return the current *arrayId* attribute
      */
     public String getArrayId() {
       return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
   
     /** set the *paramList* attribute
      */
     public void setParamList(List param) {
        ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).setAttribValue(param);
     }
   
     /**
      * @return the current *paramList* attribute
      */
     public List getParamList() {
       return (List) ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
      /** set the *units* attribute
      */
     public void setUnits (Units units)
     {
        ((XMLAttribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).setAttribValue(units);
     }
   
     /**
      * @return the current *units* attribute
      */
     public Units getUnits()
     {
       return (Units) ((XMLAttribute) attribHash.get(UNITS_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** Sets the data format *type* for this array (an DataFormat object
      * is held in the attribute $obj->dataFormat, its type is accessible
      * Takes a SCALAR object reference as its argument. Allowed objects to pass
      * to this method include BinaryIntegerDataFormat, BinaryFloatDataFormat,
      * FloatDataFormat, IntegerDataFormat, or StringDataFormat.
     */
     public void setDataFormat(DataFormat dataFormat)
     {
        ((XMLAttribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).setAttribValue(dataFormat);
     }
   
     public Object getData (Locator locator)
     throws NoDataException 
     {
        return getDataCube().getData(locator); 
     }

     /**
      * @return the current *dataFormat* attribute
      */
     public DataFormat getDataFormat()
     {
        return (DataFormat) ((XMLAttribute) attribHash.get(DATAFORMAT_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** set the Notes object held by this Array object
      */
     public void setNotesObject (Notes notes)
     {
        ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
     }
   
     /**
        @return the current *Notes* attribute object
      */
     public Notes getNotesObject()
     {
        return (Notes) ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
      /** set the *axisList* attribute
      */
     public void setAxisList(List axis) {
        ((XMLAttribute) attribHash.get(AXISLIST_XML_ATTRIBUTE_NAME)).setAttribValue(axis);
     }
   
     /**
      * @return the current *axisList* attribute
      */
     public List getAxisList() {
       return (List) ((XMLAttribute) attribHash.get(AXISLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** set the *xmlDataIOStyle* attribute
      * note we have to insure that _parentArray is properly updated
      */
     public void setXMLDataIOStyle(XMLDataIOStyle xmlDataIOStyle)
     {
        //set the parent array to this object
        xmlDataIOStyle.setParentArray(this);
   
        // set the xmlattribute
        ((XMLAttribute) attribHash.get(XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME)).setAttribValue(xmlDataIOStyle);
   
     }
   
     /**
      * @return the current *xmlDataIOStyle* attribute
      */
     public XMLDataIOStyle getXMLDataIOStyle()
     {
       return (XMLDataIOStyle) ((XMLAttribute) attribHash.get(XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /*  set the *dataCube* attribute
      */
   
      //
      // Hurm.. This is a dangerous method. All sorts of array meta-data arent updated
      // properly when this is used. Making it private for now. -b.t.
      //
      private void setDataCube(DataCube dataCube)
      {
         ((XMLAttribute) attribHash.get(DATACUBE_XML_ATTRIBUTE_NAME)).setAttribValue(dataCube);
      }
   
   
     /**
         @return the current *DataCube* attribute
      */
     public DataCube getDataCube()
     {
        return (DataCube) ((XMLAttribute) attribHash.get(DATACUBE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
      /** Set the *noteList* attribute
       */
      public void setNoteList(List note) {
         getNotesObject().setNoteList(note);
      }
   
      /**
         @return the current *noteList* attribute
      */
      public List getNoteList() {
         return getNotesObject().getNoteList();
      }
   
      /** set the dimension of the DataCube held within this Array.
      */
      public int getDimension() {
        return getDataCube().getDimension();
      }
   
      //
      // Other Public Methods
      //
   
      /** Create one instance of an Locator object for this array.
       *
       */
      public Locator createLocator() {
         Locator locatorObj = new Locator(this);
   
         //add this locator to the list of locators this Array object monitors
         locatorList.add(locatorObj);
         return locatorObj;
      }
   
      /** Insert an ParameterGroup object into this object.
      * @param group - ParameterGroup to be added
      * @return a ParameterGroup object reference on success, null on failure.
      */
     public ParameterGroup addParamGroup (ParameterGroup group) {
         //add the group to the groupOwnedHash
         paramGroupOwnedHash.add(group);
         return group;
     }
   
     /**Remove an ParameterGroup object from the hashset--paramGroupOwnedHash
      * @param group - ParameterGroup to be removed
      * @return true on success, false on failure
      */
     public boolean removeParamGroup(ParameterGroup group) {
       return paramGroupOwnedHash.remove(group);
     }
   
      /** insert an Axis object into the list of axes held by this object.
          @param axis - Axis to be added
          @return an Axis object on success, null on failure
      */
      public Axis addAxis(Axis axis) {
   
        if (!canAddAxisObjToArray(axis)) //check if the axis can be added
           return null;
   
        getDataCube().incrementDimension(axis );  //increment the DataCube dimension by 1
   
        getAxisList().add(axis);
   
        updateNotesLocationOrder(); // reset to the current order of the axes
        updateChildLocators(axis, "add");
   
        return axis;
     }
   
      /**removes a Axis object from axisList
      * @param axisObj - Axis to be removed
      * @return true on success and decrement the dimension,
      *          false on failure and keep the dimension unchanged
      * double check the implication on datacube
      */
     public boolean removeAxis(Axis axisObj) {
       boolean isRemoveSuccess = removeFromList(axisObj, getAxisList(), AXISLIST_XML_ATTRIBUTE_NAME);
       if (isRemoveSuccess) {   //remove successful
          getDataCube().decrementDimension();   //decrease the dimension by 1
          updateChildLocators(axisObj, "remove");
       }
       return isRemoveSuccess;
     }
   
     /**removes a Axis object from AxisList
      * @param index - the index of the axis to be removed in the axisList
      *  @return true on success and decrement the dimension,
      *           false on failure and keep the dimension unchanged
      */
     public boolean removeAxis(int index) {
       boolean isRemoveSuccess = removeFromList(index, getAxisList(), AXISLIST_XML_ATTRIBUTE_NAME);
       if (isRemoveSuccess) {  //remove successful
          getDataCube().decrementDimension();   //decrease the dimension by 1
          updateChildLocators(index, "remove");
       }
       return isRemoveSuccess;
     }
   
     /**Insert an Unit object into the Units object held in this object.
      * @param unit - Unit to be added
      * @return an Unit object
      */
     public Unit addUnit(Unit unit) {
       Units u = getUnits();
       if (u == null) {
         u = new Units();
         setUnits(u);
       }
       return  u.addUnit(unit);
     }
   
     /** Remove an Unit object from the Units object held in
      * this object
      * @param what - Unit to be removed
      * @return true if successful, false if not
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
   
     /**Remove an Unit object from the Units object held in
      * this Array object
      * @param index - the index of the Unit to be removed
      * @return true if successful, false if not
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
   
     /** insert a Parameter object into the paramList
      * @param p - the Parameter to be added
      * @return a Parameter object
      */
     public Parameter addParameter(Parameter p) {
       getParamList().add(p);
       return p;
     }

   /**removes an Parameter object from paramList
    * @param what - Parameter to be removed
    * @return true on success, false on failure
    */
   public boolean removeParameter(Parameter what) {
       return removeFromList(what, getParamList(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
   }
   
   /** removes an Parameter object from paramList
    * @param index - list index number of the Parameter object to be removed
    * @return true on success, false on failure
    */
   public boolean removeParameter(int index) {
       return removeFromList(index, getParamList(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
   }
   
   /**A convenience method that returns an array ref of non-negative INTEGERS
      which are the maximum index values along each dimension (FieldAxis and Axis objects).
    */
   public int[] getMaxDataIndices () {
       return getDataCube().getMaxDataIndex();
   }
   
   /** insert a Note object into the list of notes in this Array object
    * @param n - Note to be added
    * @return a Note object
    */
   public Note addNote(Note n) {
       return getNotesObject().addNote(n);
   }
   
   /**removes a Note object from the list of notes in this Array object
    * @param what - Note to be removed
    * @return true on success, false on failure
    */
   public boolean removeNote(Note what) {
        return (boolean) getNotesObject().removeNote(what);
   }
   
   
   /**removes a Note object from the list of notes in this Array object
    * @param index - list index number of the Note to be removed
    * @return true on success, false on failure
    */
   public boolean removeNote(int index) {
        return (boolean) getNotesObject().removeNote(index); 
   }
   
   /**Convenience method which returns a list of the notes held by
    * this object.
    */
   public List getNotes() {
       return (List) getNotesObject().getNoteList();
   }
   
   /** Append the string value onto the requested datacell
    * (via DataCube LOCATOR REF).
    */
   public void appendData (Locator locator, String strValue) 
   throws SetDataException 
   {
        getDataCube().appendData(locator, strValue);
   }
   
   /** Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, double numValue) 
   throws SetDataException 
   {
      getDataCube().setData(locator, numValue);
   }
   
   /** Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, Double numValue) 
   throws SetDataException 
   {
      getDataCube().setData(locator, numValue);
   }

   /** Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, int numValue) 
   throws SetDataException 
   {
      getDataCube().setData(locator, numValue);
   }

   /** Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, Integer numValue) 
   throws SetDataException 
   {
      getDataCube().setData(locator, numValue);
   }
   
   /**  Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, String strValue ) 
   throws SetDataException 
   {
         getDataCube().setData(locator, strValue);
   }
   
   /**Get the String data in the requested datacell
    */
   public String getStringData(Locator locator) 
   throws NoDataException 
   {
       try {
         return getDataCube().getStringData(locator);
       }
       catch (NoDataException e) {
         throw e;
       }
   }
   
   /**Get the integer data in the requested datacell
    */
   public int getIntData(Locator locator) throws NoDataException {
       try {
         return getDataCube().getIntData(locator);
       }
       catch (NoDataException e) {
         throw e;
       }
   }

   /**Get the double data in the requested datacell
    */
   public double getDoubleData(Locator locator) throws NoDataException {
       try {
         return getDataCube().getDoubleData(locator);
       }
       catch (NoDataException e) {
         throw e;
       }
   }
   
   /** Remove the requested data from the indicated datacell
    *  (via DataCube LOCATOR REF) in the DataCube held in this Array.
    */
   
    public boolean  removeData (Locator locator) {
       return getDataCube().removeData(locator);
   }
   
   
   /**Get the dataFormatList for this array.
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

   /** A convenience method (same as setFieldAxis()).
       Changes the FieldAxis object in this Array to the indicated one.
       @return reference to fieldAxis if successful, null if not.
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
   
       updateChildLocators(fieldAxis, "add");
   
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
   
   public void setFieldAxis(FieldAxis fieldAxis) {
        addFieldAxis(fieldAxis);
   }
   
   public boolean hasFieldAxis() {
       return hasFieldAxis;
   }
   
   //
   // PRIVATE Methods
   //
   
   /** a special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {
   
       classXDFNodeName = "array";
   
       // order matters! these are in *reverse* order of their
       // occurence in the XDF DTD
       attribOrder.add(0, NOTES_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, DATACUBE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, AXISLIST_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, DATAFORMAT_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, UNITS_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, PARAMETERLIST_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, NODATAVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, INFINITEVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, GREATERTHANVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, LESSTHANVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, APPENDTO_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, DESCRIPTION_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, NAME_XML_ATTRIBUTE_NAME);
   
       //set up the attribute hashtable key with the default initial value
       attribHash.put(NOTES_XML_ATTRIBUTE_NAME, new XMLAttribute(new Notes(), Constants.OBJECT_TYPE));
       attribHash.put(DATACUBE_XML_ATTRIBUTE_NAME, new XMLAttribute(new DataCube(this), Constants.OBJECT_TYPE));
       //default is TaggedXMLDataIOStyle
       attribHash.put(XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME, new XMLAttribute(new TaggedXMLDataIOStyle(this), Constants.OBJECT_TYPE));
       attribHash.put(AXISLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
       attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));
       attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));
       attribHash.put(PARAMETERLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
       attribHash.put(NODATAVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(INFINITEVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(GREATERTHANVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(LESSTHANVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(APPENDTO_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(DESCRIPTION_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(NAME_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
   
   };
   
   /**canAddAxisObjToArray: check if we can add this Axis or FieldAxis Object
     * to the array
     * 1- check to see that it has an id
     * 2- we SHOULD also check that the id is unique but DONT currently.
    */
   private boolean canAddAxisObjToArray (AxisInterface axisToAdd) {
      if (axisToAdd.getAxisId() == null) {
         Log.warnln("Warning: Can't add Axis Object without axisId attribute defined");
         return false;
       }
       return true;
   }
 
   // force an update of all child locator objects. We do this to 
   // insure that when an axis is removed/added the locators reflect
   // the new geometry of the array.
   // 
   private void updateChildLocators (AxisInterface axis, String action) {
   
         Iterator iter = locatorList.iterator();
         while (iter.hasNext() ) {
             Locator childLocator = (Locator) iter.next();
             if (action.equals("add"))
                childLocator.addAxis(axis);
             else
                childLocator.removeAxis(axis);
         }
   
   }
      
   private void updateChildLocators (int index, String action) {
         Iterator iter = locatorList.iterator();
         while (iter.hasNext() ) {
             Locator childLocator = (Locator) iter.next();
             if (action.equals("add"))
                Log.errorln("This method cant add an axis!");
             else
                childLocator.removeAxis(index);
         }
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
       cloneObj.locatorList = new Vector();
   
       //set the parentArray correctly for child object
       cloneObj.getDataCube().setParentArray(cloneObj);
       cloneObj.getXMLDataIOStyle().setParentArray(cloneObj);
   
       synchronized (this.paramGroupOwnedHash) {
         synchronized(cloneObj.paramGroupOwnedHash) {
           cloneObj.paramGroupOwnedHash = Collections.synchronizedSet(new HashSet(this.paramGroupOwnedHash.size()));
           Iterator iter = this.paramGroupOwnedHash.iterator();
           while (iter.hasNext()) {
             cloneObj.paramGroupOwnedHash.add(((Group) iter.next()).clone());
           }
         }
       }
   
       return cloneObj;
   
   }  //end of clone
   
}  //end of Array class

/**
  * Modification History:
  * $Log$
  * Revision 1.22  2001/02/07 18:40:15  thomas
  * Added new setData methods. Converted XML attribute decl
  * to use constants (final static fields within the object). These
  * are private decl for now. -b.t.
  *
  * Revision 1.21  2001/01/29 19:29:34  thomas
  * Changes related to combining ExponentialDataFormat
  * and FloatDataFormat classes. -b.t.
  *
  * Revision 1.20  2001/01/22 22:09:25  thomas
  * Added updateChildLocators method. Fixed missing
  * fctnality of removingAxis then updating locators. -b.t.
  *
  * Revision 1.19  2000/11/27 16:57:44  thomas
  * Made init method protected so that extending
  * Dataformats may make use of them. -b.t.
  *
  * Revision 1.18  2000/11/17 19:01:27  thomas
  * Small fix to addAxis warning message. -b.t.
  *
  * Revision 1.17  2000/11/16 19:43:51  kelly
  * *** empty log message ***
  *
  * Revision 1.15  2000/11/08 22:30:12  thomas
  * Changed set methods to return void. -b.t.
  *
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

