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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;

import java.lang.reflect.*;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
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

   // should be hex for faster comparison?
   private int DOUBLE_DATA_TYPE = 0;
   private int INT_DATA_TYPE = 1;
   private int SHORT_DATA_TYPE = 2;
   private int LONG_DATA_TYPE = 3;
   private int STRING_DATA_TYPE = 4;
   private int BYTE_DATA_TYPE = 5;

   //  to store the n-dimensional data, it is an ArrayList of ArrayList, whose
   //  innermost layer contains two kinds of arrays:
   //  --an array of bytes to indicate if its corresponding cell contains valid data
   //  (Java fills all int[] and double[] with 0 once constructed )
   //  --an array of primative types or an array of Strings depending depending on
   //   the actual data
   private List longDataArray = Collections.synchronizedList(new ArrayList());

   private double expandFactor = 1.3; // when expanding internal data storage arrays
                                      // we increase capacity by 30% over needed new cap.
                                      // to prevent constantly having to call for more expansion.
   //
   // Constructor and related methods
   //

  /** The constructor that takes parentArray as param.
   */
  public DataCube(Array parentArray) {
     this.parentArray = parentArray;
     init();
  }

  /*  This constructor takes a Java Hashtable as an initializer of
    the XML attributes of the object to be constructed. The
    Hashtable key/value pairs coorespond to the class XDF attribute
    names and their desired values.
    */
  /*not needed, DataCube is always associated with an Array class, double check
   */
