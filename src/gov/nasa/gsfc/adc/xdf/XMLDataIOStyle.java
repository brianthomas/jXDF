  
  // XDF XMLDataIOStyle Class
  // CVS $Id$
  
  // XMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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
  
  import java.util.ArrayList;
  import java.util.Hashtable;
  import java.util.Collections;
  import java.util.List;
  
  import java.io.Writer;
  import java.io.BufferedWriter;
  import java.io.OutputStreamWriter;
  import java.io.OutputStream;
  import java.io.IOException; 
  
  /** This abstract class indicates how records are to be read/written
   * back out into XDF formatted XML files.
   * @version $Revision$
   */
  
  public abstract class XMLDataIOStyle extends BaseObject {
  
     //
     //Fields
     //
     private static final String READ_NODE_NAME = "dataStyle";
  
     /* XML attribute names */
     private static final String ENDIAN_XML_ATTRIBUTE_NAME = new String("endian");
     private static final String ENCODING_XML_ATTRIBUTE_NAME = new String("encoding");
     private static final String ID_XML_ATTRIBUTE_NAME = new String("dataStyleId");
     private static final String IDREF_XML_ATTRIBUTE_NAME = new String("dataStyleIdRef");
  
     protected List axesIOList = Collections.synchronizedList(new ArrayList());
  
     /* attribute defaults */
     public final static String DEFAULT_ENCODING = Constants.IO_ENCODING_ISO_8859_1;
     public final static String DEFAULT_ENDIAN = Constants.BIG_ENDIAN;
  
     /* other */
     protected String UntaggedInstructionNodeName = "for";
     protected String UntaggedInstructionAxisIdRefName = "axisIdRef";
     protected Array parentArray;
  
    //no-arg constructor
    public XMLDataIOStyle (Array parentArray)
    {
       this.parentArray = parentArray;
       init();
    }
  
    public XMLDataIOStyle (Array parentArray, Hashtable InitXDFAttributeTable)
    {
  
        this.parentArray = parentArray;
  
        // init the XML attributes (to defaults)
        init();
  
        // init the value of selected XML attributes to HashTable values
        hashtableInitXDFAttributes(InitXDFAttributeTable);
    }
  
    //
    //Get/Set Methods
    //
  
    /**set the Id attribute
     */
     public void setDataStyleId (String strDataStyleId)
     {
         ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strDataStyleId);
     }
  
    /**getDataStyleId
     * @return the current *Id* attribute
     */
    public String getDataStyleId()
    {
      return (String) ((Attribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
    }
  
  
  
    /**set the *dataStyleIdRef* attribute
     */
     public void setDataStyleIdRef (String strDataStyleIdRef)
     {
        ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strDataStyleIdRef);
     }
  
    /**getDataStyleIdRef
     * @return the current *readIdRef* attribute
     */
    public String getDataStyleIdRef()
    {
      return (String) ((Attribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
    }
  
    /**set the *encoding* attribute
     * @return the current *encoding* attribute
     */
     public void setEncoding (String strEncoding)
     {
        if (!Utility.isValidIOEncoding(strEncoding)) {
           Log.warnln("setEncoding() got invalid value. Request ignored.");
           return;
        }
        ((Attribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).setAttribValue(strEncoding);
      }
  
    /**getEncoding
     * @return the current *encoding* attribute
     */
    public String getEncoding()
    {
      return (String) ((Attribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).getAttribValue();
    }
  
    /**set the *endian* attribute
       @return the current *endian* attribute
     */
     public void setEndian (String strEndian)
     {
         if (!Utility.isValidEndian(strEndian)) {
            Log.warnln("setEndian() got invalid value. Request ignored.");
            return;
         }
  
         ((Attribute) attribHash.get(ENDIAN_XML_ATTRIBUTE_NAME)).setAttribValue(strEndian);
     }
  
    /**getEndian
     * @return the current *endian* attribute
     */
    public String getEndian()
    {
  
       String endian = (String) ((Attribute) attribHash.get(ENDIAN_XML_ATTRIBUTE_NAME)).getAttribValue();
       // a safety just in case someone asks a stupid question
       if (endian == null) endian = DEFAULT_ENDIAN;
  
       return endian;
    }
  
  
    public Array getParentArray() { return parentArray; }
  
    //
    // Other Public Methods
    //
  
    protected String basicXMLWriter (
                                  Writer outputWriter,
                                  String indent,
                                  boolean dontCloseNode,
                                  String newNodeNameString,
                                  String noChildObjectNodeName
                               )
    throws java.io.IOException
    {
  
      boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
      String myIndent;
      if (indent!=null)
        myIndent = indent;
      else
        myIndent = "";
  
      String moreIndent = myIndent + Specification.getInstance().getPrettyXDFOutputIndentation();
  
      if (niceOutput)
        outputWriter.write( myIndent);
  
      //open the read block
      outputWriter.write( "<"+READ_NODE_NAME);
  
      //write out attributes of read, ie.
  
      synchronized(attribHash) {  //sync, prevent the attribHash' structure be changed
        String attrib;
        if ( (attrib=getEncoding()) !=null)  
        { 
           outputWriter.write( " "+ENCODING_XML_ATTRIBUTE_NAME+"=\"");
           outputWriter.write( attrib);
           outputWriter.write( "\"");
        }
  
        // we have to do it this way to avoid printing it out
        // when we dont want to
        // (the method will return non-null value always)
        // if ( (attrib=getEndian()) !=null)  
        attrib = (String) ((Attribute) attribHash.get(ENDIAN_XML_ATTRIBUTE_NAME)).getAttribValue();
        if ( attrib != null)
        { 
           outputWriter.write( " "+ENDIAN_XML_ATTRIBUTE_NAME+"=\"");
           outputWriter.write( attrib);
           outputWriter.write( "\"");
        }
  
        if ( (attrib=getDataStyleId()) !=null)
        { 
           outputWriter.write( " "+ID_XML_ATTRIBUTE_NAME+"=\"");
           outputWriter.write( attrib);
           outputWriter.write( "\"");
        }
  
        if ( (attrib=getDataStyleIdRef()) !=null)
        { 
           outputWriter.write( " "+IDREF_XML_ATTRIBUTE_NAME+"=\"");
           outputWriter.write( attrib);
           outputWriter.write( "\"");
        }
  
  
      }
      outputWriter.write( ">");
  
      //specific tailoring for childObj: Tagged, Delimited, Formated
      specificIOStyleToXDF(outputWriter, moreIndent);
  
       //close the read block
      if (niceOutput) {
        // outputWriter.write( Constants.NEW_LINE);
        outputWriter.write( indent);
      }
  
       outputWriter.write( "</"+READ_NODE_NAME+">");
  
  //    if (niceOutput) { outputWriter.write(Constants.NEW_LINE); }
  
       return READ_NODE_NAME;
    }
  
     // Kelly, is this needed?
     public Object clone() throws CloneNotSupportedException { 
        return super.clone(); 
     }
  
     /** getIOAxesOrder:
      * Retrieve the order in which the axes will be read in/written out
      * from the Array. This ordering is NOT nesscesarily the same as
      * that returned by the parent Array.getAxes() method. This method   
      * returns a List of Axis objects in the order of 'fastest' to 'slowest'. 
      */
     public List getIOAxesOrder() {
  
        if (axesIOList == null) 
           return parentArray.getAxes();
        else 
           return axesIOList;
  
     }
  
     public void setIOAxesOrder(List axisOrderList) {
  
        int parentSize = parentArray.getAxes().size();
  
        if (axisOrderList.size() != parentSize) {
            Log.errorln("Can't setIOAxisOrder(), passed list has wrong number of axes!, Ignoring request.");
            return;
        } else {
  
           privateSetIOAxesOrder(axisOrderList);
  
        }
  
     }
  
     // 
     // Private Methods
     //
  
     /** init -- special method used by constructor methods to
      *  convienently build the XML attribute list for a given class.
      */
     protected void init()
     {
  
        resetAttributes();
  
        classXDFNodeName = "read";
       
        // order matters! these are in *reverse* order of their
        // occurence in the XDF DTD
        attribOrder.add(0, ENDIAN_XML_ATTRIBUTE_NAME);
        attribOrder.add(0, ENCODING_XML_ATTRIBUTE_NAME);
        attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
        attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);
  
        //set up the attribute hashtable key with the default initial value
        attribHash.put(ENDIAN_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
        attribHash.put(ENCODING_XML_ATTRIBUTE_NAME, new Attribute(DEFAULT_ENCODING, Constants.STRING_TYPE));
        attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
        attribHash.put(ID_XML_ATTRIBUTE_NAME, new Attribute(null, Constants.STRING_TYPE));
  
        // setIOAxesOrder(parentArray.getAxes());
        privateSetIOAxesOrder(parentArray.getAxes());
  
     };
  
  
    /** set the parentArray.
     * used when Array clones.
     * should be protected, ie only classes in the same package see see this method
     */
    protected void setParentArray(Array parentArray) {
      this.parentArray = parentArray;
    }
  
  
    protected abstract void specificIOStyleToXDF(Writer out, String indent)
    throws java.io.IOException; 
  
    //
    // Private Methods
    //
  
    // for when you are absolutely sure its the right list..
     private void privateSetIOAxesOrder (List axisOrderList)
     {
  
           synchronized (axesIOList) {
              axesIOList = Collections.synchronizedList(new ArrayList());
              for (int i = 0, size = axisOrderList.size(); i < size; i++) {
                 axesIOList.add(axisOrderList.get(i));
              }
           }
  
     }
  
  }
  
