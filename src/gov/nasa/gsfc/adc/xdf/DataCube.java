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
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**Holds the data for a given Array object. It is designed
 *to flexibly expand as data is added/appended onto it.
 * @version $Revision$
 *
 */
public class DataCube extends BaseObject {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String CHECKSUM_XML_ATTRIBUTE_NAME = "checksum";
  private static final String COMPRESSION_TYPE_XML_ATTRIBUTE_NAME = "compression";
  private static final String ENCODING_XML_ATTRIBUTE_NAME = "encoding";
  private static final String HREF_XML_ATTRIBUTE_NAME = "href";

  private int dimension = 0;;
  private Array parentArray;
  private boolean hasMoreData;

  /**
  to store the n-dimensional data, it is an ArrayList of ArrayList, whose
  innermost layer contains two kinds of arrays:
  --an array of bytes to indicate if its corresponding cell contains valid data
  (Java fills all int[] and double[] with 0 once constructed )
  --an array of primative types or an array of Strings depending depending on
  the actual data
  */
  private List data = Collections.synchronizedList(new ArrayList());

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


  /** set the *href* attribute
   */
  public void setHref (Href hrefObj)
  {
     ((XMLAttribute) attribHash.get(HREF_XML_ATTRIBUTE_NAME)).setAttribValue(hrefObj);
  }

