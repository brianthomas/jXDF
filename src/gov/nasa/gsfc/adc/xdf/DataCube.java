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

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;
import java.lang.reflect.*;
import java.io.OutputStream;

/**DataCube.java:
 * @version $Revision$
 *
 */


public class DataCube extends BaseObject {

  //
  //Fields
  //

  protected int dimension = 0;;
  protected Array parentArray;

  //to store the n-dimensional data, it is an ArrayList of ArrayList, whose
  //innermost layer contains two kinds of arrays:
  //--an array of bytes to indicate if its corresponding cell contains valid data
  //(Java fills all int[] and double[] with 0 once constructed )
  //--an array of primative types or an array of Strings depending depending on
  //the actual data
protected List data = Collections.synchronizedList(new ArrayList());

protected Locator currentLocator;

  List tags; //store the tags for writeTaggedData



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
    attribOrder.add(0,"compression");
    attribOrder.add(0, "encoding");
    attribOrder.add(0,"checksum");
    attribOrder.add(0,"href");

    //set up the attribute hashtable key with the default initial value
    attribHash.put("compression", new XMLAttribute(null, Constants.STRING_TYPE));  //double check init value
    attribHash.put("encoding", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("checksum", new XMLAttribute(null, Constants.NUMBER_TYPE));  //double check
    attribHash.put("href", new XMLAttribute(null, Constants.STRING_TYPE));

  };

  /**setHref: set the *href* attribute
   * @return: the current *href* attribute
   */
  public String setHref (String strHref)
  {
    return (String) ((XMLAttribute) attribHash.get("href")).setAttribValue(strHref);

  }

  /**getHref
   * @return: the current *href* attribute
   */
  public String getHref()
  {
    return (String) ((XMLAttribute) attribHash.get("href")).getAttribValue();
  }

  /**setChecksum: set the *checksum* attribute
   * @return: the current *checksum* attribute
   */
  public Number setChecksum (Number checksum) {
    Log.info("in DataCube.setChecksum()");
   return (Number) ((XMLAttribute) attribHash.get("checksum")).setAttribValue(checksum);
  }

  /**getChecksum
   * @return: the current *checksum* attribute
   */
public Number getChecksum () {
  return (Number) ((XMLAttribute) attribHash.get("checksum")).getAttribValue();
}

/**setEncoding: set the *encoding* attribute
   * @return: the current *encoding* attribute
   */
  public String setEncoding (String strEncoding)
  {
    if (!Utility.isValidDataEncoding(strEncoding))
      return null;
    return (String) ((XMLAttribute) attribHash.get("encoding")).setAttribValue(strEncoding);

  }

  /**getEncoding
   * @return: the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((XMLAttribute) attribHash.get("encoding")).getAttribValue();
  }


  /**setCompression: set the *compression* attribute
   * @return: the current *compression* attribute
   */
public String setCompression (String strCompression)
  {
    if (!Utility.isValidDataCompression(strCompression))
      return null;
    return (String) ((XMLAttribute) attribHash.get("compression")).setAttribValue(strCompression);

  }

  /**getCompression
   * @return: the current *compression* attribute
   */
public String getCompression()
  {
    return (String) ((XMLAttribute) attribHash.get("compression")).getAttribValue();
  }

  /**getDimension
   * @return: the current dimension
   */
public int getDimension() {
  return dimension;
}

  /**getMaxDataIndex: get the max index along with dimension
   * @return: int[]
   */
public int[] getMaxDataIndex() {
  List axes = parentArray.getAxisList();
  int[] maxDataIndices = new int[axes.size()];

  int stop = axes.size();
  for(int i = 0; i < stop; i++) {
    maxDataIndices[i]=((AxisInterface) axes.get(i)).getLength();
  }

  return maxDataIndices;
}




public Array getParentArray() {
  return parentArray;
}





  //
  //other PUBLIC methods
  //

  /**incrementDimension: increase the dimension by 1
   * @return: the current dimension ( which is incremented)
   * this is called after the Array the DataCube belongs to adds an Axis
   */
public int incrementDimension(Axis axis) {
  if (dimension==0) {  //add first dimension
    data.add(null);
    data.add(null);
    return dimension++;
  }

  if (dimension == 1) {  //add second dimension
    if (!parentArray.hasFieldAxis()) {
      int length = axis.getLength();
      for (int i = 2; i < 2*length; i++)
        data.add(i,null);
    }
  }
  else{  //dimension >= 2
    List oldData = data;
    int length = axis.getLength();
    data = Collections.synchronizedList(new ArrayList(length));
    data.add(0,oldData);
    for (int i = 1; i < length; i++)
      data.add(i,null);
  }
  return dimension++;
}

/**incrementDimension: increase the dimension by 1
   * @return: the current dimension ( which is incremented)
   * this is called after the Array the DataCube belongs to adds an Axis
   */
public int incrementDimension(FieldAxis fieldAxis) {
  if (dimension==0) {  //add first dimension
    int length = fieldAxis.getLength();
    for (int i = 0; i < 2*length; i++) {
      data.add(null);
    }
    return dimension++;
  }
  else {
    return -1;
  }
}

