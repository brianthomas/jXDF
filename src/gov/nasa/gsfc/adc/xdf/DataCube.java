// XDF DataCube Class
// CVS $Id$

// DataCube.java Copyright (C) 2000 Brian Thomas,
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

import java.util.*;
import java.lang.reflect.*;

/**DataCube.java:
 * @version $Revision$
 *
 */


public class DataCube extends BaseObject {

 //
 //Fields
 //
 protected Array parentArray;

 //to store the n-dimensional data, it is an ArrayList of ArrayList, whose
 //innermost layer contains two kinds of arrays:
 //--an array of bytes to indicate if its corresponding cell contains valid data
 //(Java fills all int[] and double[] with 0 once constructed )
 //--an array of primative types or an array of Strings depending depending on
 //the actual data
 protected List data = Collections.synchronizedList(new ArrayList());

 protected Locator currentLocator;  //double check, have to relate it to sth.



  //
  // Constructor and related methods
  //

  /** The constructor that takes parentArray as param.
   */
  public DataCube(Array parentArray) {
    this.parentArray = parentArray;
    init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  /**not needed, DataCube is always associated with an Array class, double check
  public DataCube ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  } */


 /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "data";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"maxDimensionIndex");
    attribOrder.add(0,"dimension");
    attribOrder.add(0,"compression");
    attribOrder.add(0,"checksum");
    attribOrder.add(0,"href");

     //set up the attribute hashtable key with the default initial value
    attribHash.put("maxDimensionIndex", new XMLAttribute(Collections.synchronizedList(new ArrayList()), Constants.LIST_TYPE));
    attribHash.put("dimension", new XMLAttribute(new Integer(0), Constants.NUMBER_TYPE));
    attribHash.put("compression", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("checksum", new XMLAttribute(new Double(0), Constants.NUMBER_TYPE));  //double check
    attribHash.put("href", new XMLAttribute(null, Constants.STRING_TYPE));

  };




  private Number setDimension(Number dimension) {
    return (Number) ((XMLAttribute)attribHash.get("dimension")).setAttribValue(dimension);
  }

  public Number getDimension() {
     return (Number) ((XMLAttribute) attribHash.get("dimension")).getAttribValue();
  }

  public Array setParentArray(Array parentArray) {
    Log.debug("in DataCube, setParentArray()");
    this.parentArray = parentArray;
    return parentArray;
  }

  public Array getParentArray() {
    return parentArray;
  }

  public List getMaxDataIndex() {
    Log.debug("in DataCube, getMaxDataIndex(), function body empty, returning null now");
    return null;
  }




  //
  //other PUBLIC methods
  //

  /**incrementDimension: increase the dimension by 1
   * @return: the current dimension ( which is incremented)
   * this is call after the Array the DataCube belongs to added an Axis
   */
  public Number incrementDimension(Axis axis) {
    Number dim = getDimension();
    if (dim.intValue()==0) {  //add first dimension
      data.add(null);
      data.add(null);
      return  setDimension(new Integer(1));
    }

    if (dim.intValue() == 1) {  //add second dimension
      int length = axis.getLength();
      for (int i = 2; i < 2*length; i++)
        data.add(i,null);
    }
    else{  //dimension >= 2
      List oldData = data;
      int length = axis.getLength();
      data = Collections.synchronizedList(new ArrayList(length));
      data.add(0,oldData);
      for (int i = 1; i < length; i++)
        data.add(i,null);
    }
    return setDimension(new Integer(dim.intValue()+1));
  }

  /**decrementDimension: decrease the dimension by 1
   * @return: the current dimension ( which is decremented)
   */

   //not right, have to write again, double check implications of dataCube
  public Number decrementDimension() {
    Number dim = getDimension();
    if (dim == null) {
      Log.error(" in DataCube, incrementDimentsion, the dimension is undef");
      return null;
    }
    else
      return setDimension(new Integer(dim.intValue()-1));
  }