  /**
   * @return the current *href* attribute
   */
  public Href getHref()
  {
     return (Href) ((XMLAttribute) attribHash.get(HREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *checksum* attribute
   */
  public void setChecksum (String checksum) {
     ((XMLAttribute) attribHash.get(CHECKSUM_XML_ATTRIBUTE_NAME)).setAttribValue(checksum);
  }

  /**
   * @return the current *checksum* attribute
   */
  public String getChecksum () {
     return (String) ((XMLAttribute) attribHash.get(CHECKSUM_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** set the *encoding* attribute
   */
   public void setEncoding (String strEncoding)
   {

      if (!Utility.isValidDataEncoding(strEncoding))
         Log.warnln("Encoding is not valid, ignoring request to setEncoding.");
      else
         ((XMLAttribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).setAttribValue(strEncoding);

  }

  /**
   * @return the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((XMLAttribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


  /** set the *compression* attribute
   */
  public void setCompression (String strCompression)
  {

    if (!Utility.isValidDataCompression(strCompression))
       Log.warnln("Data compression value is not valid, ignoring request to set it.");
    else
      ((XMLAttribute) attribHash.get(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(strCompression);

  }

  /**
   * @return the current *compression* attribute
   */
  public String getCompression()
  {
    return (String) ((XMLAttribute) attribHash.get(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
   * @return the current dimension
   */
  public int getDimension() {
     return dimension;
  }

  /** get the max index along with dimension
   * @return int[]
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

  /** increase the dimension by 1
   * @return the current dimension ( which is incremented)
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

/** increase the dimension by 1
   * @return the current dimension ( which is incremented)
   * this is called after the Array the DataCube belongs to adds an Axis
   * FieldAxis should always be the first axis to add
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

  /** decrease the dimension by 1
   * @return the current dimension ( which is decremented)
     <b> NOT CURRENTLY IMPLEMENTED </b>
   */

  //not right, have to write again, double check implications of dataCube
public int decrementDimension() {
  Log.errorln("in DataCube, decrementDimension(), methods empty, returning -1");
  return -1;

}


/** We return whatever object is stored in the datacell.
 */
public Object getData (Locator locator) throws NoDataException
{

  List axisList = parentArray.getAxisList();
  List current = data;
  int numOfAxis = axisList.size();

  if (numOfAxis == 1) {
    int index = locator.getAxisIndex((Axis) axisList.get(0));
    try {
      if (java.lang.reflect.Array.getByte(data.get(0), index) !=1)
        throw new NoDataException();
      return java.lang.reflect.Array.get(data.get(1), index);
    }
    catch (Exception e) {  //the location we try to access is not allocated,
      //i.e., no data in the cell
      throw new NoDataException();
    }
  }
  
  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1));
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
  }

  try {
    if (java.lang.reflect.Array.getByte(current.get(2*index0), index1) !=1)
      throw new NoDataException();  //the location we try to access contains noDataValue

    return java.lang.reflect.Array.get(current.get(2*index0+1), index1);
  }
  catch (Exception e) {  //the location we try to access is not allocated,
    //i.e., no data in the cell
    throw new NoDataException();
  }

}

/** Regardless of what type of data is stored in the data cell we return the
    String representation.
 */
public String getStringData (Locator locator) 
throws NoDataException 
{

  try {
     Object data = getData(locator);
     return data.toString();
  }
  catch (Exception e) {  //the location we try to access is not allocated,
    //i.e., no data in the cell
    throw new NoDataException();
  }

}

/** Get integer data from a requested datacell. 
 */
public int getIntData (Locator locator) 
throws NoDataException
{
  List axisList = parentArray.getAxisList();
  List current = data;
  int numOfAxis = axisList.size();
  if (numOfAxis == 1) {
    int index = locator.getAxisIndex((Axis) axisList.get(0));
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
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1));
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
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

/** get doubleprecision data from a requested datacell. 
 */
public double getDoubleData(Locator locator) throws NoDataException {

  List axisList = parentArray.getAxisList();
  List current = data;
  int numOfAxis = axisList.size();

  // one dimensional cube case
  if (numOfAxis == 1) {
    int index = locator.getAxisIndex((Axis) axisList.get(0));
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

  // multi-dimensional case
  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1));
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
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




  /**Append the String value onto the requested datacell
   */
// double check: how to prevent the user from appending to an int or double?
  public void appendData (Locator locator, String strValue) 
  throws SetDataException 
  {

    String strData;
    try {
      strData = getStringData(locator);
      strData += strValue;
    }
    catch (NoDataException e) {
      strData = strValue;
    }

    setData(locator, strData);

  }

  /** Set the value of the requested datacell. 
   *  Overwrites existing datacell value if already populated with a value.
   */
   public void setData (Locator locator, Double value) 
   throws SetDataException
   {
      setData(locator, value.doubleValue());
   }

  /** Set the value of the requested datacell. 
   *  Overwrites existing datacell value if already populated with a value.
   */
   public void setData (Locator locator, Integer value)
   throws SetDataException
   {  
      setData(locator, value.intValue());
   }

  /** Set the value of the requested datacell. 
   *  Overwrites existing datacell value if already populated with a value.
   */

public void setData (Locator locator, double numValue) 
throws SetDataException
{

  List axisList = parentArray.getAxisList();
  List prev = data;
  List current = data;
  int numOfAxis = axisList.size();
  if (numOfAxis == 1) {
    Axis axis = (Axis) axisList.get(0);
    int index = locator.getAxisIndex(axis);

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
      return;

    }
    catch (Exception e) {
      throw new SetDataException();
    }

  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1) );
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
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
    return;
  }
  catch (Exception e) {
    throw new SetDataException();
  }

}

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
public void setData(Locator locator, int numValue) 
throws SetDataException 
{
  List axisList = parentArray.getAxisList();
  List prev = data;
  List current = data;
  int numOfAxis = axisList.size();
  if (numOfAxis == 1) {
    Axis axis = (Axis) axisList.get(0);
    int index = locator.getAxisIndex(axis);

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
      return;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1) );
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
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
    return;
  }
  catch (Exception e) {
    throw new SetDataException();
  }
}

  /** setData: Set the SCALAR value of the requested datacell
   * (via L<DataCube> LOCATOR REF).
   * Overwrites existing datacell value if any.
   */
public void setData (Locator locator, String strValue) 
throws SetDataException
{
  List axisList = parentArray.getAxisList();
  List prev = data;
  List current = data;
  int numOfAxis = axisList.size();
  if (numOfAxis == 1) {
    Axis axis = (Axis) axisList.get(0);
    int index = locator.getAxisIndex(axis);

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
      return;
    }
    catch (Exception e) {
      throw new SetDataException();
    }
  } //  end of if (numOfAxis == 1)

  //contructs arraylist of arraylist to represent the multi-dimension
  for (int i = numOfAxis-1 ; i >=2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
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
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1) );
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1) );
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
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
    return;
  }
  catch (Exception e) {
    throw new SetDataException();
  }
}

  /**removeData : Remove data from the indicated datacell
   * @param locator that indicates the location of the cell
   * @return true if indicated cell constains data,
   * false if indicated cell doesn't contain data
   */

