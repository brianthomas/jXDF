
// XDF Chars Class
// CVS $Id$


// Chars.java Copyright (C) 2001 Brian Thomas,
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
import java.io.Writer;


/** This class handles the outputting of (most) character data. "NewLine" character
    date will be converted to whitespace upon output as XML. You should use the
    NewLine class if you want to output newlines.
    @version $Revision$
 */
public class Chars extends BaseObject implements OutputCharDataInterface {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String VALUE_XML_ATTRIBUTE_NAME = new String("value");

  //
  // Constructor
  //

  //no-arg constructor
  public Chars ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void Chars ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /**setValue: set the character data of this object.
   */
  public void setValue(String charData) {
     ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).setAttribValue(charData);
  }

  /**getValue: get the character data of this object
   */
  public String getValue() {
     return (String)  ((Attribute) attribHash.get(VALUE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Protected Methods
  //

   // this has to be overridden only because the attribute name has the value of "value",
   // which will normally cause the attribute to be written as PCDATA, which we specifically
   // *DONT* want to happen with this class.
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
       String value = getValue();
       if (value != null)
       {
          outputWriter.write(" "+VALUE_XML_ATTRIBUTE_NAME+"=\""+value+"\"");
       }

       outputWriter.write("/>");

     } // end sync 

     if(niceOutput) outputWriter.write(Constants.NEW_LINE);

     return classXDFNodeName;

   }

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

     super.init();

     classXDFNodeName = "chars";

     attribOrder.add(0, VALUE_XML_ATTRIBUTE_NAME);

     //set up the axisUnits attribute
     attribHash.put(VALUE_XML_ATTRIBUTE_NAME, new Attribute(" ", Constants.STRING_TYPE));

  }

}