  public String getStringData(Locator locator) throws NoDataException{
    List axisList = parentArray.getAxisList();
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      int index = locator.getAxisLocation((Axis) axisList.get(0));
      try {
       if (java.lang.reflect.Array.getByte(data.get(0), index) !=1)
        throw new NoDataException();
        return java.lang.reflect.Array.get(data.get(1), index).toString();
     }
      catch (Exception e) {  //the location we try to access is not allocated,
                             //i.e., no data in the cell
        throw new NoDataException();
      }
    }

    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      current = (List) current.get(index);
      if (current == null) {  //the location we try to access is not allocated, no data
         throw new NoDataException();
      }
    }

    int index0 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-2));
    int index1 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-1));
    try {
      if (java.lang.reflect.Array.getByte(current.get(2*index0), index1) !=1)
        throw new NoDataException();  //the location we try to access contains noDataValue

      return  java.lang.reflect.Array.get(current.get(2*index0+1), index1).toString();
    }
    catch (Exception e) {  //the location we try to access is not allocated,
                           //i.e., no data in the cell
      throw new NoDataException();
    }

  }

   public int getIntData(Locator locator) throws NoDataException{
    List axisList = parentArray.getAxisList();
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      int index = locator.getAxisLocation((Axis) axisList.get(0));
      try {
       if (java.lang.reflect.Array.getByte(data.get(0), index) !=1)
        throw new NoDataException();
        return java.lang.reflect.Array.getInt(data.get(1), index);
     }
      catch (Exception e) {  //the location we try to access is not allocated,
                             //i.e., no data in the cell
        throw new NoDataException();
      }
    }

    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      current = (List) current.get(index);
      if (current == null) {  //the location we try to access is not allocated, no data
         throw new NoDataException();
      }
    }

    int index0 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-2));
    int index1 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-1));
    try {
      if (java.lang.reflect.Array.getByte(current.get(2*index0), index1) !=1)
        throw new NoDataException();  //the location we try to access contains noDataValue

      return  java.lang.reflect.Array.getInt(current.get(2*index0+1), index1);
    }
    catch (Exception e) {  //the location we try to access is not allocated,
                           //i.e., no data in the cell
      throw new NoDataException();
    }

  }

  public double getDoubleData(Locator locator) throws NoDataException{
    List axisList = parentArray.getAxisList();
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      int index = locator.getAxisLocation((Axis) axisList.get(0));
      try {
       if (java.lang.reflect.Array.getByte(data.get(0), index) !=1)
        throw new NoDataException();
        return java.lang.reflect.Array.getDouble(data.get(1), index);
     }
      catch (Exception e) {  //the location we try to access is not allocated,
                             //i.e., no data in the cell
        throw new NoDataException();
      }
    }

    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      current = (List) current.get(index);
      if (current == null) {  //the location we try to access is not allocated, no data
         throw new NoDataException();
      }
    }

    int index0 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-2));
    int index1 = locator.getAxisLocation((Axis) axisList.get(numOfAxis-1));
    try {
      if (java.lang.reflect.Array.getByte(current.get(2*index0), index1) !=1)
        throw new NoDataException();  //the location we try to access contains noDataValue

      return  java.lang.reflect.Array.getDouble(current.get(2*index0+1), index1);
    }
    catch (Exception e) {  //the location we try to access is not allocated,
                           //i.e., no data in the cell
      throw new NoDataException();
    }

  }


   /**addData: Append the SCALAR value onto the requested datacell
   */
  public double  addData (Locator locator, double numValue) {
    Log.error("in DataCube, addData(), function body empty, returning 0");
    return 0;
  }

  /**addData: Append the SCALAR value onto the requested datacell
    */
  public String addData (Locator locator, String strValue) {
   Log.error("in DataCube, addData(), function body empty, returning null");
   return null;
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<XDF::DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */

   public double  setData (Locator locator, double numValue) throws SetDataException{
    List axisList = parentArray.getAxisList();
    List prev = data;
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      Axis axis = (Axis) axisList.get(0);
      int index = locator.getAxisLocation(axis);

      if (data.get(0) == null) {
        //used to track if the corresponding cell stores valid data
        data.set(0,new byte[axis.getLength()]);
        data.set(1,new double[axis.getLength()]);
      }

      int arrayLength = ((byte[]) data.get(0)).length;
      if ( arrayLength< index+1) {  //have to expand the array
        int toExpandLength = (int) (arrayLength*1.3)+1;  //use 1.3 as the size multiplier
        if (toExpandLength < index) {  //expand to the size of index (index > arrayLength*1.3)
          toExpandLength = index+1;
        }

        //copy the old byte array into the expanded one
        byte[] oldByteArray = (byte[]) current.get(0);
        byte[] newByteArray = new byte[toExpandLength];
        for (int k = 0; k <oldByteArray.length; k++)
          newByteArray[k]=oldByteArray[k];
        current.set(0, newByteArray);
        oldByteArray = null; //force garbage collection

        //copy the old int array into the expanded one
        double[] oldArray = (double[]) current.get(1);
        double[] newArray = new double[toExpandLength];
        for (int k = 0; k <oldArray.length; k++)
        newArray[k]=oldArray[k];
        current.set(1, newArray);
        oldArray = null; //force garbage collection
      }
      try {
        //indicates its corresponding data cell holds vaid data
        java.lang.reflect.Array.setByte(current.get(0), index, Byte.parseByte("1"));

        //put the data into the requested data cell
        java.lang.reflect.Array.setDouble(current.get(1), index, numValue);
        return numValue;
      }
      catch (Exception e) {
        throw new SetDataException();
      }
    } //  end of if (numOfAxis == 1)

    //contructs arraylist of arraylist to represent the multi-dimension
    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      int end = axis.getLength() - prev.size();
      for (i = 0; i < end ; i++)  //expand it if prev.size < index+1
        prev.add(null);
      current = (List) prev.get(index);
      if (current == null) {  //expand the datacube
        int length = ((Axis) axisList.get(i+1)).getLength();
        current = new ArrayList(length);
        for (int j = 0; j < length; j++)
          current.add(null);
        prev.set(index, current);
      }
      prev = current;
    }

    //special handling of the inner most two dimensions
    Axis lastTo2ndAxis = (Axis) axisList.get(numOfAxis-2);
    Axis lastAxis = (Axis) axisList.get(numOfAxis-1);
    int index0 = locator.getAxisLocation(lastTo2ndAxis );
    int index1 = locator.getAxisLocation(lastAxis );

    int stop = 2*(index0+1)-current.size();
    for (int i = 0; i<stop; i++) {  //expand it if current.size < 2*(index0+1)
        current.add(null);
    }

    int newCoordinate = 2*index0;  //internal index  = 2*index0
    if (current.get(newCoordinate) == null) {
      //expand array of byte and int
      current.set(newCoordinate, new byte[lastAxis.getLength()]);
      current.set(newCoordinate+1, new double[lastAxis.getLength()]);
    }

    int arrayLength = ((double[]) current.get(newCoordinate+1)).length;
    if ( arrayLength< index1+1) {  ////have to expand the array
      int toExpandLength = (int) (arrayLength*1.3)+1;
      if (toExpandLength < index1) {
        toExpandLength = index1+1;
      }

      //copy old byte array to the expanded new one
      byte[] oldByteArray = (byte[]) current.get(newCoordinate);
      byte[] newByteArray = new byte[toExpandLength];
      for (int k = 0; k <oldByteArray.length; k++)
      newByteArray[k]=oldByteArray[k];
      current.set(newCoordinate, newByteArray);
      oldByteArray = null; //force garbage collection

      //copy old int array to the expanded new one
      double[] oldArray = (double[]) current.get(newCoordinate+1);
      double[] newArray = new double[toExpandLength];
      for (int k = 0; k <oldArray.length; k++)
      newArray[k]=oldArray[k];
      current.set(newCoordinate+1, newArray);
      oldArray = null; //force garbage collection
    }
    try { //set data
      byte realValue = 1;
      //indicate its corresponding datacell holds valid data
      java.lang.reflect.Array.setByte(current.get(newCoordinate), index1, realValue);
      //put data into the requested datacell
      java.lang.reflect.Array.setDouble(current.get(newCoordinate+1), index1, numValue);
      return numValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<XDF::DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
  public int setData(Locator locator, int numValue) throws SetDataException{
    List axisList = parentArray.getAxisList();
    List prev = data;
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      Axis axis = (Axis) axisList.get(0);
      int index = locator.getAxisLocation(axis);

      if (data.get(0) == null) {
        //used to track if the corresponding cell stores valid data
        data.set(0,new byte[axis.getLength()]);
        data.set(1,new int[axis.getLength()]);
      }

      int arrayLength = ((byte[]) data.get(0)).length;
      if ( arrayLength< index+1) {  //have to expand the array
        int toExpandLength = (int) (arrayLength*1.3)+1;  //use 1.3 as the size multiplier
        if (toExpandLength < index) {  //expand to the size of index (index > arrayLength*1.3)
          toExpandLength = index+1;
        }

        //copy the old byte array into the expanded one
        byte[] oldByteArray = (byte[]) current.get(0);
        byte[] newByteArray = new byte[toExpandLength];
        for (int k = 0; k <oldByteArray.length; k++)
          newByteArray[k]=oldByteArray[k];
        current.set(0, newByteArray);
        oldByteArray = null; //force garbage collection

        //copy the old int array into the expanded one
        int[] oldArray = (int[]) current.get(1);
        int[] newArray = new int[toExpandLength];
        for (int k = 0; k <oldArray.length; k++)
        newArray[k]=oldArray[k];
        current.set(1, newArray);
        oldArray = null; //force garbage collection
      }
      try {
        //indicates its corresponding data cell holds vaid data
        java.lang.reflect.Array.setByte(current.get(0), index, Byte.parseByte("1"));

        //put the data into the requested data cell
        java.lang.reflect.Array.setInt(current.get(1), index, numValue);
        return numValue;
      }
      catch (Exception e) {
        throw new SetDataException();
      }
    } //  end of if (numOfAxis == 1)

    //contructs arraylist of arraylist to represent the multi-dimension
    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      int end = axis.getLength() - prev.size();
      for (i = 0; i < end ; i++)  //expand it if prev.size < index+1
        prev.add(null);
      current = (List) prev.get(index);
      if (current == null) {  //expand the datacube
        int length = ((Axis) axisList.get(i+1)).getLength();
        current = new ArrayList(length);
        for (int j = 0; j < length; j++)
          current.add(null);
        prev.set(index, current);
      }
      prev = current;
    }

    //special handling of the inner most two dimensions
    Axis lastTo2ndAxis = (Axis) axisList.get(numOfAxis-2);
    Axis lastAxis = (Axis) axisList.get(numOfAxis-1);
    int index0 = locator.getAxisLocation(lastTo2ndAxis );
    int index1 = locator.getAxisLocation(lastAxis );

    int stop = 2*(index0+1)-current.size();
    for (int i = 0; i<stop; i++) {  //expand it current.size < 2*(index0+1)
        current.add(null);
    }

    int newCoordinate = 2*index0;  //internal index  = 2*index0
    if (current.get(newCoordinate) == null) {
      //expand array of byte and int
      current.set(newCoordinate, new byte[lastAxis.getLength()]);
      current.set(newCoordinate+1, new int[lastAxis.getLength()]);
    }

    int arrayLength = ((int[]) current.get(newCoordinate+1)).length;
    if ( arrayLength< index1+1) {  ////have to expand the array
      int toExpandLength = (int) (arrayLength*1.3)+1;
      if (toExpandLength < index1) {
        toExpandLength = index1+1;
      }

      //copy old byte array to the expanded new one
      byte[] oldByteArray = (byte[]) current.get(newCoordinate);
      byte[] newByteArray = new byte[toExpandLength];
      for (int k = 0; k <oldByteArray.length; k++)
      newByteArray[k]=oldByteArray[k];
      current.set(newCoordinate, newByteArray);
      oldByteArray = null; //force garbage collection

      //copy old int array to the expanded new one
      int[] oldArray = (int[]) current.get(newCoordinate+1);
      int[] newArray = new int[toExpandLength];
      for (int k = 0; k <oldArray.length; k++)
      newArray[k]=oldArray[k];
      current.set(newCoordinate+1, newArray);
      oldArray = null; //force garbage collection
    }
    try { //set data
      byte realValue = 1;
      //indicate its corresponding datacell holds valid data
      java.lang.reflect.Array.setByte(current.get(newCoordinate), index1, realValue);
      //put data into the requested datacell
      java.lang.reflect.Array.setInt(current.get(newCoordinate+1), index1, numValue);
      return numValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  }

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<XDF::DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
  public String  setData (Locator locator, String strValue) throws SetDataException{
    List axisList = parentArray.getAxisList();
    List prev = data;
    List current = data;
    int numOfAxis = axisList.size();
    if (numOfAxis == 1) {
      Axis axis = (Axis) axisList.get(0);
      int index = locator.getAxisLocation(axis);

      if (data.get(0) == null) {
        //used to track if the corresponding cell stores valid data
        data.set(0,new byte[axis.getLength()]);
        data.set(1,new String[axis.getLength()]);
      }

      int arrayLength = ((byte[]) data.get(0)).length;
      if ( arrayLength< index+1) {  //have to expand the array
        int toExpandLength = (int) (arrayLength*1.3)+1;  //use 1.3 as the size multiplier
        if (toExpandLength < index) {  //expand to the size of index (index > arrayLength*1.3)
          toExpandLength = index+1;
        }

        //copy the old byte array into the expanded one
        byte[] oldByteArray = (byte[]) current.get(0);
        byte[] newByteArray = new byte[toExpandLength];
        for (int k = 0; k <oldByteArray.length; k++)
          newByteArray[k]=oldByteArray[k];
        current.set(0, newByteArray);
        oldByteArray = null; //force garbage collection

        //copy the old int array into the expanded one
        String[] oldArray = (String[]) current.get(1);
        String[] newArray = new String[toExpandLength];
        for (int k = 0; k <oldArray.length; k++)
        newArray[k]=oldArray[k];
        current.set(1, newArray);
        oldArray = null; //force garbage collection
      }
      try {
        //indicates its corresponding data cell holds vaid data
        java.lang.reflect.Array.setByte(current.get(0), index, Byte.parseByte("1"));

        //put the data into the requested data cell
        java.lang.reflect.Array.set(current.get(1), index, strValue);
        return strValue;
      }
      catch (Exception e) {
        throw new SetDataException();
      }
    } //  end of if (numOfAxis == 1)

    //contructs arraylist of arraylist to represent the multi-dimension
    for (int i = 0; i < numOfAxis-2; i++) {
      Axis axis = (Axis) axisList.get(i);
      int index =  locator.getAxisLocation(axis);
      int end = axis.getLength() - prev.size();
      for (i = 0; i < end ; i++)  //expand it if prev.size < index+1
        prev.add(null);
      current = (List) prev.get(index);
      if (current == null) {  //expand the datacube
        int length = ((Axis) axisList.get(i+1)).getLength();
        current = new ArrayList(length);
        for (int j = 0; j < length; j++)
          current.add(null);
        prev.set(index, current);
      }
      prev = current;
    }

    //special handling of the inner most two dimensions
    Axis lastTo2ndAxis = (Axis) axisList.get(numOfAxis-2);
    Axis lastAxis = (Axis) axisList.get(numOfAxis-1);
    int index0 = locator.getAxisLocation(lastTo2ndAxis );
    int index1 = locator.getAxisLocation(lastAxis );

    int stop = 2*(index0+1)-current.size();
    for (int i = 0; i<stop; i++) {  //expand it if current.size < 2*(index0+1)
        current.add(null);
    }

    int newCoordinate = 2*index0;  //internal index  = 2*index0
    if (current.get(newCoordinate) == null) {
      //expand array of byte and int
      current.set(newCoordinate, new byte[lastAxis.getLength()]);
      current.set(newCoordinate+1, new String[lastAxis.getLength()]);
    }

    int arrayLength = ((String[]) current.get(newCoordinate+1)).length;
    if ( arrayLength< index1+1) {  ////have to expand the array
      int toExpandLength = (int) (arrayLength*1.3)+1;
      if (toExpandLength < index1) {
        toExpandLength = index1+1;
      }

      //copy old byte array to the expanded new one
      byte[] oldByteArray = (byte[]) current.get(newCoordinate);
      byte[] newByteArray = new byte[toExpandLength];
      for (int k = 0; k <oldByteArray.length; k++)
      newByteArray[k]=oldByteArray[k];
      current.set(newCoordinate, newByteArray);
      oldByteArray = null; //force garbage collection

      //copy old int array to the expanded new one
      String[] oldArray = (String[]) current.get(newCoordinate+1);
      String[] newArray = new String[toExpandLength];
      for (int k = 0; k <oldArray.length; k++)
      newArray[k]=oldArray[k];
      current.set(newCoordinate+1, newArray);
      oldArray = null; //force garbage collection
    }
    try { //set data
      byte realValue = 1;
      //indicate its corresponding datacell holds valid data
      java.lang.reflect.Array.setByte(current.get(newCoordinate), index1, realValue);
      //put data into the requested datacell
      java.lang.reflect.Array.set(current.get(newCoordinate+1), index1, strValue);
      return strValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  }

  /**removeData : Remove the requested data from the indicated datacell
   * (via L<XDF::DataCube> LOCATOR REF) in the XDF::DataCube held in this Array.
   * B<NOT CURRENTLY IMPLEMENTED>.
   */

   public double  removeData (Locator locator, double numValue) {
    Log.error("in DataCube, removeData(), function body empty, returning 0");
    return 0;
  }

 /**removeData : Remove the requested data from the indicated datacell
   * (via L<XDF::DataCube> LOCATOR REF) in the XDF::DataCube held in this Array.
   * B<NOT CURRENTLY IMPLEMENTED>.
   */
  public String  removeData (Locator locator, String strValue) {
   Log.error("in DataCube, setData(), function body empty, returning null");
   return null;
  }


}

