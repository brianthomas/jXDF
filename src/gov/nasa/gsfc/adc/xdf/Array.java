
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
  *
  *
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

  public class Array extends BaseObjectWithXMLElements 
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
     private static final String NOTANUMBERVALUE_XML_ATTRIBUTE_NAME = new String("notANumberValue");
     private static final String OVERFLOWVALUE_XML_ATTRIBUTE_NAME = new String("overflowValue");
     private static final String UNDERFLOWVALUE_XML_ATTRIBUTE_NAME = new String("underflowValue");
     private static final String DISABLEDVALUE_XML_ATTRIBUTE_NAME = new String("disabledValue");
     private static final String PARAMETERLIST_XML_ATTRIBUTE_NAME = new String("paramList");
     private static final String UNITS_XML_ATTRIBUTE_NAME = new String("units");
     private static final String DATAFORMAT_XML_ATTRIBUTE_NAME = new String("dataFormat");
     private static final String AXISLIST_XML_ATTRIBUTE_NAME = new String("axisList");
     private static final String XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME = new String("xmlDataIoStyle");
     private static final String DATACUBE_XML_ATTRIBUTE_NAME = new String("dataCube");
     private static final String NOTES_XML_ATTRIBUTE_NAME = new String("arrayNotes");
 
     /** This field stores object references to those parameter group objects
        to which this array object belongs
      */
     protected Set paramGroupOwnedHash = Collections.synchronizedSet(new HashSet());

     /**the list of locators whose parentArray is this Array object
      */
     protected List locatorList = new Vector();

     protected boolean hasFieldAxis = false;

     public boolean needToUpdateLongArrayMult = true;
     private int[] longIndexMult;

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
     public void setLessThanValue (Object strDesc)
     { 
        ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *lessThanValue* attribute
      */
     public Object getLessThanValue() {
       return ((XMLAttribute) attribHash.get(LESSTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *lessThanOrEqualValue* attribute
      */
     public void setLessThanOrEqualValue (Object strDesc)
     { 
        ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *lessThanOrEqualValue* attribute
      */
     public Object getLessThanOrEqualValue() {
       return ((XMLAttribute) attribHash.get(LESSTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *greaterThanValue* attribute
      */
     public void setGreaterThanValue (Object strDesc)
     { 
        ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
      /*
      * @return the current *greaterThanValue* attribute
      */
     public Object getGreaterThanValue() {
       return ((XMLAttribute) attribHash.get(GREATERTHANVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *greaterThanOrEqualValue* attribute
      */
     public void setGreaterThanOrEqualValue (Object strDesc)
     { 
        ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
     /*
      * @return the current *greaterThanOrEqualValue* attribute
      */
     public Object getGreaterThanOrEqualValue() {
       return ((XMLAttribute) attribHash.get(GREATERTHANOREQUALVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *infiniteValue* attribute
      */
     public void setInfiniteValue (Object strDesc)
     { 
        ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     } 
       
     /*
      * @return the current *infiniteValue* attribute
      */
     public Object getInfiniteValue() {
       return ((XMLAttribute) attribHash.get(INFINITEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     } 
   
     /** set the *infiniteNegativeValue* attribute
      */
     public void setInfiniteNegativeValue (Object strDesc)
     {
        ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
     /*
      * @return the current *infiniteNegativeValue* attribute
      */
     public Object getInfiniteNegativeValue() {
       return ((XMLAttribute) attribHash.get(INFINITENEGATIVEVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /** set the *noDataValue* attribute
      */
     public void setNoDataValue (Object strDesc)
     {
        ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
     }
   
     /*
      * @return the current *noDataValue* attribute
      */
     public Object getNoDataValue() {
       return ((XMLAttribute) attribHash.get(NODATAVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
   /** set the *notANumberValue* attribute
    */
   public void setNotANumberValue (Object strDesc)
   {
      ((XMLAttribute) attribHash.get(NOTANUMBERVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /*
    * @return the current *notANumberValue* attribute
    */
   public Object getNotANumberValue() {
      return ((XMLAttribute) attribHash.get(NOTANUMBERVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *overflowValue* attribute
    */
   public void setOverflowValue (Object strDesc)
   {
      ((XMLAttribute) attribHash.get(OVERFLOWVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /*
    * @return the current *overflowValue* attribute
    */
   public Object getOverflowValue() {
      return ((XMLAttribute) attribHash.get(OVERFLOWVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *underflowValue* attribute
    */
   public void setUnderflowValue (Object strDesc)
   {
      ((XMLAttribute) attribHash.get(UNDERFLOWVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /*
    * @return the current *underflowValue* attribute
    */
   public Object getUnderflowValue() {
      return ((XMLAttribute) attribHash.get(UNDERFLOWVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
   }

   /** set the *disabledValue* attribute
    */
   public void setDisabledValue (Object strDesc)
   {
      ((XMLAttribute) attribHash.get(DISABLEDVALUE_XML_ATTRIBUTE_NAME)).setAttribValue(strDesc);
   }

   /*
    * @return the current *disabledValue* attribute
    */
   public Object getDisabledValue() {
      return ((XMLAttribute) attribHash.get(DISABLEDVALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
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
      * @deprecated use getParameters method instead.
      */
     public List getParamList() {
       return (List) ((XMLAttribute) attribHash.get(PARAMETERLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
     }

     /**
      * @return the current list of parameters held in this object.
      */
     public List getParameters() {
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
     public void setArrayNotes (Notes notes)
     {
        ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
     }

     /** set the Notes object held by this Array object
         @deprecated use the setArrayNotes method instead.
      */
     public void setNotesObject (Notes notes)
     {
        ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).setAttribValue(notes);
     }
   
     /**
        @return the current (array) Notes object that describes the notes held by this array. 
      */
     public Notes getArrayNotes()
     {
        return (Notes) ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
     /**
        @return the current (array) Notes object that describes the notes held by this array. 
        @deprecated use the getArrayNotes method instead.
     */
     public Notes getNotesObject()
     {
        return (Notes) ((XMLAttribute) attribHash.get(NOTES_XML_ATTRIBUTE_NAME)).getAttribValue();
     }

     /* set the *axisList* attribute
      */
     public void setAxisList (List axisList) {
	 // remove the existing axes
	 List oldAxisList = getAxes();
         int lastindex = oldAxisList.size()-1;
	 for (int i = lastindex; i >= 0; i--)
         {
	     removeAxis(i);
         }

         hasFieldAxis = false;

	 //add new axes
	 if (axisList != null && axisList.size() > 0) {
	     Iterator iter = axisList.iterator();
	     while (iter.hasNext())
             {
		 AxisInterface axis = (AxisInterface) iter.next();
		 addAxis(axis);
             }
	 }
     }
   
     /**
      * @return the current a list of FieldAxis and Axis Objects in this array. 
      */
     public List getAxes () {
       return (List) ((XMLAttribute) attribHash.get(AXISLIST_XML_ATTRIBUTE_NAME)).getAttribValue();
     }

     /**
      * @return the current a list of FieldAxis and Axis Objects in this array. 
      * @deprecated This name does not make clear that fieldAxis will also be returned.
      */
     public List getAxisList () {
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
   
     /**
         @return the current *DataCube* attribute
      */
     public DataCube getDataCube()
     {
        return (DataCube) ((XMLAttribute) attribHash.get(DATACUBE_XML_ATTRIBUTE_NAME)).getAttribValue();
     }
   
      /** Set the *noteList* attribute
       * @deprecated use setNotes instead
       */
      public void setNoteList (List note) {
         getArrayNotes().setNotes(note);
      }

      /** Set the *noteList* attribute
       */
      public void setNotes (List note) {
         getArrayNotes().setNotes(note);
      }
   
      /**
         @return the current *noteList* attribute
      */
      public List getNoteList() {
         return getArrayNotes().getNotes();
      }
   
      /** get the dimension of the DataCube held within this Array.
      */
      public int getDimension() {
        // return getDataCube().getDimension();
        return getAxes().size();
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
     public boolean addParamGroup (ParameterGroup group) {
         //add the group to the groupOwnedHash
         paramGroupOwnedHash.add(group);
         return true;
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
     public boolean addAxis (AxisInterface axis) {

        if (canAddAxisObjToArray(axis)) { //check if the axis can be added

           if (axis instanceof FieldAxis) { 

              setFieldAxis((FieldAxis) axis); // special routine, there can only be one in list 
                                              // amoungst other things.
           } else {

              getAxes().add((Axis) axis);
              updateChildLocators((Axis) axis, "add");
              updateNotesLocationOrder(); // reset to the current order of the axes
              getXMLDataIOStyle().getIOAxesOrder().add(axis); // tack this axis on the end
              axis.setParentArray(this); // set this as the parent array of the object

           }
        
           // update readAxisOrder by taking on new axis.
           getXMLDataIOStyle().getIOAxesOrder().add(axis); 


        } else {  
           return false;
        }

        return true;

     }
   
      /**removes a Axis object from axisList
      * @param axisObj - Axis to be removed
      * @return true on success and decrement the dimension,
      *          false on failure and keep the dimension unchanged
      * double check the implication on datacube
      */
     public boolean removeAxis (AxisInterface axisObj) {

       boolean isRemoveSuccess = false;
       if (axisObj instanceof FieldAxis) {
          isRemoveSuccess = setFieldAxis(null);
       } else {
          isRemoveSuccess = removeFromList(axisObj, getAxes(), AXISLIST_XML_ATTRIBUTE_NAME);
          if (isRemoveSuccess) {    //remove successful
             getDataCube().reset(); // reset data within the datacube
             updateChildLocators(axisObj, "remove");
             getXMLDataIOStyle().setIOAxesOrder(getAxes()); // reset IO ordering
          }
       }
       return isRemoveSuccess;
     }
   
     /**removes a Axis object from AxisList
      * @param index - the index of the axis to be removed in the axisList
      *  @return true on success and decrement the dimension,
      *           false on failure and keep the dimension unchanged
      */
     public boolean removeAxis (int index) {

       AxisInterface removeAxis = (AxisInterface) getAxes().get(index);
       boolean isRemoveSuccess = false;

       if (removeAxis instanceof FieldAxis) {
          isRemoveSuccess = setFieldAxis(null);
       } else {
          isRemoveSuccess = removeFromList(index, getAxes(), AXISLIST_XML_ATTRIBUTE_NAME);
          if (isRemoveSuccess) {  //remove successful
             getDataCube().reset(); // reset data within the datacube
             updateChildLocators(index, "remove");
             getXMLDataIOStyle().setIOAxesOrder(getAxes()); // reset IO ordering
          }
       }
       return isRemoveSuccess;
     }
   
     /**Insert an Unit object into the Units object held in this object.
      * @param unit - Unit to be added
      * @return an Unit object
      */
     public boolean addUnit(Unit unit) {
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
     public boolean addParameter(Parameter p) {
       getParameters().add(p);
       return true;
     }

   /**removes an Parameter object from paramList
    * @param what - Parameter to be removed
    * @return true on success, false on failure
    */
   public boolean removeParameter(Parameter what) {
       return removeFromList(what, getParameters(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
   }
   
   /** removes an Parameter object from paramList
    * @param index - list index number of the Parameter object to be removed
    * @return true on success, false on failure
    */
   public boolean removeParameter(int index) {
       return removeFromList(index, getParameters(), PARAMETERLIST_XML_ATTRIBUTE_NAME);
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
   public boolean addNote(Note n) {
       return getArrayNotes().addNote(n);
   }
   
   /**removes a Note object from the list of notes in this Array object
    * @param what - Note to be removed
    * @return true on success, false on failure
    */
   public boolean removeNote(Note what) {
        return (boolean) getArrayNotes().removeNote(what);
   }
   
   
   /**removes a Note object from the list of notes in this Array object
    * @param index - list index number of the Note to be removed
    * @return true on success, false on failure
    */
   public boolean removeNote(int index) {
        return (boolean) getArrayNotes().removeNote(index); 
   }
   
   /** Convenience method which returns a list of the notes held by
    *  the array notes object of this array.
    */
   public List getNotes() {
       return (List) getArrayNotes().getNotes();
   }
   
   /** Append the string value onto the requested datacell
    * (via DataCube LOCATOR REF).
    */
   public void appendData (Locator locator, String strValue) 
   throws SetDataException 
   {
        getDataCube().appendData(locator, strValue);
   }
   

   /**
    *  set data for a general object
    *  which can be an array of primitive object; or wrapped data
    */
   public void setData (Locator locator, Object dataObj) 
       throws SetDataException 
   {
       String classType = dataObj.getClass().getName().trim();
       if (classType.startsWith("[[")) {
	   Object [] dataArray = (Object[])dataObj;
	   for (int i=0; i<dataArray.length; i++) {
	       setData(locator, dataArray[i]);
	   }
       } else if (classType.startsWith("[")) {
	   if (dataObj instanceof byte[] )
	       this.setData (locator, (byte[])dataObj);
	   else if (dataObj instanceof short[] )
	       this.setData (locator, (short[])dataObj);
	   else if (dataObj instanceof int[] )
	       this.setData (locator, (int[])dataObj);
	   else if (dataObj instanceof long[])
	       this.setData (locator, (long[])dataObj);
	   else if (dataObj instanceof float[])
	       this.setData (locator, (float[])dataObj);
	   else if (dataObj instanceof double[])
	       this.setData (locator, (double[])dataObj);
	   else if (dataObj instanceof String[])
	       this.setData (locator, (String[])dataObj);	
	   else {
	       String msg = "Array: setData(): " + dataObj.getClass().getName() + " is not implemented";
	       Log.errorln(msg);
	       throw new SetDataException (msg);
	   }
       } else {
	   if (dataObj instanceof Double )
	       this.setData (locator, (Double) dataObj);
	   else if (dataObj instanceof Integer )
	       this.setData (locator, (Integer) dataObj);
	   else if (dataObj instanceof Short )
	       this.setData (locator, (Short) dataObj);
	   else {
	       String msg = "Array: setData(): " + dataObj.getClass().getName() + " is not implemented";
	       Log.errorln(msg);
	       throw new SetDataException (msg);
	   }
       }
   }


   /** 
    *  set Data from adouble data array
    */
   public void setData (Locator locator, double [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, numValue[i]);
	   locator.next();
       }
   }

   /** 
    *  set Data from an integer data array 
    */
   public void setData (Locator locator, int [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, numValue[i]);
	   locator.next();
       }
   }


   /** 
    *  set Data from an integer data array 
    */
   public void setData (Locator locator, short [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, numValue[i]);
	   locator.next();
       }
   }


  /** 
    *  set Data from an integer data array 
    */
   public void setData (Locator locator, long [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, (int) numValue[i]);
	   locator.next();
       }
   }


   /** 
    *  set Data from a float data array 
    */
   public void setData (Locator locator, float [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, (double) numValue[i]);
	   locator.next();
       }
   }


   /** 
    *  set Data from a data array -- String
    */
   public void setData (Locator locator, String [] numValue) 
       throws SetDataException {

       for (int i = 0; i < numValue.length; i++) {
	   this.setData(locator, numValue[i]);
	   locator.next();
       }
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
   public void setData (Locator locator, short numValue) 
   throws SetDataException 
   {
      getDataCube().setData(locator, numValue);
   }


   /** Set the value of the requested datacell. 
    * Overwrites existing datacell value if any.
    */
   public void setData (Locator locator, Short numValue) 
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

  /** Changes the FieldAxis object in this Array to the indicated one.
      If null object is passed, then it will remove the current FieldAxis. 
      from the array axis list.
      @return true if successful, false if not.
    */
   // You can't just add in a FieldAxis, as there can only be one in list 
   // amoungst other things which we check/do here.
   public boolean setFieldAxis (FieldAxis fieldAxis) {

       if (fieldAxis != null && !canAddAxisObjToArray(fieldAxis))
         return false;

       if (hasFieldAxis) { // already one exists

          FieldAxis removeAxis = null;
          if (fieldAxis != null) {
              removeAxis = (FieldAxis) getAxes().get(0);
              getAxes().set(0, fieldAxis); // replace current fieldAxis with this one 
          } else {
              removeAxis = (FieldAxis) getAxes().remove(0); // delete old axis
              hasFieldAxis = false;
          }

          // reset IO ordering by removing old axis and then
          // inserting in new one in its place (if new axis exists) 
          int removeIndex = getXMLDataIOStyle().getIOAxesOrder().lastIndexOf(removeAxis); 
          getXMLDataIOStyle().getIOAxesOrder().remove(removeIndex); 
          if (fieldAxis != null) 
             getXMLDataIOStyle().getIOAxesOrder().add(removeIndex, fieldAxis); 

       } else { // add in new fieldAxis 

          hasFieldAxis = true; 
          getAxes().add(0, fieldAxis);  //replace the old fieldAxis with the new one
          getXMLDataIOStyle().getIOAxesOrder().add(fieldAxis); // tack it on the end 

       }

       getDataCube().reset(); // reset data within the datacube

       // IF we had an object, update the locators
       if (fieldAxis != null) {
          updateChildLocators(fieldAxis, "add");
          fieldAxis.setParentArray(this); // set this as the parent array of the object
       } else { 
          hasFieldAxis = false; 
       }

       updateNotesLocationOrder(); // reset to the current order of the axes
                                   // may not be needed, but we do for all cases anyways
   
       // array doenst hold a dataformat anymore
       // each field along the fieldAxis should have dataformat
       // IF fieldAxis exists
       if (hasFieldAxis) { 
          setDataFormat(null);
       }

       return true;

   }

   public FieldAxis getFieldAxis() {

       FieldAxis fieldAxis = null;

       if (hasFieldAxis) {
          List axisList = getAxes();
          Iterator iter = axisList.iterator();
          while (iter.hasNext()) {
             Object axis = (Object) iter.next();
             if (axis instanceof FieldAxis) {
                 return (FieldAxis) axis;
             }
          }
       } 

       return fieldAxis;

   }

   public boolean hasFieldAxis() {
       return hasFieldAxis;
   }


   /** a deep clone of this array. 
    */
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
   

   //
   // Protected Methods
   //

   /** a special private method used by constructor methods to
       conviently build the XML attribute list for a given class.
    */
   protected void init()
   {
   
       super.init();

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
       attribOrder.add(0, DISABLEDVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, UNDERFLOWVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, OVERFLOWVALUE_XML_ATTRIBUTE_NAME);
       attribOrder.add(0, NOTANUMBERVALUE_XML_ATTRIBUTE_NAME);
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
       attribHash.put(AXISLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
       //default is TaggedXMLDataIOStyle
       attribHash.put(XMLDATAIOSTYLE_XML_ATTRIBUTE_NAME, new XMLAttribute(new TaggedXMLDataIOStyle(this), Constants.OBJECT_TYPE));
       attribHash.put(DATAFORMAT_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));
       attribHash.put(UNITS_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));
       attribHash.put(PARAMETERLIST_XML_ATTRIBUTE_NAME, new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
       attribHash.put(DISABLEDVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(OVERFLOWVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(UNDERFLOWVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
       attribHash.put(NOTANUMBERVALUE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
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
   

   //
   // PRIVATE Methods
   //

   /**  set the *dataCube* that this array describes
     */
      // Hurm.. This is a dangerous method. All sorts of array meta-data arent updated
      // properly when this is used. Making it private for now. -b.t.
   private void setDataCube(DataCube dataCube)
   {
         ((XMLAttribute) attribHash.get(DATACUBE_XML_ATTRIBUTE_NAME)).setAttribValue(dataCube);
   }

   /** canAddAxisObjToArray: check if we can add this Axis or FieldAxis Object
     * to the array. This entails:  
     * 1- check to see that it has an id. 
     * 2- we SHOULD also check that the id is unique to the entire structure
     *    to which the array belongs but dont currently. 
     */
   private boolean canAddAxisObjToArray (AxisInterface axisToAdd) {
      if (axisToAdd.getAxisId() == null) {
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

   // This whole routine should probably be in the locator.
   // and update ONLY when the current location is changed.
   // Note that because of complications in storing values from fieldAxis
   // which is always the at the 0 index position (if it exists)
   // we can't simply treat index0 as the short axis. Instead, we
   // have to use the axis at index1 (if it exists).
   public int getLongArrayIndex (Locator locator) {

      int longIndex = 0;

      List axisList = getAxes();
      int numOfAxes = axisList.size(); // should be internal variable updated on add/removeAxis in Array 
      if (numOfAxes > 0) {
         AxisInterface axis = (AxisInterface) axisList.get(0);
         longIndex = locator.getAxisIndex(axis);

         if (numOfAxes >= 2) {
            // safety
            if (this.longIndexMult == null || this.needToUpdateLongArrayMult ) {
                 updateLongIndexMultArray(); 
            }

            // we skip over axis at index 1, that is the "short axis"
            // each of the higher axes contribute 2**(i-1) * index
            // to the overall long axis value.
            for (int i = 2; i < numOfAxes; i++) {
               axis = (AxisInterface) axisList.get(i);
               longIndex += locator.getAxisIndex(axis) * longIndexMult[i];
            }
         }
      }

      return (longIndex*2); // double value to allow for shadow byte array 
   }

   private void updateLongIndexMultArray () {

      List axisList = getAxes();
      int numOfAxes = axisList.size(); // should be internal variable updated on add/removeAxis in Array 
      if (numOfAxes > 1) {

         longIndexMult = new int[numOfAxes+1];

         // we skip axis #1 (its the short axis) and just get
         // axis 0 as prev axis for axis #2
         int mult = ((AxisInterface) axisList.get(0)).getLength();
         longIndexMult[2] = mult;

         // algorithm for higher dimension axes 
         for (int i = 3; i < numOfAxes; i++) {
             mult *= ((AxisInterface) axisList.get(i-1)).getLength();
             longIndexMult[i] = mult;
         }

      } else
         longIndexMult = null;

      this.needToUpdateLongArrayMult = false;

   }


   // Should be hardwired w/ private variable. Only
   // updates when addAxis is called by parentArray.
   public int getShortArrayIndex (Locator locator) {
      int shortIndex = 0;

      AxisInterface shortaxis = getShortAxis();
      if (shortaxis != null) {
         shortIndex = locator.getAxisIndex(shortaxis);
      }

      return shortIndex;
   }

   // get the axis that represents the short axis
   // short axis is axis "1" (not "0"; causes complications when
   // we have a fieldAxis, which is at 0).
   private AxisInterface getShortAxis () {
      AxisInterface shortAxis = null;

      List axisList = getAxes();
      if (axisList.size() > 1) {
         shortAxis = (AxisInterface) axisList.get(1);
      }
      return shortAxis;
   }

   public int getShortAxisSize () {

      AxisInterface shortAxis = getShortAxis();
      if (shortAxis != null) {
         return shortAxis.getLength();
      } else {
         return -1;
      }
   }



   
   // Need to do this operation after every axis add
   // reset to the current order of the axes.
   // Note: IF there where lots of axes in an object
   // then this could become a real processing bottleneck
   private void updateNotesLocationOrder () {
   
         List axisList = getAxes();
         ArrayList axisIdList = new ArrayList();
   
         // assemble the list of axisId's
         Iterator iter = axisList.iterator();
         while (iter.hasNext() ) {
            AxisInterface axisObj = (AxisInterface) iter.next();
            String axisIdRef = axisObj.getAxisId();
            ((ArrayList) axisIdList).add(axisIdRef);
         }
   
         Notes notesObj = getArrayNotes();
         notesObj.setLocationOrderList(axisIdList);
   }
   
}  //end of Array class

/**
  * Modification History:
  * $Log$
  * Revision 1.35  2001/08/01 18:00:48  huang
  * allow setData () for object to have recursive ability for multi-dim array and added more convenience methods for setData() for other types
  *
  * Revision 1.34  2001/07/11 22:35:21  thomas
  * Changes related to adding valueList or removeal of unneeded interface files.
  *
  * Revision 1.33  2001/07/02 21:19:29  thomas
  * changed attribs to match XDF DTD: now has notAnumber,
  * disabledValue, overflow/underflowValue.
  *
  * Revision 1.32  2001/06/26 21:22:25  huang
  * changed return type to boolean for all addObject()
  *
  * Revision 1.31  2001/06/26 19:44:58  thomas
  * added stuff to allow updating of dataCube in situations
  * where the axis size has changed.
  *
  * Revision 1.30  2001/06/19 15:51:06  thomas
  * minor edit
  *
  * Revision 1.29  2001/06/18 21:41:33  thomas
  * removed extraneous DEBUG statements. Revamp of
  * addAxis and setFieldAxis for new dataCube design.
  *
  * Revision 1.28  2001/06/18 17:08:40  thomas
  * fixed setAxisList bug. Removed extraneous methods
  * of incrementDimension, decrementDimension. Small
  * (other) changes to reflect new DataCube storage scheme.
  *
  * Revision 1.27  2001/05/24 17:23:54  huang
  * revived setAxisList() and other minor changes
  *
  * Revision 1.26  2001/05/16 22:47:30  huang
  * added/modified several conveniencemethods
  *
  * Revision 1.25  2001/05/15 23:20:49  huang
  * added a few convenience methods
  *
  * Revision 1.24  2001/05/04 20:15:59  thomas
  * setNotesObject method is now setArrayNotes
  * Implement Interface stuff in Array.
  * changed baseObject class to baseObjectWIthXMLElements.
  * lessthan, etc methods now take OBject rather than String.
  *
  * Revision 1.23  2001/05/02 18:14:54  thomas
  * Minor changes related to API standardization
  * effort.
  *
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

