

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
import java.io.Writer;
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
  public FormattedXMLDataIOStyle (Array parentArray)
  {
     super(parentArray);
  }

  /**  This constructor takes a Java Hashtable as an initializer of
       the XML attributes of the object to be constructed. The
       Hashtable key/value pairs coorespond to the class XDF attribute
       names and their desired values.
    */
  public FormattedXMLDataIOStyle ( Array parentArray, Hashtable InitXDFAttributeTable )
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

/*
  read SECTION for formatted data now looks like:

      <dataStyle>
         <fixedWidth>
            <fixedWidthInstruction>
               <repeat count="9">
                  <readCell/>
                  <skip count="1"/> <!-- just outputs a space -->
               </repeat>
               <readCell/>
               <skip count="1"><newLine/></skip> <!-- will output logical newLine -->
            </fixedWidthInstruction>
            <for axisIdRef="y-axis">
               <for axisIdRef="x-axis">
                  <doInstruction/>
               </for>
            </for>
         </fixedWidth>
      </dataStyle>
*/

  protected void specificIOStyleToXDF( Writer outputWriter, String indent)
  throws java.io.IOException
  {

     List axisList = getIOAxesOrder();
     int numberOfAxes = axisList.size(); 
     boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
     String moreIndent = Specification.getInstance().getPrettyXDFOutputIndentation();
     String sectionIndent = indent + moreIndent;

     if (niceOutput) {
        outputWriter.write( Constants.NEW_LINE);
        outputWriter.write( indent );
     }

     outputWriter.write("<fixedWidth>");

     /* SECTION 1: open up the read instruction section */
     if (niceOutput) {
        outputWriter.write( Constants.NEW_LINE);
        outputWriter.write( sectionIndent);
     }

     outputWriter.write("<fixedWidthInstruction>");

     if (niceOutput) 
        outputWriter.write( Constants.NEW_LINE);

     //write out nodes in formatCommandList
     synchronized (formatCommandList) {
        int stop = formatCommandList.size();
        for (int i = 0; i <stop; i++) {
           ((BaseObject) formatCommandList.get(i)).toXMLWriter(outputWriter, sectionIndent + moreIndent);
        }
     } // end formatCommandList sync 

     if (niceOutput) { 
          outputWriter.write( sectionIndent);
     }

     outputWriter.write("</fixedWidthInstruction>");

     /* SECTION 2: now print the For nodes */

     for (int i = (numberOfAxes-1); i >= 0; i--) {
        AxisInterface axis = (AxisInterface) axisList.get(i);
        if (niceOutput) {
           outputWriter.write( Constants.NEW_LINE);
           outputWriter.write( sectionIndent);
           sectionIndent = sectionIndent + moreIndent;
        }
        outputWriter.write( "<"+UntaggedInstructionNodeName+" "+UntaggedInstructionAxisIdRefName+"=\"");
        writeOutAttribute(outputWriter, axis.getAxisId());
        outputWriter.write( "\">");
     }

     if (niceOutput) { 
          outputWriter.write( Constants.NEW_LINE);
          outputWriter.write( sectionIndent + moreIndent);
     }

     //write out  the instruction to read the commands
     outputWriter.write("<doInstruction/>");

     if (niceOutput)
          outputWriter.write( Constants.NEW_LINE);

     // print out remaining for statements
     while(numberOfAxes-- > 0) 
     {
        if (niceOutput) {
           // peel off some indent
           sectionIndent = sectionIndent.substring(0,sectionIndent.length() - moreIndent.length());
           outputWriter.write( sectionIndent);
        }
        outputWriter.write( "</"+UntaggedInstructionNodeName+">");
        if (niceOutput) {
           outputWriter.write( Constants.NEW_LINE);
        }
     }

     if (niceOutput) 
         outputWriter.write( indent);

     outputWriter.write("</fixedWidth>");

     if (niceOutput) 
           outputWriter.write( Constants.NEW_LINE);

  }


  //
  // Private Methods
  //


}

