
// XDF RepeatFormattedIOCmd Class
// CVS $Id$


// RepeatFormattedIOCmd.java Copyright (C) 2000 Brian Thomas,
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
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException; 
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** this class handles the repeat ELEMENT
   @version $Revision$
 */
public class RepeatFormattedIOCmd extends BaseObject implements FormattedIOCmd {

  //
  //Fields
  //

  /* XML attribute names */
  private static final String COUNT_XML_ATTRIBUTE_NAME = new String("count");

  /* default attribute values */
  public static final int DEFAULT_COUNT = 1;

  //list to store the formatted IO commands
  private List formatCommandList = Collections.synchronizedList(new ArrayList());

  //
  // Constructor
  //

  //no-arg constructor
  public RepeatFormattedIOCmd ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void RepeatFormattedIOCmd ( Hashtable InitXDFAttributeTable )
  {

     // init the XML attributes (to defaults)
     init();

     // init the value of selected XML attributes to HashTable values
     hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //

  /** Set the *count* attribute. 
   */
  public void setCount(Integer numCount) {
    if (numCount.intValue() < 1) {
       Log.warnln("Cant set repeatFormattedIOCmd count to less than 1, ignoring set request.");
    } else {
       ((Attribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).setAttribValue(numCount);
    }
  }

  /** Get the *count* attribute. 
   */
  public Integer getCount() {
     return (Integer) ((Attribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** Set the formatCommandList. 
   */
  public void setFormatCommandList(List formatList) {
     formatCommandList = formatList;
  }

  /** Get the formatCommandList. 
  */
  public List getFormatCommandList() {
   return formatCommandList;
  }

  /** Add a command to the formatCommandList
    * @return true on success, false on failure.
    */
  public boolean addFormatCommand(FormattedIOCmd formatCmd) {
    return formatCommandList.add(formatCmd);
  }

  /** Convenience method that returns the command list. Repeat
      commands are expanded into their component parts. 
   */
  public List getFormatCommands() {

     ArrayList commandList = new ArrayList();

     Iterator iter = formatCommandList.iterator();
     while (iter.hasNext()) {
        FormattedIOCmd thisCommand = (FormattedIOCmd) iter.next();
        if (thisCommand instanceof RepeatFormattedIOCmd) {
           int count = ((RepeatFormattedIOCmd) thisCommand).getCount().intValue();
           while (count-- > 0) {
              commandList.addAll(((RepeatFormattedIOCmd) thisCommand).getFormatCommands());
           }
        } else {
           commandList.add(thisCommand);
        }
     }

     return (List) commandList;
  }

  /**deep copy of this RepeatFormattedIOCmd object
    */
   public Object clone () throws CloneNotSupportedException {
    RepeatFormattedIOCmd cloneObj = (RepeatFormattedIOCmd) super.clone();
    synchronized (formatCommandList) {
      synchronized (cloneObj.formatCommandList) {
        int stop = formatCommandList.size();
        cloneObj.formatCommandList = Collections.synchronizedList(new ArrayList(stop));
        for (int i = 0; i <stop; i++) {
          cloneObj.formatCommandList.add(((BaseObject)formatCommandList.get(i)).clone());
        }
        return cloneObj;
      } //synchronize
    } //synchronize
   }

   protected String basicXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )

   throws java.io.IOException
   {

     if (Specification.getInstance().isPrettyXDFOutput()) {
         outputWriter.write(indent);
     }

     //open the code
     outputWriter.write("<"+classXDFNodeName+" "+COUNT_XML_ATTRIBUTE_NAME+"=\"");
     writeOutAttribute(outputWriter, getCount().toString());
     outputWriter.write("\">");

     if (Specification.getInstance().isPrettyXDFOutput()) {
         outputWriter.write(Constants.NEW_LINE);
     }

     //write out nodes in formatCommandList
     synchronized (formatCommandList) {
       int stop = formatCommandList.size();
       String moreIndent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
       for (int i = 0; i <stop; i++) {
         ((BaseObject) formatCommandList.get(i)).toXMLWriter(outputWriter, moreIndent);
       }
     }

     //close the node
     if (Specification.getInstance().isPrettyXDFOutput()) {
       // outputWriter.write(Constants.NEW_LINE);
        outputWriter.write(indent);
     }
     outputWriter.write("</" + classXDFNodeName + ">");

//     if (Specification.getInstance().isPrettyXDFOutput()) { outputWriter.write(Constants.NEW_LINE); }
     return classXDFNodeName;

  }

  //
  // Protected Methods
  //

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

    super.init();

    classXDFNodeName = "repeat";

    attribOrder.add(0, COUNT_XML_ATTRIBUTE_NAME);
    attribHash.put(COUNT_XML_ATTRIBUTE_NAME, new Attribute(new Integer(DEFAULT_COUNT), Constants.INTEGER_TYPE));
  }

}