protected boolean  removeData (Locator locator) {
  List axisList = parentArray.getAxisList();
  List current = data;
  int numOfAxis = axisList.size();
  if (numOfAxis == 1) {
    int index = locator.getAxisIndex((Axis) axisList.get(0));
    try {
      if (java.lang.reflect.Array.getByte(data.get(0), index) ==1) {
        //there is the data in the requested cell
        //remove the data
        Class arrayEltType = data.get(1).getClass().getComponentType();
        if (arrayEltType.equals(Integer.TYPE)) {
          java.lang.reflect.Array.setInt(data.get(1), index, 0);
        }
        else {
          if (arrayEltType.equals(Double.TYPE)) {
            java.lang.reflect.Array.setDouble(data.get(1), index, 0);
          }
          else {  //it is String
            java.lang.reflect.Array.set(data.get(1), index, null);
          }
        }
        //indicate the requested cell constains no data
        //put 0 in the byte array cell
        java.lang.reflect.Array.setByte(data.get(0), index, (byte) 0);
        return true;
      }
      else { //the datacell is allocated, but contains no data
        return false;
      }
    }
    catch (Exception e) {  //the location we try to access is not allocated,
      //i.e., no data in the cell
      return false;
    }
  } //end of if when numOfAxis =1

  for (int i = numOfAxis-1 ; i >= 2; i--) {
    Axis axis = (Axis) axisList.get(i);
    int index =  locator.getAxisIndex(axis);
    current = (List) current.get(index);
    if (current == null) {
      //the location we try to access is not allocated, no data
      return false;
    }
  }

  //special handling for the innermost two layers
  int index0;
  int index1;
  if (parentArray.hasFieldAxis()) {
    //FieldAxis is always the second to last innermost layer
    index0 = locator.getAxisIndex((FieldAxis) axisList.get(0));
    index1 = locator.getAxisIndex((Axis) axisList.get(1));
  }
  else {
    index0 = locator.getAxisIndex((Axis) axisList.get(1));
    index1 = locator.getAxisIndex((Axis) axisList.get(0));
  }

  try {
    //byte array that indicates if corresponding cell contains data;
    Object byteArray = current.get(2*index0);
    //array that stores the actual data
    Object dataArray =  current.get(2*index0+1);

    if (java.lang.reflect.Array.getByte(byteArray, index1) ==1) {
     //there is the data in the requested cell
     //remove the data
     Class arrayEltType = dataArray.getClass().getComponentType();
      if (arrayEltType.equals(Integer.TYPE)) {
        java.lang.reflect.Array.setInt(dataArray, index1, 0);
      }
      else {
        if (arrayEltType.equals(Double.TYPE)) {
          java.lang.reflect.Array.setDouble(dataArray, index1, 0);
        }
        else {
          java.lang.reflect.Array.set(dataArray, index1, null);
        }
      }
        //indicate the requested cell constains no data
      java.lang.reflect.Array.setByte(byteArray, index1, (byte) 0);
      return true;
    }
    else { //the datacell is allocated, but contains no data
      return false;
    }
  }
  catch (Exception e) {  //the location we try to access is not allocated,
    //i.e., no data in the cell
    return false;
  }
}


  /**write out the data object to valid XML stream
   */

  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String strIndent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {

    boolean writeHrefAttribute = false;
    boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
    String indent = "";
    String nodeName = getClassXDFNodeName();

    // indent up one 
    indent = indent + strIndent;

    //open node
    if (niceOutput)
      writeOut(outputstream, indent);

    writeOut(outputstream, "<" + nodeName );

    Href hrefObj = getHref();

    //write out just the data
    XMLDataIOStyle readObj = parentArray.getXMLDataIOStyle();
    OutputStream dataOutputStream = outputstream;


    if (hrefObj !=null) {  //write out to another file,
      String fileName = hrefObj.getSysId();
      String hrefName = hrefObj.getName();

      if(hrefName == null) 
      {
        Log.errorln("Error: href object in dataCube lacks name. Data being written into metadata instead.\n");
      } 
      else if (fileName != null)
      {
         writeHrefAttribute = true;
         try {
            dataOutputStream = new FileOutputStream(hrefObj.getSysId());
         }
         catch (IOException e) {
            //oops, something. is wrong, writ out to the passed in OutputStream
            Log.warnln("Error: cannot open file:"+fileName+" for writing. Data being written into metadata.\n");
            writeHrefAttribute = false;
         }
      } 
      else 
      {
            Log.warnln("Error: href:"+hrefName+" lacks systemId, cannot write data to a separate file.");
            Log.warnln("Data are being written into metadata instead.\n");
            writeHrefAttribute = false;
      }
    }
    else {
      // no *href* attribute specified, write out to the passed in OutputStream
      // dataOutputStream = outputstream; // not needed now 
    }

    // write data node attributes
    if (writeHrefAttribute) {
      writeOut(outputstream, " "+HREF_XML_ATTRIBUTE_NAME+"=\"");
      writeOutAttribute(outputstream, hrefObj.getName());
      writeOut(outputstream, "\"");
    }

    String checksum = getChecksum();
    if (checksum != null) {  
      writeOut(outputstream, " "+CHECKSUM_XML_ATTRIBUTE_NAME+"=\"");
      writeOutAttribute(outputstream, checksum.toString());
      writeOut(outputstream, "\"");
    }


    String encoding = getEncoding();
    if (encoding!= null) {  
      writeOut(outputstream, " "+ENCODING_XML_ATTRIBUTE_NAME+"=\"");
      writeOutAttribute(outputstream, encoding.toString());
      writeOut(outputstream, "\"");
    }


    String compress = getCompression();
    if (compress != null) {  
      writeOut(outputstream, " "+COMPRESSION_TYPE_XML_ATTRIBUTE_NAME+"=\"");
      writeOutAttribute(outputstream, compress.toString());
      writeOut(outputstream, "\"");
    }

    if (writeHrefAttribute) 
       writeOut(outputstream, "/>");  //we just close the data node now
    else 
       writeOut(outputstream, ">");  //end of opening code

    Locator currentLocator = parentArray.createLocator();

    AxisInterface fastestAxis = (AxisInterface) parentArray.getAxisList().get(0);
    //stores the NoDataValues for the parentArray,
    //used in writing out when NoDataException is caught
    String[] NoDataValues;

    if (parentArray.hasFieldAxis()) {
      NoDataValues = new String[fastestAxis.getLength()];
      DataFormat[] dataFormatList = parentArray.getDataFormatList();
      for (int i = 0; i < NoDataValues.length; i++) {
        DataFormat d =  dataFormatList[i];
        if (d != null && d.getNoDataValue() != null) 
          NoDataValues[i]=d.getNoDataValue().toString();
      }
    }
    else {
          NoDataValues = new String[1];
          NoDataValues[0] = parentArray.getNoDataValue();
/*
     // what tis this?? If there is no fieldAxis, then no fields,
     // and hence, only ONE noDataValue.
      DataFormat d = parentArray.getDataFormat();
      for (int i = 0; i < NoDataValues.length; i++) {
        if (d!=null && d.getNoDataValue() != null) 
          NoDataValues[i] = d.getNoDataValue().toString();
      }
*/
    }

    if (readObj instanceof TaggedXMLDataIOStyle) {
      String[] tagOrder = ((TaggedXMLDataIOStyle)readObj).getAxisTags();
      int stop = tagOrder.length;
      String[] tags = new String[stop];

      for (int i = stop-1; i >= 0 ; i--) {
        tags[stop-i-1]  = tagOrder[i];
      }

      int[] axes = getMaxDataIndex();
      stop =axes.length;
      int[] axisLength = new int[stop];
      for (int i = 0; i < stop; i++) {
        axisLength[i] =axes[stop - 1 - i];
      }
      writeTaggedData(dataOutputStream,
                      currentLocator,
                      indent,
                      axisLength,
                      tags,
                      0,
                      fastestAxis,
                      NoDataValues);

    }  //done dealing with with TaggedXMLDataIOSytle
    else {
       if (readObj instanceof DelimitedXMLDataIOStyle) {
           writeDelimitedData( dataOutputStream, currentLocator,
                               (DelimitedXMLDataIOStyle) readObj,
                               fastestAxis, NoDataValues,
                               writeHrefAttribute ? false : true
                              );

       }
       else {
        writeFormattedData(  dataOutputStream,
                             currentLocator,
                             (FormattedXMLDataIOStyle) readObj,
                             fastestAxis,
                             NoDataValues,
                             writeHrefAttribute ? false : true
                           );
       }
    }

    //close the data section appropriately
    if (!writeHrefAttribute && niceOutput) {
      writeOut(outputstream, Constants.NEW_LINE);
      writeOut(outputstream, indent);
    }

    // If we didnt write Href attribute, then means that data
    // were put into document. We need to close the open data
    // node appropriately.
    if (!writeHrefAttribute) 
      writeOut(outputstream, "</" + nodeName + ">");

    if (niceOutput)
      writeOut(outputstream, Constants.NEW_LINE);

  }


  /**writeTaggedData: write out tagged data
   *
   */
  private void writeTaggedData(OutputStream outputstream,
			       Locator locator,
			       String indent,
			       int[] axisLength,
			       String[] tags,
			       int which,
                               AxisInterface fastestAxis,
                               String[] noDataValues)
  {

    int nrofNoDataValues = noDataValues.length;

    String tag = (String) tags[which];
    if (Specification.getInstance().isPrettyXDFOutput()) {
      indent += Specification.getInstance().getPrettyXDFOutputIndentation();
    }

    //base case (writes the last 2 inner dimensions of the data cube)
    if (which == tags.length-2) {
      int stop = axisLength[which];
      String tag1 = (String) tags[which+1];
      for (int count = 0; count < stop; count++) {
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "<" + tag + ">");
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent + Specification.getInstance().getPrettyXDFOutputIndentation());
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
             // sloppy algorithm as a result of clean up after Kelly 
             String noDataString;
             if (nrofNoDataValues > 1)
                noDataString = noDataValues[locator.getAxisIndex(fastestAxis)];
             else
                noDataString = noDataValues[0];

             if (noDataString != null)
             {
                writeOut(outputstream, ">" + noDataString );
                writeOut( outputstream, "</" + tag1 + ">");
             } else
                writeOut( outputstream, "/>");
	  }

	  dataNum ++;
	  locator.next();
	}
	if (Specification.getInstance().isPrettyXDFOutput()) {
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
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "<" + tag + ">");
	writeTaggedData(outputstream, locator, indent, axisLength, tags, which, fastestAxis, noDataValues);
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  writeOut(outputstream, Constants.NEW_LINE);
	  writeOut(outputstream, indent);
	}
	writeOut(outputstream, "</" + tag + ">");
      }
    }
  }

  /** write delimited data
   *
   */
  private void writeDelimitedData(  OutputStream outputstream,
                                    Locator locator,
                                    DelimitedXMLDataIOStyle readObj,
                                    AxisInterface fastestAxis, 
                                    String[] noDataValues,
                                    boolean writeCDATAStatement
                                  ) 
  {

    int nrofNoDataValues = noDataValues.length;

    String delimiter = readObj.getDelimiter();
    String recordTerminator = readObj.getRecordTerminator();
    int fastestAxisLength = fastestAxis.getLength();
    int dataNum = 0;

    if(writeCDATAStatement) 
       writeOut(outputstream, "<![CDATA[");

    do {
      dataNum ++;
      try {
        
         writeOut(outputstream, getStringData(locator));

      } catch (NoDataException e) {  //double check, a bug here, "yes" is already printed

         // sloppy algorithm as a result of clean up after Kelly 
         String noData;
         if (nrofNoDataValues > 1)
             noData = noDataValues[locator.getAxisIndex(fastestAxis)];
         else
             noData = noDataValues[0];

         if (noData == null) {
            if(readObj.getRepeatable().equals("yes"))
            {
               // should throw an error
               Log.errorln("Error: you have not set noDataValue and have a repeatable delimiter. Can't write data. Aborting.");
               System.exit(-1);
            }
         } else {
           writeOut(outputstream, noData);
         }
      }

      //write out delimiter anyway
      writeOut(outputstream, delimiter);
      if (dataNum == fastestAxisLength) {
        writeOut(outputstream, recordTerminator);
        dataNum = 0;
      }
    }
    while (locator.next());

    if (writeCDATAStatement) 
       writeOut(outputstream, "]]>");
  }

  /** Write formatted data
   */
   private void writeFormattedData(OutputStream outputstream ,
                                   Locator locator,
                                   FormattedXMLDataIOStyle readObj,
                                   AxisInterface fastestAxis,
                                   String[] noDataValues,
                                   boolean writeCDATAStatement 
                                  )
   {

      int nrofNoDataValues = noDataValues.length;

      // print opening CDATA statement
      if (writeCDATAStatement) 
         writeOut(outputstream, "<![CDATA[");

      // print out the data as appropriate for the format
      // QUESTION: don't we need to syncronize on readObj too?
      synchronized (data) 
      { 

        List commands = readObj.getCommands(); // returns expanded list (no repeat cmds) 
        String endian = readObj.getEndian();
        int nrofCommands = commands.size();
        int currentCommand = 0;

        // init important dataFormat information into arrays, this 
        // will help speed up long writes.
        DataFormat dataFormat[] = parentArray.getDataFormatList(); 
        int nrofDataFormats = dataFormat.length;
        int currentDataFormat = 0;
        String[] pattern = new String[nrofDataFormats];
        String[] intFlag = new String[nrofDataFormats];
        int[] numOfBytes = new int[nrofDataFormats];
        for (int i=0; i< nrofDataFormats; i++) { 
           pattern[i] = dataFormat[i].getFormatPattern();
           numOfBytes[i] = dataFormat[i].numOfBytes();
           if (dataFormat[i] instanceof IntegerDataFormat) 
              intFlag[i] = ((IntegerDataFormat) dataFormat[i]).getType();
           else 
              intFlag[i] = null;
        }

        // loop thru all of the dataCube until finished with all data and commands 
        boolean atEndOfDataCube = false;
        boolean backToStartOfDataCube = false;
        while (!backToStartOfDataCube)
        { 

             FormattedIOCmd command = (FormattedIOCmd) commands.get(currentCommand);

             if(atEndOfDataCube && locator.getAxisIndex(fastestAxis) == 0) 
                 backToStartOfDataCube = true;

             if (command instanceof ReadCellFormattedIOCmd)
             {

                if (backToStartOfDataCube) break; // dont bother, we'd be re-printing data 

                try {
                   doReadCellFormattedIOCmdOutput( outputstream,
                                                   dataFormat[currentDataFormat],
                                                   numOfBytes[currentDataFormat],
                                                   pattern[currentDataFormat],
                                                   endian,
                                                   intFlag[currentDataFormat],
                                                   locator );
                } catch (NoDataException e) {

                    // no data here, hurm. Print the noDataValue. 
                    // sloppy algorithm as a result of clean up after Kelly 
                    String noData;

                    if (nrofNoDataValues > 1) 
                        noData = noDataValues[locator.getAxisIndex(fastestAxis)];
                    else 
                        noData = noDataValues[0];

                    if (noData != null) { 
                        writeOut(outputstream, noData);
                    } else { 
                        Log.errorln("Can't print out null data: noDataValue NOT defined.");
                    }

                }

                // advance the data location 
                locator.next();

                // advance the DataFormat to be used  
                if(nrofDataFormats > 1)
                {
                   currentDataFormat++;
                   if ( currentDataFormat == nrofDataFormats)
                      currentDataFormat = 0;
                }

             }
             else if (command instanceof SkipCharFormattedIOCmd)
             {

                doSkipCharFormattedIOCmdOutput ( outputstream, (SkipCharFormattedIOCmd) command);

             }
             else
             {
                Log.errorln("DataCube cannot write out, unimplemented format command:"+
                              command.toString());
                System.exit(-1);
             }

             if(nrofCommands > 1)
             { 
                currentCommand++;
                if ( currentCommand == nrofCommands) { 
                   currentCommand = 0;
                }
             }

             if(!atEndOfDataCube && !locator.hasNext()) atEndOfDataCube = true;


         } // end of while loop 

      } // end of sync loop

      // print closing CDATA statement
      if (writeCDATAStatement) 
         writeOut(outputstream, "]]>");

   }

   private void doSkipCharFormattedIOCmdOutput ( OutputStream outputstream, 
                                                 SkipCharFormattedIOCmd skipCharCommand)
   {
       writeOut(outputstream, skipCharCommand.getOutput());
   }


   // Prints out data as appropriate when we get a ReadCellFormattedIOCmd
   // We make use of the Java Message|Decimal Formats here, seems wasteful
   // because we have to convert datatypes around to accomodate these classes.
   // should look into buffered type of IO ala JavaFits to find better 
   // performance. -b.t. 
   private void doReadCellFormattedIOCmdOutput ( OutputStream outputstream,
                                                 DataFormat thisDataFormat, 
                                                 int formatsize,
                                                 String pattern,
                                                 String endian,
                                                 String intFlagType,
                                                 Locator locator
                                               ) 
   throws NoDataException
   {

         String output = null;

         // format the number for output
         if (thisDataFormat instanceof IntegerDataFormat )
         {


            DecimalFormat formatter = new DecimalFormat(pattern);
            Integer thisDatum = new Integer (getIntData(locator));

            if (intFlagType.equals(Constants.INTEGER_TYPE_DECIMAL)) {

               output = formatter.format(thisDatum);

            } else if (intFlagType.equals(Constants.INTEGER_TYPE_OCTAL)) {

               String intStrVal = Integer.toOctalString(thisDatum.intValue());
               //output = formatter.format(Integer.toOctalString(thisDatum.intValue()));
               int size = intStrVal.length();
               while (size++ < formatsize) 
                  intStrVal = new String ("0" + intStrVal);
               output = intStrVal;

            } else if (intFlagType.equals(Constants.INTEGER_TYPE_HEX)) {

               String intStrVal = Integer.toHexString(thisDatum.intValue());
               int size = intStrVal.length();
               while (size++ < (formatsize-2)) 
                  intStrVal = new String ("0" + intStrVal); 

               // tack on leading stuff
               output = "0x" + intStrVal;

            } 


         } else if (thisDataFormat instanceof StringDataFormat)
         {

            output = getStringData(locator);
            // hmm. check to see we dont exceed the allowed width of this field
            // trim down the string IF that is the case.
            if(output.length() > formatsize)
               output = output.substring(0,formatsize);

         } 
         else if ( thisDataFormat instanceof FloatDataFormat)
         {

            // Exponentials need special treatment. Why? because as of Java
            // 1.2.2 and 1.3, the DecimalFormatter will not enforce a maximum
            // exponent size on exponential numbers. This means that the output
            // can violate the declared fix width of the field if the expontent
            // on a number is negative (for example).
            DecimalFormat formatter = new DecimalFormat(pattern);
            Double thisDatum = new Double(getDoubleData(locator));
            output = formatter.format(thisDatum);

            // Our quick 'fix': trim down the size of the output if its exceeded.
            if (output.length() > formatsize) { 
               Log.warn("Warning: formatted floating point number width exceeds spec, trimming ["+output+"] to ");
               output = output.substring(0,formatsize);
               Log.warnln("["+output+"]");
            }

         } 
         else if ( thisDataFormat instanceof BinaryFloatDataFormat)
         {
         
            int numOfBytes = thisDataFormat.numOfBytes();
            byte[] byteBuf = new byte[numOfBytes];

            if (numOfBytes == 8) 
            {

               long lbits = Double.doubleToLongBits(getDoubleData(locator));

               byteBuf[0] = (byte) (lbits >>> 56);
               byteBuf[1] = (byte) (lbits >>> 48);
               byteBuf[2] = (byte) (lbits >>> 40);
               byteBuf[3] = (byte) (lbits >>> 32);
               byteBuf[4] = (byte) (lbits >>> 24);
               byteBuf[5] = (byte) (lbits >>> 16);
               byteBuf[6] = (byte) (lbits >>>  8);
               byteBuf[7] = (byte)  lbits;

            } 
            else if (numOfBytes == 4) 
            {

               // Q: does this involve rounding??
               float datum = (float) getDoubleData(locator);
               int ibits = Float.floatToIntBits(datum);

               byteBuf[0] = (byte) (ibits >>> 24);
               byteBuf[1] = (byte) (ibits >>> 16);
               byteBuf[2] = (byte) (ibits >>>  8);
               byteBuf[3] = (byte)  ibits;

            } 
            else 
            {
               Log.errorln("Got weird number of bytes for BinaryFloatDataFormat:"+numOfBytes+" exiting.");
               System.exit(-1);
            }

            // check for endianess
            if (endian.equals(Constants.LITTLE_ENDIAN)) 
            {
               // reverse the byte order
               byteBuf = reverseBytes ( byteBuf );
            }

            output = new String(byteBuf);
            
         } 
         else if ( thisDataFormat instanceof BinaryIntegerDataFormat)
         {

            int numOfBytes = thisDataFormat.numOfBytes();
            byte[] byteBuf = new byte[numOfBytes];
            int i = getIntData(locator);

            // short
            if (numOfBytes == 2) {

               byteBuf[0] = (byte) (i >>>  8);
               byteBuf[1] = (byte)  i;

            } else if (numOfBytes == 4) {

               byteBuf[0] = (byte) (i >>> 24);
               byteBuf[1] = (byte) (i >>> 16);
               byteBuf[2] = (byte) (i >>>  8);
               byteBuf[3] = (byte)  i;

            } else if (numOfBytes == 8) {

               byteBuf[0] = (byte) (i >>> 56);
               byteBuf[1] = (byte) (i >>> 48);
               byteBuf[2] = (byte) (i >>> 40);
               byteBuf[3] = (byte) (i >>> 32);
               byteBuf[4] = (byte) (i >>> 24);
               byteBuf[5] = (byte) (i >>> 16);
               byteBuf[6] = (byte) (i >>>  8);
               byteBuf[7] = (byte)  i;

            } else {
               Log.errorln("Got weird number of bytes for BinaryIntegerDataFormat:"+numOfBytes+" exiting.");
               System.exit(-1);
            }

            if (endian.equals(Constants.LITTLE_ENDIAN)) 
            {
               // reverse the byte order
               byteBuf = reverseBytes ( byteBuf );
            }

            output = new String(byteBuf);

         } 
         else 
         {
            // a failure to communicate :)
            Log.errorln("Unknown Dataformat:"+thisDataFormat.getClass().toString()
                        +" is not implemented for formatted writes. Aborting.");
            System.exit(-1);
         }

         // if we have some output, write it
         if (output != null) {

            // pad with leading spaces
            int actualsize = output.length();
            while (actualsize < formatsize)
            {
               writeOut(outputstream, " ");
               actualsize++;
            }

            // now write the data out
            writeOut(outputstream, output);

         } else {
            // throw error
            Log.printStackTrace(new IOException());
         }

   } 

   //
   // PROTECTED methods
   //

  protected void setParentArray(Array parentArray) {
     this.parentArray = parentArray;
  }


  /**deep copy of this Data object
   */
  protected Object clone() throws CloneNotSupportedException {
    DataCube cloneObj = (DataCube) super.clone();
    synchronized (this) {
      synchronized (cloneObj) {
        cloneObj.data= deepCopy(this.data, dimension);
      }
    }
    return cloneObj;
  }

  //
  // PRIVATE methods
  //

   /** init -- special private method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   private void init()
   {

    classXDFNodeName = "data";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, COMPRESSION_TYPE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, ENCODING_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, CHECKSUM_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, HREF_XML_ATTRIBUTE_NAME);

    //set up the attribute hashtable key with the default initial value
    attribHash.put(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(ENCODING_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(CHECKSUM_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put(HREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.OBJECT_TYPE));

  };

  /* reverse the byte order */
  private byte[] reverseBytes ( byte[] bytes ) {

     int bufsize = (bytes.length - 1);
     byte[] rbytes = new byte[bufsize+1];

     for (int i=0; i <= bufsize; i++) 
        rbytes[bufsize-i] = bytes[i];

     return rbytes;
  }

  /**deepCopy: deep copy data
   * @param data - the data that needs to be copied
   *         currentLayer--which dimension we are copying
   * @return an exact copy of passed in data
   */
  private List deepCopy(List data, int currentLayer) {
    List tempData = new ArrayList();
    if (data == null)
      return null;
    int stop = data.size();
    //we have reached the inner most 2 layers
    if ( currentLayer == 2) {
      for (int i = 0; i < stop; i++) {
        Object obj = data.get(i);
        if (obj == null)
          tempData.add(null);
        else {
          int length = java.lang.reflect.Array.getLength(obj);
          Object tempArray = java.lang.reflect.Array.newInstance(obj.getClass().getComponentType(), length);
          Class arrayComponentType = obj.getClass().getComponentType();
          for (int index = 0; index<length; index++) {
            if (arrayComponentType.equals(Byte.TYPE)){
              java.lang.reflect.Array.setByte(tempArray, index, java.lang.reflect.Array.getByte(obj, index));
            }
            else {
              if (arrayComponentType.equals(Integer.TYPE)){
                java.lang.reflect.Array.setInt(tempArray, index, java.lang.reflect.Array.getInt(obj, index));
              }
              else {
                if (arrayComponentType.equals(Double.TYPE)){
                  java.lang.reflect.Array.setDouble(tempArray, index, java.lang.reflect.Array.getDouble(obj, index));
                }
                else
                  java.lang.reflect.Array.set(tempArray, index, java.lang.reflect.Array.get(obj, index));
              }
            }

          }  //  end of inner for loop
          tempData.add(tempArray);
        } //end of outer for loop
      }

    }
    else {
      for (int i = 0; i< stop; i++) {
        tempData.add(deepCopy((List) data.get(i), currentLayer-1));
      }
    }
    return tempData;
  }

}
 /**
  * Modification History:
  * $Log$
  * Revision 1.23  2001/02/07 18:40:15  thomas
  * Added new setData methods. Converted XML attribute decl
  * to use constants (final static fields within the object). These
  * are private decl for now. -b.t.
  *
  * Revision 1.22  2001/01/29 19:29:34  thomas
  * Changes related to combining ExponentialDataFormat
  * and FloatDataFormat classes. -b.t.
  *
  * Revision 1.21  2001/01/29 05:03:37  thomas
  * Half-hearted code for binary writing. Disabled
  * for the time being. -b.t.
  *
  * Revision 1.20  2001/01/19 22:33:12  thomas
  * Some small changes to the output when Href is
  * used. -b.t.
  *
  * Revision 1.19  2001/01/19 17:23:20  thomas
  * Fixed Href stuff to DTD standard. Now using
  * notation and entities at the beginning of the
  * file. -b.t.
  *
  * Revision 1.18  2000/11/27 22:39:25  thomas
  * Fix to allow attribute text to have newline, carriage
  * returns in them (print out as entities: &#010; and
  * &#013;) This allows files printed out to be read back
  * in again(yeah!). -b.t.
  *
  * Revision 1.17  2000/11/22 20:42:00  thomas
  * beaucoup changes to make formatted reads work.
  * DataFormat methods now store the "template" or
  * formatPattern that will be needed to print them
  * back out. Removed sprintfNotation, Perl regex and
  * Perl attributes from DataFormat classes. -b.t.
  *
  * Revision 1.16  2000/11/20 22:06:08  thomas
  * plit up XMLAttribute type NUMBER_TYPE into
  * INTEGER_TYPE and DOUBLE_TYPE. This allows for
  * some needed handling in the SaxDocHandler when
  * parsing data for the formatted read. Put prior NUMBER_TYPE
  * attributes into appropriate new category. -b.t.
  *
  * Revision 1.15  2000/11/16 19:51:25  kelly
  * fixed documentation.  -k.z.
  *
  * Revision 1.14  2000/11/10 15:35:08  kelly
  * minor fix related to cvs check in.
  *
  * Revision 1.13  2000/11/10 04:32:44  thomas
  * Updated to fix compile problems. -b.t.
  *
  * Revision 1.12  2000/11/09 23:22:59  kelly
  * handles formatted read now.  -k.z.
  *
  * Revision 1.11  2000/11/09 04:52:00  thomas
  * In toXML* method, CDATA miss-spelled CDDATA (!)
  * Fixed. -b.t.
  *
  * Revision 1.10  2000/11/08 22:30:12  thomas
  * Changed set methods to return void. -b.t.
  *
  * Revision 1.9  2000/11/08 19:18:07  thomas
  * Changed the name of toXDF* methods to toXML* to
  * better reflect the nature of the output (its not XDF
  * unless you call th emethod from strcuture object;
  * otherwise, it wont validate as XDF; it is still XML
  * however). -b.t.
  *
  * Revision 1.8  2000/11/06 21:11:01  kelly
  * --added removeData method
  * --added deep cloning
  *
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






