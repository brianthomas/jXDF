
// XDF Delimiter Class
// CVS $Id$

// Delimiter.java Copyright (C) 2001 Brian Thomas,
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

/** this class handles the delimiter ELEMENT
   @version $Revision$
 */
public class Delimiter extends BaseObject {

  //
  //Fields
  //

  /* default values */
  private static final String DEFAULT_REPEATABLE = "yes";

  /* XML attribute names */
  private static final String VALUE_OBJ_XML_ATTRIBUTE_NAME = new String("valueObj");
  private static final String REPEATABLE_XML_ATTRIBUTE_NAME = new String("repeatable");

  //
  // Constructor
  //

  //no-arg constructor
  public Delimiter ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void Delimiter ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //


  /** set the *repeatable* attribute
   */
  public void setRepeatable (String strRepeatable)
  {

    if (!Utility.isValidDataRepeatable(strRepeatable))
       Log.warnln("Data repeatable value is not valid, ignoring request to set it.");
    else
      ((Attribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).setAttribValue(strRepeatable);

  }

  /**
   * @return the current *repeatable* attribute
   */
  public String getRepeatable()
  {
    return (String) ((Attribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the value attribute
   */
  public void setValue (OutputCharDataInterface value) {
     ((Attribute) attribHash.get(VALUE_OBJ_XML_ATTRIBUTE_NAME)).setAttribValue(value);
  }

  /** get the value of this delimiter object (return either Chars or NewLine object). 
   */
  public OutputCharDataInterface getValue() {
     return (OutputCharDataInterface)  ((Attribute) attribHash.get(VALUE_OBJ_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** return the String representation of the delimiter object
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

      if ((attrib=getRepeatable()) != null) {
         outputWriter.write(" " + REPEATABLE_XML_ATTRIBUTE_NAME + "=\"" + attrib.toString() +"\"");
      }

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

     classXDFNodeName = "delimiter";

     attribOrder.add(0, VALUE_OBJ_XML_ATTRIBUTE_NAME);
     attribOrder.add(0, REPEATABLE_XML_ATTRIBUTE_NAME);

     //set up the axisUnits attribute
     Chars outputObj = new Chars();
     attribHash.put(VALUE_OBJ_XML_ATTRIBUTE_NAME, new Attribute(outputObj, Constants.OBJECT_TYPE));
     attribHash.put(REPEATABLE_XML_ATTRIBUTE_NAME, new Attribute(DEFAULT_REPEATABLE, Constants.STRING_TYPE));

  }

}

