
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
import java.io.OutputStream;
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
       ((XMLAttribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).setAttribValue(numCount);
    }
  }

  /** Get the *count* attribute. 
   */
  public Integer getCount() {
     return (Integer) ((XMLAttribute) attribHash.get(COUNT_XML_ATTRIBUTE_NAME)).getAttribValue();
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

  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {

     if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, indent);
     }

     //open the code
     writeOut(outputstream, "<" + classXDFNodeName);
     writeOut(outputstream, " "+COUNT_XML_ATTRIBUTE_NAME+"=\"");
     writeOutAttribute(outputstream, getCount().toString());
     writeOut(outputstream, "\"");

     writeOut(outputstream, ">");
     if (Specification.getInstance().isPrettyXDFOutput()) {
         writeOut(outputstream, Constants.NEW_LINE);
     }

     //write out nodes in formatCommandList
     synchronized (formatCommandList) {
       int stop = formatCommandList.size();
       String moreIndent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();
       for (int i = 0; i <stop; i++) {
         ((BaseObject) formatCommandList.get(i)).toXMLOutputStream(outputstream, moreIndent);
       }
     }

     //close the node
     if (Specification.getInstance().isPrettyXDFOutput()) {
       // writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
     }
     writeOut(outputstream, "</" + classXDFNodeName + ">");

     if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
     }
  }

  //
  // Protected Methods
  //

  /** special method used by constructor methods to
      convienently build the XML attribute list for a given class.
   */
  protected void init()
  {

    resetXMLAttributes();

    classXDFNodeName = "repeat";

    attribOrder.add(0, COUNT_XML_ATTRIBUTE_NAME);
    attribHash.put(COUNT_XML_ATTRIBUTE_NAME, new XMLAttribute(new Integer(DEFAULT_COUNT), Constants.INTEGER_TYPE));
  }

}


/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.8  2001/05/10 21:38:04  thomas
 * added resetXMLAttributes to init().
 * replaced specificIOStyleToXDF w/ appropriate
 * toXMLOutputStream method.
 *
 * Revision 1.7  2001/05/02 18:16:39  thomas
 * Minor changes related to API standardization effort.
 *
 * Revision 1.6  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.5  2000/11/27 22:39:25  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.4  2000/11/20 22:07:58  thomas
 * Implimented some changes needed by SaxDocHandler
 * to allow formatted reads (e.g. these classes were not
 * working!!). Implemented new XMLAttribute INTEGER_TYPE
 * in count attributes for repeat/skipChar classes. -b.t.
 *
 * Revision 1.3  2000/11/20 18:31:35  thomas
 * fixed getCommands method to return expaneded
 * command list. Fixed setCount to prevent <1 value
 * from being placed in count attribute. Made getCount
 * truely return Integer object. -b.t.
 *
 * Revision 1.2  2000/11/16 20:06:09  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.1  2000/11/09 23:42:38  kelly
 * created the class
 *
 *
 */