  /**decrementDimension: decrease the dimension by 1
   * @return: the current dimension ( which is decremented)
   */

  //not right, have to write again, double check implications of dataCube
public int decrementDimension() {
  if (dimension == 0) {
    Log.error(" in DataCube, incrementDimentsion, the dimension is 0");
    return 0;
  }
  else
   return dimension--;
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

  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    current = (List) current.get(index);
    if (current == null) {  //the location we try to access is not allocated, no data
      throw new NoDataException();
    }
  }

  //special handling for the innermost two layers
  int index0;
  int index1;
  if (parentArray.hasFieldAxis()) {
    //FieldAxis is always the second to last innermost layer
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1));
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

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

  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    current = (List) current.get(index);
    if (current == null) {  //the location we try to access is not allocated, no data
      throw new NoDataException();
    }
  }

  //special handling for the innermost two layers
  int index0;
  int index1;
  if (parentArray.hasFieldAxis()) {
    //FieldAxis is always the second to last innermost layer
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1));
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

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

  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    current = (List) current.get(index);
    if (current == null) {  //the location we try to access is not allocated, no data
      throw new NoDataException();
    }
  }

 //special handling for the innermost two layers
  int index0;
  int index1;
  if (parentArray.hasFieldAxis()) {
    //FieldAxis is always the second to last innermost layer
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1));
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

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




  /**appendData: Append the String value onto the requested datacell
   * double check: now to prevent the user from appending to an int or double?
   */
  public String appendData (Locator locator, String strValue) throws SetDataException{
    String strData;
    try {
      strData = getStringData(locator);
      strData +=strValue;
    }
    catch (NoDataException e) {
      strData = strValue;
    }
    return setData(locator, strData);
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
      byte b = 1;
      java.lang.reflect.Array.setByte(current.get(0), index, b);

      //put the data into the requested data cell
      java.lang.reflect.Array.setDouble(current.get(1), index, numValue);
      return numValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    int end = axis.getLength() - prev.size();
    for (int k = 0; k < end ; k++)  //expand it if prev.size < index+1
      prev.add(null);
    current = (List) prev.get(index);
    if (current == null) {  //expand the datacube
      int length = ((Axis) axisList.get(i-1)).getLength();
      current = new ArrayList(length);
      for (int j = 0; j < length; j++)
	current.add(null);
      prev.set(index, current);
    }
    prev = current;
  }

  //special handling of the inner most two dimensions
  int index0;
  int index1;
  if (parentArray.hasFieldAxis()) {
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1) );
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

  int stop = 2*(index0+1)-current.size();
  for (int i = 0; i<stop; i++) {  //expand it if current.size < 2*(index0+1)
    current.add(null);
  }

  int newCoordinate = 2*index0;  //internal index  = 2*index0
  if (current.get(newCoordinate) == null) {
    int length;
    //expand array of byte and String
    if (parentArray.hasFieldAxis())
      length= ((Axis) axisList.get(1)).getLength();
    else
      length = ((Axis) axisList.get(0)).getLength();
    current.set(newCoordinate, new byte[length]);
    current.set(newCoordinate+1, new double[length]);
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
      byte b = 1;
      java.lang.reflect.Array.setByte(current.get(0), index, b);

      //put the data into the requested data cell
      java.lang.reflect.Array.setInt(current.get(1), index, numValue);
      return numValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    int end = axis.getLength() - prev.size();
    for (int k = 0; k < end ; k++)  //expand it if prev.size < index+1
      prev.add(null);
    current = (List) prev.get(index);
    if (current == null) {  //expand the datacube
      int length = ((Axis) axisList.get(i-1)).getLength();
      current = new ArrayList(length);
      for (int j = 0; j < length; j++)
	current.add(null);
      prev.set(index, current);
    }
    prev = current;
  }

 //special handling for the innermost two layer
 int index0;
 int index1;
 if (parentArray.hasFieldAxis()) { //fieldAxis is always the 2nd to last layer
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1) );
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

  int stop = 2*(index0+1)-current.size();
  for (int i = 0; i<stop; i++) {  //expand it if current.size < 2*(index0+1)
    current.add(null);
  }

  int newCoordinate = 2*index0;  //internal index  = 2*index0
  if (current.get(newCoordinate) == null) {
    int length;
    //expand array of byte and int
    if (parentArray.hasFieldAxis())
      length= ((Axis) axisList.get(1)).getLength();
    else
      length = ((Axis) axisList.get(0)).getLength();
    current.set(newCoordinate, new byte[length]);
    current.set(newCoordinate+1, new int[length]);

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
      byte b = 1;
      java.lang.reflect.Array.setByte(current.get(0), index, b);

      //put the data into the requested data cell
      java.lang.reflect.Array.set(current.get(1), index, strValue);
      return strValue;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisLocation(axis);
    int end = axis.getLength() - prev.size();
    for (int k = 0; k < end ; k++)  //expand it if prev.size < index+1
      prev.add(null);
    current = (List) prev.get(index);
    if (current == null) {  //expand the datacube
      int length = ((Axis) axisList.get(i-1)).getLength();
      current = new ArrayList(length);
      for (int j = 0; j < length; j++)
	current.add(null);
      prev.set(index, current);
    }
    prev = current;
  }

  //special handling of the inner most two dimensions
  int index0;
  int index1;

  if (parentArray.hasFieldAxis()) {
    index0 = locator.getAxisLocation((FieldAxis) axisList.get(0));
    index1 = locator.getAxisLocation((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisLocation((Axis) axisList.get(1) );
    index1 = locator.getAxisLocation((Axis) axisList.get(0));
  }

  int stop = 2*(index0+1)-current.size();
  for (int i = 0; i<stop; i++) {  //expand it if current.size < 2*(index0+1)
    current.add(null);
  }

  int newCoordinate = 2*index0;  //internal index  = 2*index0
  if (current.get(newCoordinate) == null) {
    int length;
    //expand array of byte and String
    if (parentArray.hasFieldAxis())
      length= ((Axis) axisList.get(1)).getLength();
    else
      length = ((Axis) axisList.get(0)).getLength();
    current.set(newCoordinate, new byte[length]);
    current.set(newCoordinate+1, new String[length]);

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

public void toXDFOutputStream (
			       OutputStream outputstream,
			       Hashtable XMLDeclAttribs,
			       String strIndent
			       )
  {
    boolean niceOutput = sPrettyXDFOutput;
    String indent = "";
    indent = indent + strIndent;

    String nodeName = getClassXDFNodeName();

    //open node
    if (niceOutput)
      writeOut(outputstream, indent);

    writeOut(outputstream, "<" + nodeName );
    String href = getHref();
    if (href !=null)
      writeOut(outputstream, " href = \"" + href + "\"");

    Number checksum = getChecksum();
    if (checksum !=null)
      writeOut(outputstream, " checksum = \"" + checksum.toString() + "\"");

    writeOut(outputstream, ">");  //end of opening code

    //write out just the data
    XMLDataIOStyle readObj = parentArray.getXMLDataIOStyle();
    if (href !=null) {  //write out to another file, double check
    }
    currentLocator = parentArray.createLocator();

    AxisInterface fastestAxis = (AxisInterface) parentArray.getAxisList().get(0);
    //stores the NoDataValues for the parentArray,
    //used in writing out when NoDataException is caught
    String NoDataValues[] = new String[fastestAxis.getLength()];
    if (parentArray.hasFieldAxis()) {
      DataFormat[] dataFormatList = parentArray.getDataFormatList();
      for (int i = 0; i < NoDataValues.length; i++) {
        DataFormat d =  dataFormatList[i];
        if (d != null && d.getNoDataValue() != null)
          NoDataValues[i]=d.getNoDataValue().toString();
      }
    }
    else {
      DataFormat d = parentArray.getDataFormat();
      for (int i = 0; i < NoDataValues.length; i++) {
        if (d!=null)
          NoDataValues[i] = d.getNoDataValue().toString();
      }
    }

    if (readObj instanceof TaggedXMLDataIOStyle) {
      String[] tagOrder = ((TaggedXMLDataIOStyle)readObj).getAxisTags();
      int stop = tagOrder.length;
      String[] tags = new String[stop];

      for (int i = stop-1; i >= 0 ; i--) {
        tags[stop-i-1]  = tagOrder[i];
	// System.out.println(tagOrder.get(i));
      }

      int[] axes = getMaxDataIndex();
      stop =axes.length;
      int[] axisLength = new int[stop];
      for (int i = 0; i < stop; i++) {
        axisLength[i] =axes[stop - 1 - i];
      }
      writeTaggedData(outputstream, currentLocator, indent, axisLength, tags, 0, fastestAxis, NoDataValues);

    }  //done dealing with with TaggedXMLDataIOSytle
    else {
       if (readObj instanceof DelimitedXMLDataIOStyle) {
           writeDelimitedData( outputstream, currentLocator, 
                               (DelimitedXMLDataIOStyle) readObj, 
                               fastestAxis, NoDataValues );

       }
    }

    //close the data section appropriately 
    if (niceOutput) {
      writeOut(outputstream, Constants.NEW_LINE);
      writeOut(outputstream, indent);
    }

    writeOut(outputstream, "</" + nodeName + ">");
    if (niceOutput)
      writeOut(outputstream, Constants.NEW_LINE);

  }


  /**  write data with no less than 3D, using recursion, expansive, data with no more
       than 3D has special methods, data retrieval is ok,
    */

protected void writeTaggedData(OutputStream outputstream,
			       Locator locator,
			       String indent,
			       int[] axisLength,
			       String[] tags,
			       int which,
                               AxisInterface fastestAxis,
                               String[] noDataValues)
  {

    String tag = (String) tags[which];
    if (sPrettyXDFOutput) {
      indent += sPrettyXDFOutputIndentation;
    }

    //base case (writes the last 2 inner dimensions of the data cube)
    if (which == tags.length-2) {
      int stop = axisLength[which];
      String tag1 = (String) tags[which+1];
      for (int count = 0; count < stop; count++) {
	if (sPrettyXDFOutput) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "<" + tag + ">");
	if (sPrettyXDFOutput) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent + sPrettyXDFOutputIndentation);
	}

	int fastestAxisLength = fastestAxis.getLength();
	int dataNum = 0;
	while (dataNum < fastestAxisLength) {
          writeOut( outputstream, "<" + tag1 );
  	  try {
             writeOut( outputstream, ">" + getStringData(locator));
             writeOut( outputstream, "</" + tag1 + ">");
          }
          catch (NoDataException e) {
             // opps! no data in that location. Print out accordingly
             String noDataValueString = noDataValues[locator.getAxisLocation(fastestAxis)];
             if (noDataValueString != null) 
             { 
                writeOut(outputstream, ">" + noDataValueString );
                writeOut( outputstream, "</" + tag1 + ">");
             } else 
                writeOut( outputstream, "/>");
	  }

	  dataNum ++;
	  locator.next();
	}
	if (sPrettyXDFOutput) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "</" + tag+ ">");

      }
    }
    else {
      // the 'outer' data tag wrapper. writes dimension 3 or higher tags
      int stop = axisLength[which];
      which++;
      for (int i = 0; i < stop; i++) {
	if (sPrettyXDFOutput) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "<" + tag + ">");
	writeTaggedData(outputstream, locator, indent, axisLength, tags, which, fastestAxis, noDataValues);
	if (sPrettyXDFOutput) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "</" + tag + ">");
      }
    }
  }

  protected void writeDelimitedData(OutputStream outputstream,
                                    Locator locator,
                                    DelimitedXMLDataIOStyle readObj,
                                    AxisInterface fastestAxis, String[] noDataValues) {
    String delimiter = readObj.getDelimiter();
    String recordTerminator = readObj.getRecordTerminator();
    int fastestAxisLength = fastestAxis.getLength();
    int dataNum = 0;

    writeOut(outputstream, "<![CDDATA[");
    do {
      dataNum ++;
      try {
        writeOut(outputstream, getStringData(locator));
        writeOut(outputstream, delimiter);
      }
      catch (NoDataException e) {  //double check, a bug here, "yes" is already printed
        String noData = noDataValues[locator.getAxisLocation(fastestAxis)];
        if (noData == null)
          readObj.setRepeatable("no");
        writeOut(outputstream, noData);
        writeOut(outputstream, delimiter);
      }
      if (dataNum == fastestAxisLength) {
        writeOut(outputstream, recordTerminator);
        dataNum = 0;
      }
    }
    while (locator.next());
    writeOut(outputstream, "]]>");
  }

}

 /**
  * Modification History:
  * $Log$
  * Revision 1.7  2000/11/01 16:28:25  thomas
  * Updated taggedIOsection to write out noDataValued
  * data that has no noDataValue defined to be an empty
  * tag. -b.t.
  *
  * Revision 1.6  2000/10/31 21:37:18  kelly
  * --completed *toXDF* for delimited IO style.
  * --added NoDataException handling  -k.z.
  *
  * Revision 1.5  2000/10/30 18:16:24  kelly
  * changed are made for relevant FieldAxis stuff.  -k.z.
  *
  *
  */






