

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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
   @version $Revision$
 */
public class FormattedXMLDataIOStyle extends XMLDataIOStyle {

  //
  //Fields
  //

  /**list to store the formatted IO commands
  */
  protected List formatCommandList = Collections.synchronizedList(new ArrayList());

  //
  //constructor and related methods
  //

  /** constructor
   */
  public FormattedXMLDataIOStyle (ArrayInterface parentArray)
  {
     super(parentArray);
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public FormattedXMLDataIOStyle ( ArrayInterface parentArray, Hashtable InitXDFAttributeTable )
  {
     super(parentArray,InitXDFAttributeTable);
  }

  //
  //Get/Set methods
  //
  /** set the formatCommandList
   */

  public void setFormatCommandList(List formatList) {
     formatCommandList = formatList;
  }

  /** get the formatCommandList
  */
  public List getFormatCommandList() {
   return formatCommandList;
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

  //
  //Other PUBLIC methods
  //

  /** add a command to the formatCommandList
    * @return true on success, false on failure.
    */
  public boolean addFormatCommand(FormattedIOCmd formatCmd) {
    return formatCommandList.add(formatCmd);
  }


   /**deep copy of this FormattedXMLDataIOStyle object
    */
  public Object clone () throws CloneNotSupportedException {
    FormattedXMLDataIOStyle cloneObj = (FormattedXMLDataIOStyle) super.clone();
    synchronized (formatCommandList) {
      synchronized (cloneObj.formatCommandList) {
        int stop = formatCommandList.size();
        cloneObj.formatCommandList = Collections.synchronizedList(new ArrayList(stop));
        for (int i = 0; i <stop; i++) {
          Object obj = formatCommandList.get(i) ;
            cloneObj.formatCommandList.add(((BaseObject) obj).clone());
        }
        return cloneObj;
      } //synchronize
    } //synchronize
  }

  //
  // Protected Methods
  //
  /**specific tailoring when writing out
   */

  protected void specificIOStyleToXDF( OutputStream outputstream, String indent)
  throws java.io.IOException
  {

     //List axisList = parentArray.getAxes();
     List axisList = getIOAxesOrder();
     int numberOfAxes = axisList.size(); 
     for (int i = (numberOfAxes-1); i >= 0; i--) {
        AxisInterface axis = (AxisInterface) axisList.get(i);
        if (Specification.getInstance().isPrettyXDFOutput()) {
           writeOut(outputstream, Constants.NEW_LINE);
           writeOut(outputstream, indent);
           indent = indent + Specification.getInstance().getPrettyXDFOutputIndentation(); 
        }
        writeOut(outputstream, "<"+UntaggedInstructionNodeName+" "+UntaggedInstructionAxisIdRefName+"=\"");
        writeOutAttribute(outputstream, axis.getAxisId());
        writeOut(outputstream, "\">");
     }

     if (Specification.getInstance().isPrettyXDFOutput()) 
              writeOut(outputstream, Constants.NEW_LINE);

     String newindent = indent + Specification.getInstance().getPrettyXDFOutputIndentation();

     //write out nodes in formatCommandList
     synchronized (formatCommandList) {
        int stop = formatCommandList.size();
        for (int i = 0; i <stop; i++) {
           // if (Specification.getInstance().isPrettyXDFOutput()) {
           //   writeOut(outputstream, Constants.NEW_LINE);
           //   writeOut(outputstream, indent);
          // }
           // ((XMLDataIOStyle) formatCommandList.get(i)).specificIOStyleToXDF(outputstream, indent);
           ((BaseObject) formatCommandList.get(i)).toXMLOutputStream(outputstream, newindent);
        }
     } // end formatCommandList sync 

     // print out remaining for statements
     while(numberOfAxes-- > 0) 
     {
        if (Specification.getInstance().isPrettyXDFOutput()) {
           // peel off some indent
           indent = indent.substring(0,indent.length() - 
                          Specification.getInstance().getPrettyXDFOutputIndentation().length());
           writeOut(outputstream, indent);
        }
        writeOut(outputstream, "</"+UntaggedInstructionNodeName+">");
        if (Specification.getInstance().isPrettyXDFOutput()) {
           writeOut(outputstream, Constants.NEW_LINE);
        }
     }

  }


  //
  // Private Methods
  //

/*
  private void nestedToXDF(OutputStream outputstream, String indent, int which, int stop) 
  throws java.io.IOException
  {
    //base condition
    if (which > stop) {
      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      synchronized (formatCommandList) {
        int end = formatCommandList.size();
        for (int i = 0; i < end; i++) {
          Object command = formatCommandList.get(i);
          ((XMLDataIOStyle) command).specificIOStyleToXDF(outputstream, indent);
           if (Specification.getInstance().isPrettyXDFOutput()) {
           writeOut(outputstream, Constants.NEW_LINE);
           writeOut(outputstream, indent);
          }
        }
      }
    }
    else {
      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      writeOut(outputstream, "<" + UntaggedInstructionNodeName + " "+UntaggedInstructionAxisIdRefName+"=\"");
      writeOut(outputstream, ((AxisInterface) parentArray.getAxes().get(which)).getAxisId() + "\">");
      which++;
      nestedToXDF(outputstream, indent + Specification.getInstance().getPrettyXDFOutputIndentation(), which, stop);

      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
       writeOut(outputstream, "</" + UntaggedInstructionNodeName + ">");
    }
   }
*/


}

/* Modification History:
 *
 * $Log$
 * Revision 1.18  2001/07/06 19:05:01  thomas
 * toXMLOutputStream and related methods now throw
 * java.io.IOException
 *
 * Revision 1.17  2001/06/28 16:50:54  thomas
 * changed add method(s) to return boolean.
 *
 * Revision 1.16  2001/06/19 19:29:22  thomas
 * *** empty log message ***
 *
 * Revision 1.15  2001/06/18 21:32:35  thomas
 * changes to reflect new getIOAxisOrder method of parent.
 *
 * Revision 1.14  2001/05/10 21:16:10  thomas
 * changes related to inheritance. call super in constructor.
 * FormattedIO objects arent type XMLDataIOSTYLE!!
 * changed to make them BaseObject.
 *
 * Revision 1.13  2001/05/04 20:23:16  thomas
 * Added Interface stuff.
 *
 * Revision 1.12  2001/05/02 18:16:39  thomas
 * Minor changes related to API standardization effort.
 *
 * Revision 1.11  2001/02/07 18:44:03  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.10  2000/11/27 22:39:26  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.9  2000/11/22 21:56:03  thomas
 * Fix to print out for nodes in toXML* methods. -b.t.
 *
 * Revision 1.8  2000/11/20 22:07:58  thomas
 * Implimented some changes needed by SaxDocHandler
 * to allow formatted reads (e.g. these classes were not
 * working!!). Implemented new XMLAttribute INTEGER_TYPE
 * in count attributes for repeat/skipChar classes. -b.t.
 *
 * Revision 1.7  2000/11/17 22:29:55  thomas
 * Some minor changes to code layout. -b.t.
 *
 * Revision 1.6  2000/11/16 20:00:19  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.5  2000/11/10 15:36:21  kelly
 * minor fix related to cvs checkin
 *
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

