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
import java.io.OutputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
   @version $Revision$
 */
public class SkipCharFormattedIOCmd extends XMLDataIOStyle, implements FormattedIOCmd {
  //
  //Fields
  //

  public static final int DefaultCount = 1;
  public static final String DefaultOutput = " ";
  //
  //constructor and related methods
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

  /**setCount: set the *count* attribute
   */
  public void setCount(Integer numCount) {
    ((XMLAttribute) attribHash.get("count")).setAttribValue(numCount);
  }

  /**getCount: get the *count* attribute
   */
  public Integer getCount() {
    return (Integer)  ((XMLAttribute) attribHash.get("count")).getAttribValue();
  }

  /**setOutput: set the *output* attribute
   */
  public void setOutput(String strOutput) {
     ((XMLAttribute) attribHash.get("output")).setAttribValue(strOutput);
  }

  /**getOutput: get the *output* attribute
   */
  public String getOutput() {
    return (String)  ((XMLAttribute) attribHash.get("output")).getAttribValue();
  }

  //
  // Protected Methods
  //
  protected void synchronized (attribHash) toXMLOutputStream( OutputStream outputstream, String indent) {
    //open the node
    writeOut(outputstream, "<" + classXDFNodeName);

    //writeOutAttributes
    String attrib=null;
    if ( (attrib=getCount()) !=null)
      writeOut( outputstream, " count=\"" + attrib + "\"");
    if ((attrib=getOutput()) !=null)
      writeOut(outputstream, " output=\"" + attrib + "\"");
    //close the node

    writeOut(outputstream, " />");

  }


  //
  // Private Methods
  //

  /** special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  private void init()
  {
    classXDFNodeName = "skipChars";
    attribOrder.add(0,"output");
    attribOrder.add(0, "count");

    attribHash.put("count", new XMLAttribute(DefaultCount, Constants.NUMBER_TYPE));
    attribHash.put("output", new XMLAttribute(DefaultOutput, Constants.STRING_TYPE));
  }

}


/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/11/09 23:32:06  kelly
 * created the class to handle skipChars
 *
 *
 */