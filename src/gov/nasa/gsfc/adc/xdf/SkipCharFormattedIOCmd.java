// XDF SkipCharFormattedIOCmd Class
// CVS $Id$


// SkipCharFormattedIOCmd.java Copyright (C) 2000 Brian Thomas,
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
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** this class handles the skipChar ELEMENT
   @version $Revision$
 */
public class SkipCharFormattedIOCmd extends BaseObject implements FormattedIOCmd {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String COUNT_XML_ATTRIBUTE_NAME = new String("count");
  private static final String OUTPUT_XML_ATTRIBUTE_NAME = new String("output");

  /* default attribute values */
  public static final int DEFAULT_COUNT = 1;
  public static final String DEFAULT_OUTPUT = " ";

  //
  // Constructor
  //

  //no-arg constructor
  public SkipCharFormattedIOCmd ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void SkipCharFormattedIOCmd ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /** Set the *count* attribute. May not set numCount to less than 1. 
   */
  public void setCount(Integer numCount) {
    if(numCount.intValue() < 1) 
      Log.warnln("Cant set skipChar count value to less than 1, ignoring request.");
    else  
      ((Attribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).setAttribValue(numCount);
  }

  /** Get the *count* attribute. 
   */
  public Integer getCount() {
     return ((Integer) ((Attribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).getAttribValue());
  }

  /**setOutput: set the *output* attribute
   */
  public void setOutput(OutputCharDataInterface outputObj) {
     ((Attribute) attribHash.get(OUTPUT_XML_ATTRIBUTE_NAME)).setAttribValue(outputObj);
  }

  /**getOutput: get the *output* attribute
   */
  public OutputCharDataInterface getOutput() {
     return (OutputCharDataInterface)  ((Attribute) attribHash.get(OUTPUT_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Protected Methods
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

    if(niceOutput) outputWriter.write(indent);

    synchronized (attribHash) {
      //open the node
      outputWriter.write("<" + classXDFNodeName);

      //writeOutAttributes
      Object attrib=null;
      if ( (attrib=getCount()) !=null) 
      { 
         outputWriter.write(" "+COUNT_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputWriter, ((Integer) attrib).toString());
         outputWriter.write("\"");
      }

      // we put in a slight logic hack to make it look 'neat' for default case of 
      // a single space within a Chars object. ONLY do the full string when
      // its something else 
      if ((attrib=getOutput()) !=null && !((OutputCharDataInterface) attrib).getValue().equals(" ") )
      { 


         outputWriter.write(">");
         if(niceOutput) 
            outputWriter.write(Constants.NEW_LINE);

         ((BaseObject) attrib).toXMLWriter(outputWriter, indent + Specification.getInstance().getPrettyXDFOutputIndentation());

         if(niceOutput) outputWriter.write(indent);
         outputWriter.write("</"+classXDFNodeName+">");

      } else {

         // just close the node
         outputWriter.write("/>");
      }

    }

    //if(niceOutput) outputWriter.write(Constants.NEW_LINE);
    return classXDFNodeName;

  }

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

     super.init();

     classXDFNodeName = "skip";

     attribOrder.add(0, OUTPUT_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, COUNT_XML_ATTRIBUTE_NAME);

     //set up the axisUnits attribute
     Chars outputObj = new Chars();
     attribHash.put(OUTPUT_XML_ATTRIBUTE_NAME, new Attribute(outputObj, Constants.OBJECT_TYPE));
     attribHash.put(COUNT_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_COUNT), Constants.INTEGER_TYPE));

  }

}

