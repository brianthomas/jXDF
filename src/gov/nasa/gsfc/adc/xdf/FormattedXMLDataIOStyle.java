
// XDF FormattedXMLDataIOStyle Class
// CVS $Id$


// FormattedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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
public class FormattedXMLDataIOStyle extends XMLDataIOStyle {

  //
  //Fields
  //

  //list to store the formatted IO commands
  private List formatCommandList = Collections.synchronizedList(new ArrayList());

  //
  //constructor and related methods
  //

  //no-arg constructor
  public FormattedXMLDataIOStyle ()
  {
     init();
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public void FormattedXMLDataIOStyle ( Hashtable InitXDFAttributeTable )
  {

    // init the XML attributes (to defaults)
    init();

    // init the value of selected XML attributes to HashTable values
    hashtableInitXDFAttributes(InitXDFAttributeTable);

  }

  //
  //Get/Set methods
  //
  /**setFormatCommandList: set the formatCommandList
   */

  public void setFormatCommandList(List formatList) {
     formatCommandList = formatList;
  }

  /**getFormatCommandList: get the formatCommandList
  */
  public List getFormatCommandList() {
   return formatCommandList;
  }
  /**getCommands: convenience methods that return the command list
   */
  public List getCommands() {
     return formatCommandList;
  }

  //
  //Other PUBLIC methods
  //

  /**addFormatCommand: add a command to the formatCommandList
    * @return: the command that is added
    */
  public FormattedIOCmd addFormatCommand(FormattedIOCmd formatCmd) {
    formatCommandList.add(formatCmd);
    return formatCmd;
  }

/*
  public void toXMLOutputStream ( OutputStream outputstream, String indent) { } 
*/

  //
  // Protected Methods
  //

  protected void specificIOStyleToXDF( OutputStream outputstream,String indent)
  {

  }



  //
  // Private Methods
  //

  /** special private method used by constructor methods to
      conviently build the XML attribute list for a given class.
   */
  private void init()
  {
  }

  private void nestedToXDF(OutputStream outputstream, String indent, int which, int stop) {
    //base condition
    if (which > stop) {
      if (sPrettyXDFOutput) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      synchronized (formatCommandList) {
        int stop2 = formatCommandList.size();
        for (int i = 0; i < stop2; i++) {
          FormattedIOCmd command = (FormattedIOCmd) formatCommandList.get(i);
          command.toXMLOutputStream(outputstream, indent);
           if (sPrettyXDFOutput) {
           writeOut(outputstream, Constants.NEW_LINE);
           writeOut(outputstream, indent);
          }
        }
      }
    }
    else {
      if (sPrettyXDFOutput) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      writeOut(outputstream, "<" + UntaggedInstructionNodeName + " axisIdRef=\"");
      writeOut(outputstream, ((AxisInterface) parentArray.getAxisList().get(which)).getAxisId() + "\">");
      which++;
      nestedToXDF(outputstream, indent + sPrettyXDFOutputIndentation, which, stop);

      if (sPrettyXDFOutput) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
       writeOut(outputstream, "</" + UntaggedInstructionNodeName + ">");
    }
   }


}


/* Modification History:
 *
 * $Log$
 * Revision 1.4  2000/11/10 01:40:41  thomas
 * Bug fix. This code was keeping the package from
 * compiling. Kelly, please review code carefully.
 * -b.t.
 *
 * Revision 1.3  2000/11/09 23:25:01  kelly
 * completed specificIOStyleToXDF()
 *
 * Revision 1.2  2000/11/01 16:14:13  thomas
 *
 *   Another version, not finished but has more in it that before. -b.t.
 *
 *
 */