/*
    public DataCube ( Hashtable InitXDFAttributeTable )
    {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

    } */


  /** set the *href* attribute
   */
  public void setHref (Entity hrefObj)
  {
     ((Attribute) attribHash.get(HREF_XML_ATTRIBUTE_NAME)).setAttribValue(hrefObj);
  }

  /**
   * @return the current *href* attribute
   */
  public Entity getHref()
  {
     return (Entity) ((Attribute) attribHash.get(HREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *checksum* attribute
   */
  public void setChecksum (String checksum) {
     ((Attribute) attribHash.get(CHECKSUM_XML_ATTRIBUTE_NAME)).setAttribValue(checksum);
  }

  /**
   * @return the current *checksum* attribute
   */
  public String getChecksum () {
     return (String) ((Attribute) attribHash.get(CHECKSUM_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** Set the *encoding* attribute. Note that this attribute is different in nature
       from the encoding attribute on the XMLDataIOStyle objects. Currently reading of
       data encoded data is not supported. 
   */
   public void setEncoding (String strEncoding)
   {

      if (!Utility.isValidDataEncoding(strEncoding))
         Log.warnln("Encoding is not valid, ignoring request to setEncoding.");
      else
         ((Attribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).setAttribValue(strEncoding);

   }

  /*
   * @return the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((Attribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


  /** set the *compression* attribute
   */
  public void setCompression (String strCompression)
  {

    if (!Utility.isValidDataCompression(strCompression))
       Log.warnln("Data compression value is not valid, ignoring request to set it.");
    else
      ((Attribute) attribHash.get(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME)).setAttribValue(strCompression);

  }

  /**
   * @return the current *compression* attribute
   */
  public String getCompression()
  {
    return (String) ((Attribute) attribHash.get(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**
   * @return the current dimension
   */
/*
  public int getDimension() {
     return dimension;
  }
*/

  /** get the max index along with dimension
   * @return int[]
   */
  private int[] getMaxDataIndex(XMLDataIOStyle readObj) {

     List axes = readObj.getIOAxesOrder(); // parentArray.getAxes();
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
  // other PUBLIC methods
  //


   /** reset the datacube
    */
   //not right, have to write again, double check implications of dataCube
   public void reset() { 

      Log.debugln("in DataCube, called reset()");

      // reset the longDataArray, will free all related shortDataArrays.
      // What else needs to be done?
      longDataArray = Collections.synchronizedList(new ArrayList());

   }
   
   
   /** We return whatever object is stored in the datacell.
    */
   public Object getData (Locator locator) throws NoDataException
   {
   
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);
   
      try {
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue
   
         return java.lang.reflect.Array.get(longDataArray.get(longIndex+1), shortIndex);
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
   
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);
   
      try {
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue
   
         return java.lang.reflect.Array.getInt(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
          //i.e., no data in the cell
          throw new NoDataException();
      }
   
   }
   
   /** Get short (16-bit) integer data from a requested datacell. 
    */
   public short getShortData (Locator locator)
   throws NoDataException
   {
   
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);
   
      try {
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue

         return java.lang.reflect.Array.getShort(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
        //i.e., no data in the cell
         throw new NoDataException();
      }
   
   }

   /** Get byte (1-bit) integer data from a requested datacell. 
    */
   public byte getByteData (Locator locator)
   throws NoDataException
   {

      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      try { 
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue

         return java.lang.reflect.Array.getByte(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
        //i.e., no data in the cell
         throw new NoDataException();
      }

   }

   
   /** Get long (64-bit) integer data from a requested datacell. 
    */
   public long getLongData (Locator locator)
   throws NoDataException
   {
   
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);
   
      try {
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue
   
         return java.lang.reflect.Array.getLong(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
         //i.e., no data in the cell
         throw new NoDataException();
      }
   
   }


   /**Get the double value of the requested datacell.
    */
   public double getDoubleData (Locator locator)
   throws NoDataException
   {

      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      try {
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue
    
         return java.lang.reflect.Array.getDouble(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
         //i.e., no data in the cell
         throw new NoDataException();
      }

   }

   /**Get the double value of the requested datacell.
    */
   public float getFloatData (Locator locator)
   throws NoDataException
   {

      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      try { 
         if (java.lang.reflect.Array.getByte(longDataArray.get(longIndex), shortIndex) !=1)
            throw new NoDataException();  //the location we try to access contains noDataValue

         return java.lang.reflect.Array.getFloat(longDataArray.get(longIndex+1), shortIndex);
      }
      catch (Exception e) {  //the location we try to access is not allocated,
         //i.e., no data in the cell
         throw new NoDataException();
      }

   }

  /**  Append the String value onto the requested datacell. 
       Care should be taken when using this method to insure that
       String data is not appended into a non-String datacell (currently
       possible, but it will cause problems later on).
   */
// We need a double check here: how to prevent the user 
// from appending to an int or double?
   public void appendData (Locator locator, String stringValue) 
   throws SetDataException 
   {

      String strData;
      try {
         strData = getStringData(locator);
         strData += stringValue;
      }
      catch (NoDataException e) {
         strData = stringValue;
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
   public void setData (Locator locator, Float value)
   throws SetDataException
   {
      setData(locator, value.floatValue());
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
   public void setData (Locator locator, Long value)
   throws SetDataException
   {
      setData(locator, value.longValue());
   }

  /** Set the value of the requested datacell. 
   *  Overwrites existing datacell value if already populated with a value.
   */
   public void setData (Locator locator, Short value)
   throws SetDataException
   {
      setData(locator, value.shortValue());
   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData (Locator locator, double numValue)
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, DOUBLE_DATA_TYPE);
   
      // Set the Data
      try { 
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setDouble(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set double data:["+numValue+"] ("+longIndex+","+shortIndex+") Array out of bounds, using the wrong Locator?");
      }

   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData (Locator locator, float numValue)
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, DOUBLE_DATA_TYPE);

      // Set the Data
      try {
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setFloat(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set float data:["+numValue+"] Array out of bounds, using the wrong Locator?");
      }

   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData(Locator locator, int numValue) 
   throws SetDataException 
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, INT_DATA_TYPE);

      // Set the Data
      try { 
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setInt(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set int data:["+numValue+"] Array out of bounds, using the wrong Locator?");
      }

   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData(Locator locator, long numValue)
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, LONG_DATA_TYPE);

      // Set the Data
      try { 
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setLong(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set long data:["+numValue+"] Array out of bounds, using the wrong Locator?");
      }

   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData(Locator locator, short numValue)
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, SHORT_DATA_TYPE);

      // Set the Data
      try { 
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setShort(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set short data:["+numValue+"] Array out of bounds, using the wrong Locator?");   
      }

   }

   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData(Locator locator, byte numValue)
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, BYTE_DATA_TYPE);

      // Set the Data
      try {
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex+1), shortIndex, numValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set byte data:["+numValue+"] Array out of bounds, using the wrong Locator?");   
      }

   }


   /** Set the value of the requested datacell. 
    *  Overwrites existing datacell value if already populated with a value.
    */
   public void setData (Locator locator, String stringValue) 
   throws SetDataException
   {

      // data are stored in a huge 2D array. The long array axis
      // mirrors all dimensions but the 2nd axis. The 2nd axis gives
      // the index on the 'short' internal array.
      int longIndex = parentArray.getLongArrayIndex(locator);
      int shortIndex = parentArray.getShortArrayIndex(locator);

      // Bounds checking
      checkDataArrayBounds(longIndex, shortIndex, STRING_DATA_TYPE);

      // Set the Data
      try {
         byte realValue = 1;
         //indicate its corresponding datacell holds valid data
         java.lang.reflect.Array.setByte(longDataArray.get(longIndex), shortIndex, realValue);

         //put data into the requested datacell
         java.lang.reflect.Array.set(longDataArray.get(longIndex+1), shortIndex, stringValue);
         return;
      }
      catch (ArrayIndexOutOfBoundsException e) {
         throw new SetDataException("Cant set String data:["+stringValue+"] Array out of bounds, using the wrong Locator?");   
      }

   }

   //
   // PROTECTED methods
   //


   protected String basicXMLWriter (
                                Writer outputWriter,
                                String strIndent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )

   throws java.io.IOException
   {


      // init some internals
      boolean writeHrefAttribute = false;
      OutputStream dataOutputStream = null;
      Writer dataOutputWriter = null;
      boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
      String indent = "";

      // get the node name
      String nodeName = getClassXDFNodeName();
      if (newNodeNameString != null) nodeName = newNodeNameString;
  
      // indent up one 
      indent = indent + strIndent;
  
      //open node
      if (niceOutput)
        outputWriter.write( indent);
  
      outputWriter.write("<" + nodeName );
  
      Entity hrefObj = getHref();
  
      XMLDataIOStyle readObj = parentArray.getXMLDataIOStyle();
  
      // Well here decide if we need to write out to another file,
      // In practice the systemId may correspond to an internet URI,
      // and we cant always write to that (!). Its not clear under which
      // situations we do want to do so. :P For the time being we just
      // take the following 'dumb' approach.
      if (hrefObj != null) {  
        String fileName = hrefObj.getSystemId();
        String hrefName = hrefObj.getName();
  
        // Some parsers return systemId with the 'file:' prefix. Java
        // doenst currently understand this so we need to peal it off.
        int index = fileName.indexOf("file:");
        if (index == 0)
           fileName = fileName.substring(5);

        if(hrefName == null) 
        {
          Log.errorln("Error: href object in dataCube lacks name. Data being written into metadata instead.\n");
        } 
        else if (fileName != null)
        {
           writeHrefAttribute = true;

           try {

              dataOutputStream = new FileOutputStream(fileName);

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
  
      // write data node attributes
      if (writeHrefAttribute) {
         outputWriter.write( " "+HREF_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputWriter, hrefObj.getName());
         outputWriter.write( "\"");
      }
  
      String checksum = getChecksum();
      if (checksum != null) {  
         outputWriter.write( " "+CHECKSUM_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputWriter, checksum.toString());
         outputWriter.write( "\"");
      }
  
/*
      String encoding = getEncoding();
      if (encoding != null) {  
         outputWriter.write( " "+ENCODING_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputWriter, encoding.toString());
         outputWriter.write( "\"");
      }
*/
  
  
      String compress = getCompression();
      if (compress != null && dataOutputStream != null ) {  
          outputWriter.write( " "+COMPRESSION_TYPE_XML_ATTRIBUTE_NAME+"=\"");
          writeOutAttribute(outputWriter, compress.toString());
          outputWriter.write( "\"");
 
          if (hrefObj == null) {
             Log.errorln("Cant write compressed data within the XML file (use href instead for an external file). Aborting write.");
             return nodeName;
          }

          // change the data outputWriter to match
          if (compress.equals(Constants.DATA_COMPRESSION_GZIP)) {
             try {
                dataOutputStream = new GZIPOutputStream(dataOutputStream);

             } catch (java.io.IOException e) {
                Log.errorln("Cant open compressed (GZIP) outputstream to write to an href. Aborting.");
                return nodeName;
             }
          } else if (compress.equals(Constants.DATA_COMPRESSION_ZIP)) {
             dataOutputStream = new ZipOutputStream(dataOutputStream);
             try {
                ((ZipOutputStream) dataOutputStream).putNextEntry(new ZipEntry(hrefObj.getSystemId())); // write only to the first entry for now 
             } catch (java.io.IOException e) {
                Log.errorln("Cant open compressed (ZIP) outputstream to write to an href. Aborting.");
                return nodeName;
             }
          } else {
             Log.errorln("Error: cant write data with compression type:"+compress+". Ignoring request.");
             return nodeName;
          }
      }
  
      if (writeHrefAttribute) 
          outputWriter.write("/>");  //we just close the data node now
      else 
          outputWriter.write(">");  //end of opening code
  
      // Locator currentLocator = parentArray.createLocator();
      // List axisList = parentArray.getAxes();
      List axisList = readObj.getIOAxesOrder();

      if (axisList == null || axisList.size() == 0) {

          // we dont have axes to direct the write?!?. Well, then, we 
          // wont be writing Data to either the XML file or an Href
         Log.errorln("No axes defined! Cannot write data. Ignoring request.");

      } else {
 
         // writing Data to either the XML file or an Href
         //

         // Init important dataFormat information into arrays, this 
         // will help speed up long writes. Each entry corresponds to 
         // cached information about a particular dataFormat object. In
         // the case where no FieldAxis (e.g. Fields) exist within the array,
         // then we only have 1 entry in each of these arrays. 

         String[] noDataValues; //stores the NoDataValues for the parentArray,
                                //used in writing out when NoDataException is caught

         String[] formatPattern;      // cache of Java Formatter patterns for correct 
                                // formatting of output values.

         String[] negExponentialPattern; // Stupid Java DecimalFormatter cant work properly
                                         // on exponential data, so we compensate with this.

         String[] intFlag;      // Integer format flag 


         int[] numOfBytes;      // number of bytes each data format has. 

         DataFormat dataFormat[] = parentArray.getDataFormatList();

         if (parentArray.hasFieldAxis()) {

            // If we have field axis then prepare to init multiple size arrays

            List fields = parentArray.getFieldAxis().getFields();
            int nrofDataFormats = fields.size();

            noDataValues  = new String[nrofDataFormats];
            formatPattern = new String[nrofDataFormats];
            intFlag       = new String[nrofDataFormats];
            negExponentialPattern = new String[nrofDataFormats];
            numOfBytes = new int[nrofDataFormats];

            // now assign values 
            for (int i = 0; i < nrofDataFormats; i++) {

               formatPattern[i] = dataFormat[i].getFormatPattern();
               numOfBytes[i]    = dataFormat[i].numOfBytes();

               Field field = (Field) fields.get(i);
               if (field != null && field.getNoDataValue() != null) 
                   noDataValues[i] = field.getNoDataValue().toString();

               if (dataFormat[i] instanceof FloatDataFormat)
                  negExponentialPattern[i] = ((FloatDataFormat) dataFormat[i]).getNegativeExponentFormatPattern();
               else
                  negExponentialPattern[i] = null;

               if (dataFormat[i] instanceof IntegerDataFormat)
                  intFlag[i] = ((IntegerDataFormat) dataFormat[i]).getType();
               else
                  intFlag[i] = null;
            }


         } else {

            // no field axis? then only one dataFormat and we get it from the Array
            // init single size arrays
            noDataValues  = new String[1];
            formatPattern = new String[1];
            negExponentialPattern = new String[1];
            intFlag       = new String[1];
            numOfBytes = new int[1];

            // assign values 
            formatPattern[0] = dataFormat[0].getFormatPattern();
            numOfBytes[0]    = dataFormat[0].numOfBytes();

            String value = (String) null;
            if (parentArray.getNoDataValue() != null) {
               value = parentArray.getNoDataValue().toString(); // this is a HACK 
            }
            noDataValues[0] = value;

            if (dataFormat[0] instanceof FloatDataFormat)
               negExponentialPattern[0] = ((FloatDataFormat) dataFormat[0]).getNegativeExponentFormatPattern();
            else
               negExponentialPattern[0] = null;

            if (dataFormat[0] instanceof IntegerDataFormat)
               intFlag[0] = ((IntegerDataFormat) dataFormat[0]).getType();
            else
               intFlag[0] = null;

         }
     
         // init the dataOutputWriter properly. To a file or to the same writer as the
         // rest of the XML metadata?
         if (dataOutputStream != null) { 
            // if this exists, then we are re-directing to an outside file.
            // wrap the outputstream (compressed or otherwise) with bufferedWriter
            dataOutputWriter = new BufferedWriter(new OutputStreamWriter(dataOutputStream));
         } else {
            // goes to same spot as meta-data, e.g. just use the XML output writer  
            dataOutputWriter = outputWriter;
         }

         // some info about the format Object
         String endian = readObj.getEndian();

         // now, based on outputstyle, write out the data
         if (readObj instanceof TaggedXMLDataIOStyle) 
         {


            String[] tags = ((TaggedXMLDataIOStyle)readObj).getAxisTags();

            int[] axes = getMaxDataIndex(readObj);
            int stop = axes.length;
            int[] axisLength = new int[stop];
            for (int i = 0; i < stop; i++) {
               axisLength[i] = axes[stop - 1 - i];
            }
        
            int whichTagIsFieldAxis = -1;
            if (parentArray.hasFieldAxis()) {
               for (int i = 0; i < stop; i++) {
                  whichTagIsFieldAxis = i;
                  AxisInterface axisObj = ((TaggedXMLDataIOStyle) readObj).getAxisByTag("d" + i);
                  if (axisObj instanceof FieldAxis) {
                     break;
                  }
               }
            }

            Locator taggedLocator = parentArray.createLocator();
            AxisInterface fastestAxis = (AxisInterface) axisList.get(0);
            int nrofDataFormats = dataFormat.length;

            writeTaggedData( dataOutputWriter,
                             taggedLocator,
                             indent,
                             axisLength,
                             tags,
                             0,
                             0,
                             fastestAxis,
                             noDataValues,
                             nrofDataFormats,
                             dataFormat,
                             numOfBytes,
                             formatPattern,
                             negExponentialPattern,
                             endian,
                             intFlag, 
                             whichTagIsFieldAxis
                           );
   
            // this *shouldnt* be needed, but tests with both Java 1.2.2 and 1.3.0
            // on SUN and Linux platforms show that it is. Hopefully we can remove
            // this in the future.
            dataOutputWriter.flush();
   
     
         }  //done dealing with with TaggedXMLDataIOSytle
         else 
         {
   
            if (readObj instanceof DelimitedXMLDataIOStyle) {

               AxisInterface fastestAxis = (AxisInterface) axisList.get(0);
               writeDelimitedData( dataOutputWriter,
                                   (DelimitedXMLDataIOStyle) readObj,
                                   fastestAxis, noDataValues,
                                   dataFormat,
                                   numOfBytes,
                                   formatPattern,
                                   negExponentialPattern,
                                   endian,
                                   intFlag,
                                   writeHrefAttribute ? false : true
                                 );
     
            } else {
               writeFormattedData( dataOutputWriter,
                                   (FormattedXMLDataIOStyle) readObj,
                                   noDataValues,
                                   dataFormat,
                                   numOfBytes,
                                   formatPattern,
                                   negExponentialPattern,
                                   endian,
                                   intFlag, 
                                   writeHrefAttribute ? false : true
                                 );
            }
   
            if (writeHrefAttribute) {
               try {
                  // should work as flush() too, so no call needed here. 
                  dataOutputWriter.close();
               } catch (java.io.IOException e) {
                  Log.errorln("Cant close dataOuputStream! Aborting.");
                  return nodeName;
               }
            }
   
         }

      } // finish writing data to XML/Href 
  
      //close the data section appropriately
      if (!writeHrefAttribute && niceOutput) {
        outputWriter.write( Constants.NEW_LINE+indent);
      }
  
      // If we didnt write Href attribute, then means that data
      // were put into document. We need to close the open data
      // node appropriately.
      if (!writeHrefAttribute) 
        outputWriter.write( "</" + nodeName + ">");
  
 //     if (niceOutput)
 //       outputWriter.write( Constants.NEW_LINE);
      return nodeName;

   }

   protected void setParentArray(Array parentArray) {
      this.parentArray = parentArray;
   }

  /**removeData : Remove data from the indicated datacell
   * @param locator that indicates the location of the cell
   * @return true if indicated cell constains data,
   * false if indicated cell doesn't contain data
   */

   protected boolean  removeData (Locator locator) {
      Log.errorln("removeData() not currently implemented, returning false.");
      return false;
   }


   /**deep copy of this Data object
    */
   protected Object clone() throws CloneNotSupportedException {
      DataCube cloneObj = (DataCube) super.clone();
      synchronized (this) {
         synchronized (cloneObj) {
            cloneObj.longDataArray = deepCopy(this.longDataArray, dimension);
         }
      }
      return cloneObj;
   }

   //
   // PRIVATE methods
   //

   private int getLongDataArraySize (int longIndex, int type)
   {
      int size = 0;

      if (type == DOUBLE_DATA_TYPE) {
         size = ((double[]) longDataArray.get(longIndex+1)).length;
      } else if (type == INT_DATA_TYPE) {
         size = ((int []) longDataArray.get(longIndex+1)).length;
      } else if (type == BYTE_DATA_TYPE) {
         size = ((byte []) longDataArray.get(longIndex+1)).length;
      } else if (type == SHORT_DATA_TYPE) {
         size = ((short []) longDataArray.get(longIndex+1)).length;
      } else if (type == LONG_DATA_TYPE) {
         size = ((long []) longDataArray.get(longIndex+1)).length;
      } else if (type == STRING_DATA_TYPE) {
         size = ((String []) longDataArray.get(longIndex+1)).length;
      }

     return size;
   }

   private void checkDataArrayBounds (int longIndex, int shortIndex, int type)
   throws SetDataException
   {

      if (shortIndex < 0 || longIndex < 0) 
      {
         throw new SetDataException("Cant set data: passed locator/axes dont belong to this array?");
      }

      // Does the location exist yet? If not, create the primative arrays 
      // that lie along the short axis
      // int shortAxisSize = getShortAxis().getLength();
      int shortAxisSize = parentArray.getShortAxisSize();
      if (shortAxisSize < 1) { shortAxisSize = 1; }

      // is the long array too small?
      if (longDataArray.size() < (longIndex+1)) {
         int maxDeclLongArraySize = getMaxLongArraySize(); // should be held in private var
         int expandSize = longIndex > maxDeclLongArraySize ? longIndex : maxDeclLongArraySize;
         expandSize *= expandFactor; // add in additional amount to prevent us from doing this too much
         expandLongArray(expandSize);
      }

      // is the short array too small? has it been init'd even yet? 
      // we perform checks here..
      if (longDataArray.get(longIndex) == null) {

         // init/create the short array 
         longDataArray.set(  longIndex, new byte[shortAxisSize]);
         if (type == DOUBLE_DATA_TYPE) {
            longDataArray.set(longIndex+1, new double [shortAxisSize]);
         } else if (type == INT_DATA_TYPE) {
            longDataArray.set(longIndex+1, new int [shortAxisSize]);
         } else if (type == BYTE_DATA_TYPE) {
            longDataArray.set(longIndex+1, new byte [shortAxisSize]);
         } else if (type == SHORT_DATA_TYPE) {
            longDataArray.set(longIndex+1, new short [shortAxisSize]);
         } else if (type == LONG_DATA_TYPE) {
            longDataArray.set(longIndex+1, new long [shortAxisSize]);
         } else if (type == STRING_DATA_TYPE) {
            longDataArray.set(longIndex+1, new String [shortAxisSize]);
         }

      } else {

        int currentShortAxisSize = getLongDataArraySize(longIndex, type);

         // requested short axis location not exist?
         if (currentShortAxisSize <= shortIndex) {

            // should flag the user that need to add AxisValue first
            if (shortIndex > shortAxisSize) {
               throw new SetDataException("Error: axis lacks an AxisValue at location requested in setData().");
            } else {
               // add in short axis location to local short array(s) 
               int newsize = shortIndex+1; // need to add one for case of 1-D 
               newsize *= expandFactor; // expand short axis by expandFactor 
// Log.debugln("Expanding short array at longIndex:"+longIndex+" to "+newsize);
               longDataArray.set(longIndex, expandArray((byte []) longDataArray.get(longIndex), newsize));

               if (type == DOUBLE_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((double[]) longDataArray.get(longIndex+1), newsize));
                } else if (type == INT_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((int[]) longDataArray.get(longIndex+1), newsize));
                } else if (type == BYTE_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((byte[]) longDataArray.get(longIndex+1), newsize));
                } else if (type == SHORT_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((short[]) longDataArray.get(longIndex+1), newsize));
                } else if (type == LONG_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((long []) longDataArray.get(longIndex+1), newsize));
                } else if (type == STRING_DATA_TYPE) {
                   longDataArray.set(longIndex+1, expandArray((String []) longDataArray.get(longIndex+1), newsize));
                }
            }
         }
      }
   }


   private static byte[] expandArray (byte[] array, int newsize) {
      byte[] newarray = new byte[newsize];
 
      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }

   private static double[] expandArray (double[] array, int newsize) {

      double[] newarray = new double[newsize];

      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }

   private static String[] expandArray (String[] array, int newsize) {

      String[] newarray = new String[newsize];

      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }

   private static int[] expandArray (int[] array, int newsize) {

      int[] newarray = new int[newsize];

      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }

   private static short[] expandArray (short[] array, int newsize) {

      short[] newarray = new short[newsize];

      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }

   private static long[] expandArray (long[] array, int newsize) {

      long[] newarray = new long[newsize];

      int size = array.length;
      for (int i = 0; i < size ; i++) { newarray[i] = array[i]; }
      return newarray;

   }


   // make the present longArray bigger
   private void expandLongArray (int newsize) {

      int currentSize = longDataArray.size();
      int additionalCapacity = (newsize - currentSize) * 2; // mult by 2 to allow for shadow byte array 

      if (additionalCapacity > 0) {
Log.debugln(" DataCube is expanding internal LongDataArray size to "+(newsize*2)+" from "+(currentSize*2)+" (added capacity is:"+additionalCapacity+")");
         List moreArray = Collections.synchronizedList(new ArrayList(additionalCapacity));
         for (int i = 0; i < additionalCapacity; i++) {
            moreArray.add(null); // populate with nulls
         }
         longDataArray.addAll(moreArray);
      }

   }

   // Should be hardwired w/ private variable. Only
   // updates when addAxis is called by parentArray.
   private int getMaxLongArraySize() {
      int size = 0;
      List axisList = parentArray.getAxes();
      int numOfAxes = axisList.size();
      if (numOfAxes > 0) {
         size = ((AxisInterface) axisList.get(0)).getLength();

         // we skip over axis at index 1, that is the "short axis"
         // each of the higher axes contribute 2**(i-1) * index
         // to the overall long axis value.
         for (int i = 2; i < numOfAxes; i++) {
            int axisSize = ((AxisInterface) axisList.get(i)).getLength();
            size *= axisSize;
         }

      }

      return size;
   }


  /**writeTaggedData: write out tagged data
   *
   */
  private void writeTaggedData( Writer outputWriter,
			        Locator locator,
			        String indent,
			        int[] axisLength,
			        String[] tags,
			        int whichTag,
			        int currentDataFormat,
                                AxisInterface fastestAxis,
                                String[] noDataValues, 
                                int nrofDataFormats,
                                DataFormat[] dataFormat,
                                int[] numOfBytes,
                                String[] pattern,
                                String[] negExponentialPattern,
                                String endian,
                                String[] intFlag, 
                                int whichTagIsFieldAxis
                              )
  throws java.io.IOException
  {

    // int nrofNoDataValues = noDataValues.length;

    String tag = (String) tags[whichTag];
    boolean hasFieldAxis = parentArray.hasFieldAxis();

    if (Specification.getInstance().isPrettyXDFOutput()) {
      indent += Specification.getInstance().getPrettyXDFOutputIndentation();
    }

    // base case (writes the last 2 inner dimensions of the data cube)
    if (whichTag == tags.length-2) {
      int stop = axisLength[whichTag];
      String tag1 = (String) tags[whichTag+1];

      for (int count = 0; count < stop; count++) {
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  outputWriter.write( Constants.NEW_LINE+indent);
	}
	outputWriter.write( "<" + tag + ">");
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  outputWriter.write( Constants.NEW_LINE);
	  outputWriter.write( indent + Specification.getInstance().getPrettyXDFOutputIndentation());
	}

        // lets write out one row of data w/out newlines
	int fastestAxisLength = fastestAxis.getLength();
	int dataNum = 0;
	while (dataNum < fastestAxisLength) {
          outputWriter.write(  "<" + tag1 );
  	  try {
              outputWriter.write(  ">" + getFormattedDataCell ( dataFormat[currentDataFormat],
                                                                numOfBytes[currentDataFormat],
                                                                pattern[currentDataFormat],
                                                                negExponentialPattern[currentDataFormat],
                                                                endian,
                                                                intFlag[currentDataFormat],
                                                                false,
                                                                locator )
                                    + "</" + tag1 + ">");
             // the old method. Faster, but incorrectly formats 'E' (as well other types of) format data
             // outputWriter.write(  ">" + getStringData(locator) + "</" + tag1 + ">");
          }
          catch (NoDataException e) {

             String noDataString = noDataValues[currentDataFormat];
             if (noDataString != null)
             {
                outputWriter.write(">" + noDataString + "</" + tag1 + ">");
             } else
                outputWriter.write("/>");
	  }

	  dataNum++;
	  locator.next();

          // advance the DataFormat to be used  
          if(hasFieldAxis && (whichTag+1) == whichTagIsFieldAxis )
          {
             currentDataFormat++;
             if ( currentDataFormat == nrofDataFormats)
                currentDataFormat = 0;
          }

	}

	if (Specification.getInstance().isPrettyXDFOutput()) {
	  outputWriter.write( Constants.NEW_LINE + indent);
	}

	outputWriter.write("</" + tag+ ">");

        if(hasFieldAxis && whichTag == whichTagIsFieldAxis )
        {
           currentDataFormat++;
           if ( currentDataFormat == nrofDataFormats)
               currentDataFormat = 0;
        }

      }
    }
    else {
      // the 'outer' data tag wrapper. writes dimension 3 or higher tags
      int stop = axisLength[whichTag];
      whichTag++;
      for (int i = 0; i < stop; i++) {
	if (Specification.getInstance().isPrettyXDFOutput()) {
	  outputWriter.write(Constants.NEW_LINE+indent);
	}
	outputWriter.write("<" + tag + ">");
	writeTaggedData( outputWriter, locator, indent, axisLength, tags, whichTag, currentDataFormat, 
                         fastestAxis, noDataValues,
                         nrofDataFormats, dataFormat, numOfBytes, pattern, negExponentialPattern, endian, intFlag, 
                         whichTagIsFieldAxis );

        // advance the DataFormat to be used  
        if(hasFieldAxis && whichTag == whichTagIsFieldAxis )
        {
           currentDataFormat++;
           if ( currentDataFormat == nrofDataFormats)
              currentDataFormat = 0;
        }

	if (Specification.getInstance().isPrettyXDFOutput()) {
	  outputWriter.write(Constants.NEW_LINE+indent);
	}
	outputWriter.write("</" + tag + ">");
      }
    }
  }

  /** write delimited data
   *
   */
  private void writeDelimitedData(  Writer outputWriter,
                                    DelimitedXMLDataIOStyle readObj,
                                    AxisInterface fastestAxis, 
                                    String[] noDataValues,
                                    DataFormat[] dataFormat,
                                    int[] numOfBytes,
                                    String[] pattern,
                                    String[] negExponentialPattern,
                                    String endian,
                                    String[] intFlag,
                                    boolean writeCDATAStatement
                                  ) 
  throws java.io.IOException
  {

    Locator locator = parentArray.createLocator();

    int lastFieldIndex = 0;
    boolean hasFieldAxis = parentArray.hasFieldAxis();
    FieldAxis fieldAxis = null;
    if (hasFieldAxis)
       fieldAxis = parentArray.getFieldAxis();

    String delimiter = readObj.getDelimiter();
    String recordTerminator = readObj.getRecordTerminator();

    // safety
    if (recordTerminator == null)
       recordTerminator = delimiter;

    int fastestAxisLength = fastestAxis.getLength();

    if(writeCDATAStatement) 
       outputWriter.write("<![CDATA[");

    // the data loop
    int dataNum = 0;
    while (locator.hasNext())
    {

      try {
        
          outputWriter.write ( getFormattedDataCell ( dataFormat[lastFieldIndex],
                                                      numOfBytes[lastFieldIndex],
                                                      pattern[lastFieldIndex],
                                                      negExponentialPattern[lastFieldIndex],
                                                      endian,
                                                      intFlag[lastFieldIndex],
                                                      true,
                                                      locator )
                              );

      } catch (NoDataException e) {  //double check, a bug here, "yes" is already printed

         String noData = noDataValues[lastFieldIndex];

         if (noData == null) {
            if(readObj.getRepeatable().equals("yes"))
            {
               // should throw an error
               Log.errorln("Error: you have not set noDataValue and have a repeatable delimiter. Can't write data. Aborting.");
               System.exit(-1);
            }
         } else {
           outputWriter.write(noData);
         }
      }

       // advance our counters
       dataNum++;
       locator.next();

       // write the record terminator when we reach end of the fastest axis
       if (dataNum == fastestAxisLength) {
          outputWriter.write(recordTerminator);
          dataNum = 0;
       } else { 
          // write out delimiter anyway
          outputWriter.write(delimiter);
       }

       // advance the DataFormat to be used only if we are on a new field
       if(hasFieldAxis)
       {
          int currentFieldIndex = locator.getAxisIndex(fieldAxis);
          if (currentFieldIndex != lastFieldIndex)
          {
             lastFieldIndex = currentFieldIndex;
          }
       }

    }

    if (writeCDATAStatement) 
       outputWriter.write("]]>");
  }

  /** Write formatted data
   */
   private void writeFormattedData( Writer outputWriter,
                                    FormattedXMLDataIOStyle readObj,
                                    String[] noDataValues,
                                    DataFormat[] dataFormat,
                                    int[] numOfBytes,
                                    String[] pattern,
                                    String[] negExponentialPattern,
                                    String endian,
                                    String[] intFlag, 
                                    boolean writeCDATAStatement 
                                  )
   throws java.io.IOException
   {

      // int nrofNoDataValues = noDataValues.length;
      Locator locator = parentArray.createLocator();

      int lastFieldIndex = 0;
      boolean hasFieldAxis = parentArray.hasFieldAxis(); 
      FieldAxis fieldAxis = null;
      if (hasFieldAxis) 
          fieldAxis = parentArray.getFieldAxis();

      // print opening CDATA statement
      if (writeCDATAStatement) 
         outputWriter.write("<![CDATA[");

      // print out the data as appropriate for the format
      // QUESTION: don't we need to syncronize on readObj too?
      synchronized (longDataArray) 
      { 

        List commands = readObj.getFormatCommands(); // returns expanded list (no repeat cmds) 
        int nrofCommands = commands.size();
        int currentCommand = 0;

        // loop thru all of the dataCube until finished with all data and commands 
        while (locator.hasNext())
        { 

             FormattedIOCmd command = (FormattedIOCmd) commands.get(currentCommand);

             if (command instanceof ReadCellFormattedIOCmd)
             {

                try {
                   outputWriter.write ( getFormattedDataCell ( dataFormat[lastFieldIndex],
                                                               numOfBytes[lastFieldIndex],
                                                               pattern[lastFieldIndex],
                                                               negExponentialPattern[lastFieldIndex],
                                                               endian,
                                                               intFlag[lastFieldIndex],
                                                               true,
                                                               locator )  
                                      );
                } catch (NoDataException e) {

                    // no data here, hurm. Print the noDataValue. 
                    String noData = noDataValues[lastFieldIndex];

                    if (noData != null) { 
                        outputWriter.write( noData);
                    } else { 
                        Log.errorln("Can't print out null data: noDataValue NOT defined.");
                    }

                }

                // advance the data location 
                locator.next();

                // advance the DataFormat to be used only if we are on a new field
                if(hasFieldAxis)
                {
                   int currentFieldIndex = locator.getAxisIndex(fieldAxis);
                   if (currentFieldIndex != lastFieldIndex) 
                   {
                      lastFieldIndex = currentFieldIndex;
                   }
                }

             }
             else if (command instanceof SkipCharFormattedIOCmd)
             {

                doSkipCharFormattedIOCmdOutput ( outputWriter, (SkipCharFormattedIOCmd) command);

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

         } // end of while loop 

      } // end of sync loop

      // print closing CDATA statement
      if (writeCDATAStatement) 
         outputWriter.write("]]>");

   }

   private void doSkipCharFormattedIOCmdOutput ( Writer outputWriter, 
                                                 SkipCharFormattedIOCmd skipCharCommand)
   throws java.io.IOException
   {
       outputWriter.write( skipCharCommand.getOutput());
   }


   // Prints out data as appropriate as directed by the given DataFormat.
   // We make use of the Java Message|Decimal Formats here, seems wasteful
   // because we have to convert datatypes around to accomodate these classes.
   private String getFormattedDataCell ( DataFormat thisDataFormat, 
                                         int formatsize,
                                         String pattern,
                                         String negExponentialPattern,
                                         String endian,
                                         String intFlagType,
                                         boolean rightJustify,
                                         Locator locator
                                       ) 
   throws NoDataException, java.io.IOException
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
            double value = getDoubleData(locator);
            Double thisDatum = new Double(value);
            DecimalFormat formatter;

            // get the right pattern. 
            if (value > 0.0 && value < 1.0 && negExponentialPattern != null) { 
                // negative exponent number 
                formatter = new DecimalFormat(negExponentialPattern);
            } else { 
               // all other floats
               formatter = new DecimalFormat(pattern);
            }

            output = formatter.format(thisDatum);

/*
            // NOT NEEDED ANY MORE: Our quick 'fix': trim down the size of the output if its exceeded.
            if (output.length() > formatsize) { 
               Log.warnln("");
               Log.warn("Warning: formatted floating point number width exceeds spec, trimming ["+output+"] to ");
               output = output.substring(0,formatsize);
               Log.warnln("["+output+"] pattern: ["+pattern+"]");
            }
*/

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

            if (numOfBytes == 1) {

               byteBuf[0] = (byte) i;

            } else if (numOfBytes == 2) {

               byteBuf[0] = (byte) (i >>>  8);
               byteBuf[1] = (byte)  i;

            } else if (numOfBytes == 3) {

               byteBuf[0] = (byte) (i >>> 16);
               byteBuf[1] = (byte) (i >>>  8);
               byteBuf[2] = (byte)  i;

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
               // we could be a lot nicer than this..
               Log.errorln("XDF BinaryIntegerDataFormat cant handle integers with:"+numOfBytes+"bytes. Exiting.");
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

         StringBuffer outputBuffer = new StringBuffer();

         // if we have some output, write it
         if (output != null) {

            // pad with leading spaces
            if (rightJustify) {
               int actualsize = output.length();
               while (actualsize < formatsize)
               {
                  // outputWriter.write(" ");
                  outputBuffer.append(" ");
                  actualsize++;
               }
            }

            // now write the data out
            // outputWriter.write(output);
            outputBuffer.append(output);

         } else {
            // throw error
            // need better message here
            Log.printStackTrace(new IOException("No data to output."));
         }

         return outputBuffer.toString();
   } 

   /** init -- special private method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

    resetAttributes();

    classXDFNodeName = "data";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0, COMPRESSION_TYPE_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, ENCODING_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, CHECKSUM_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, HREF_XML_ATTRIBUTE_NAME);

    //set up the attribute hashtable key with the default initial value
    attribHash.put(COMPRESSION_TYPE_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(ENCODING_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(CHECKSUM_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
    attribHash.put(HREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.OBJECT_TYPE));

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
  * Revision 1.51  2001/09/21 16:50:17  thomas
  * setData now throw SetDataException w/ messages now
  *
  * Revision 1.50  2001/09/20 20:59:36  thomas
  * checkBounds now checks for neg. short/long internal axes values
  *
  * Revision 1.49  2001/09/20 15:07:48  thomas
  * added some float handling
  *
  * Revision 1.48  2001/09/19 16:39:25  thomas
  * clean up of write data code; better implementation of file href writing
  *
  * Revision 1.47  2001/09/18 21:40:45  thomas
  * I saw the light and yanked the 'fastestAxis' code in writeFormatted/Delimited data as per earlier stuff in tagged data writes. We should be thinking only in terms of the field axis when we need to determine if a new dataformat is needed for the next datacell we are writing. These changes should make it much more likely that the higher dimensional data will write out correctly (but it still needs to be tested!)
  *
  * Revision 1.46  2001/09/18 19:35:54  thomas
  * subtracted unused code
  *
  * Revision 1.45  2001/09/18 17:42:57  thomas
  * fixes to writing out tagged data, small code clean up
  *
  * Revision 1.44  2001/09/13 21:39:25  thomas
  * name change to either XMLAttribute, XMLNotation, XDFEntity, XMLElementNode class forced small change in this file
  *
  * Revision 1.43  2001/09/06 15:55:16  thomas
  * added byte set/getData stuff; changed basicXMLWriter to return String (nodeName)
  *
  * Revision 1.42  2001/09/05 22:01:36  thomas
  * removed toXMLoutputstream, toXMLWriter. Made it basicXMLWriter. Made Href->Entity change
  *
  * Revision 1.41  2001/09/04 21:17:52  thomas
  * added 8, 16 bit Integers
  *
  * Revision 1.40  2001/07/26 15:55:42  thomas
  * added flush()/close() statement to outputWriter object as
  * needed to get toXMLOutputStream to work properly.
  *
  * Revision 1.39  2001/07/19 21:52:39  thomas
  * yanked XMLDeclAttribs from toXMLOutputStream (only needed
  * in the XDF class)
  *
  * Revision 1.38  2001/07/11 22:35:21  thomas
  * Changes related to adding valueList or removeal of unneeded interface files.
  *
  * Revision 1.37  2001/07/06 19:04:23  thomas
  * toXMLOutputStream and related methods now pass on IOExceptions
  * to the application writer (e.g. they throw the error).
  *
  * Revision 1.36  2001/06/27 21:19:45  thomas
  * Implimented writing of compressed data to external file (GZIP, Zip only).
  *
  * Revision 1.35  2001/06/26 19:46:08  thomas
  * moved calculation of long, short axis up to
  * the Array. Probably will be moved again (to locator?)
  * in the near future.
  *
  * Revision 1.34  2001/06/25 15:13:56  thomas
  * implimented negativeExponentFormatPatterns in floats as an alt.
  * pattern when N <0 && N>-1 and an exponent is specified.
  *
  * Revision 1.33  2001/06/19 19:04:16  thomas
  * bug fix on getInt, getShort, getLongData methods.
  *
  * Revision 1.32  2001/06/18 21:42:29  thomas
  * first implemntation of reset() method. Antipated a possible
  * bug in getting shortArrayIndex when DataCube was 1-D.
  *
  * Revision 1.31  2001/06/18 17:07:26  thomas
  * total re-vamp of internal data storage. Will
  * actually handle more than 2D of data now.
  * More work needed to optimize.
  *
  * Revision 1.30  2001/05/29 21:56:39  thomas
  * added [set/get][Long/Short]Data methods.
  *
  * Revision 1.29  2001/05/10 21:08:51  thomas
  * init method is now protected. Added resetAttributes
  * call to init.
  *
  * Revision 1.28  2001/05/04 20:22:01  thomas
  * Minor bugfixes. Implement ArrayInterface for parentArray. Implement
  * AxisInterface in places, but not complete with this work.
  *
  * Revision 1.27  2001/05/02 18:16:39  thomas
  * Minor changes related to API standardization effort.
  *
  * Revision 1.26  2001/04/27 21:27:50  thomas
  * Small change to accomodate moving get/set LessThan, etc methods
  * from dataformat to Field class.
  *
  * Revision 1.25  2001/03/28 21:55:43  thomas
  * Doh! Perl code doesnt run in Java file. Fixed.
  *
  * Revision 1.24  2001/03/07 23:15:29  thomas
  * was missing newNodeNameString rename in toXMLfilehandle (or subroutine).
  *
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
  * plit up Attribute type NUMBER_TYPE into
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






