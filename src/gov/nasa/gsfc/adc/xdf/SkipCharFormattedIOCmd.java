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
  private static final String OUTPUT_STRING_XML_ATTRIBUTE_NAME = new String("output");

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
      ((XMLAttribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).setAttribValue(numCount);
  }

  /** Get the *count* attribute. 
   */
  public Integer getCount() {
     return ((Integer) ((XMLAttribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).getAttribValue());
  }

  /**setOutput: set the *output* attribute
   */
  public void setOutput(String strOutput) {
     ((XMLAttribute) attribHash.get(OUTPUT_STRING_XML_ATTRIBUTE_NAME)).setAttribValue(strOutput);
  }

  /**getOutput: get the *output* attribute
   */
  public String getOutput() {
    return (String)  ((XMLAttribute) attribHash.get(OUTPUT_STRING_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  // Protected Methods
  //

  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  throws java.io.IOException
  {

    boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();

    if(niceOutput) writeOut(outputstream, indent);

    synchronized (attribHash) {
      //open the node
      writeOut(outputstream, "<" + classXDFNodeName);

      //writeOutAttributes
      Object attrib=null;
      if ( (attrib=getCount()) !=null) 
      { 
         writeOut( outputstream, " "+COUNT_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputstream, ((Integer) attrib).toString());
         writeOut(outputstream, "\"");
      }

      if ((attrib=getOutput()) !=null)
      { 
         writeOut(outputstream, " "+OUTPUT_STRING_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputstream, (String) attrib);
         writeOut(outputstream, "\"");
      }

      //close the node
      writeOut(outputstream, "/>");
    }

    if(niceOutput) writeOut(outputstream, Constants.NEW_LINE);

  }

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

    resetXMLAttributes();
    classXDFNodeName = "skipChars";

    attribOrder.add(0, OUTPUT_STRING_XML_ATTRIBUTE_NAME);
    attribOrder.add(0, COUNT_XML_ATTRIBUTE_NAME);

    attribHash.put(COUNT_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_COUNT), Constants.INTEGER_TYPE));
    attribHash.put("output", new XMLAttribute(DEFAULT_OUTPUT, Constants.STRING_TYPE));

  }

}


/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.8  2001/05/10 21:40:20  thomas
 * added resetXMLAttributes to init().
 * replaced specificIOStyleToXDF w/ appropriate
 * toXMLOutputStream method.
 *
 * Revision 1.7  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.6  2000/11/27 22:39:25  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.5  2000/11/20 22:07:58  thomas
 * Implimented some changes needed by SaxDocHandler
 * to allow formatted reads (e.g. these classes were not
 * working!!). Implemented new XMLAttribute INTEGER_TYPE
 * in count attributes for repeat/skipChar classes. -b.t.
 *
 * Revision 1.4  2000/11/16 20:08:27  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.3  2000/11/10 15:36:44  kelly
 * minor fix related to cvs checkin
 *
 * Revision 1.2  2000/11/10 01:40:41  thomas
 * Bug fix. This code was keeping the package from
 * compiling. Kelly, please review code carefully.
 * -b.t.
 *
 * Revision 1.1  2000/11/09 23:32:06  kelly
 * created the class to handle skipChars
 *
 *
 */
