
// XDF RecordTerminator Class
// CVS $Id$

// RecordTerminator.java Copyright (C) 2001 Brian Thomas,
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

/** this class handles the recordTerminator ELEMENT
   @version $Revision$
 */
public class RecordTerminator extends BaseObject {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String VALUE_OBJ_XML_ATTRIBUTE_NAME = new String("valueObj");

  //
  // Constructor
  //

  //no-arg constructor
  public RecordTerminator ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void RecordTerminator ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /**setValue: set the value attribute
   */
  public void setValue (OutputCharDataInterface value) {
     ((Attribute) attribHash.get(VALUE_OBJ_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /**getValue: get the value of this recordterminator object
   */
  public OutputCharDataInterface getValue() {
     return (OutputCharDataInterface)  ((Attribute) attribHash.get(VALUE_OBJ_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** return the String representation of the recordTerminator object
   */
  public String getStringValue() {
     return getValue().getValue();
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

      // we put in a slight logic hack to make it look 'neat' for default case of 
      // a single space within a Chars object. ONLY do the full string when
      // its something else 
      if ((attrib=getValue()) !=null && !((OutputCharDataInterface) attrib).getValue().equals(" ") )
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

     classXDFNodeName = "recordTerminator";

     attribOrder.add(0, VALUE_OBJ_XML_ATTRIBUTE_NAME);

     //set up the axisUnits attribute
     Chars outputObj = new Chars();
     attribHash.put(VALUE_OBJ_XML_ATTRIBUTE_NAME, new Attribute(outputObj, Constants.OBJECT_TYPE));

  }

}

