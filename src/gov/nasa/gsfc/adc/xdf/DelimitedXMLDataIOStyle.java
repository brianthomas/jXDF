

// XDF DelimitedXMLDataIOStyle Class
// CVS $Id$

// DelimitedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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
// import java.io.OutputStream;

/** DelimitedDataIOStyle is a class that indicates
   how delimited ASCII records are to be read in
   @version $Revision$
 */


public class DelimitedXMLDataIOStyle extends XMLDataIOStyle {

   //
   // Fields
   //

   /* XML attribute names */
   private static final String DELIMITER_XML_ATTRIBUTE_NAME = "delimiter";
//   private static final String REPEATABLE_XML_ATTRIBUTE_NAME = "repeatable";
   private static final String END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME = "recordTerminator";

   // as defined in the DTD. 
//   public final static String DEFAULT_DELIMITER = " ";
//   public final static String DEFAULT_REPEATABLE = "yes";
 //  public final static String DEFAULT_RECORD_TERMINATOR = Constants.NEW_LINE;

   //
   // Constructors
   //
   public DelimitedXMLDataIOStyle (Array parentArray)
   {
      super(parentArray);
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public DelimitedXMLDataIOStyle ( Array parentArray, Hashtable InitXDFAttributeTable )
   {
      super(parentArray, InitXDFAttributeTable);
   }

  //
  //Get/Set Methods
  //

  /** set the *delimiter* attribute
   * @return the current *delimiter* attribute
   */
  public void setDelimiter (Delimiter delimiter)
  {
      ((Attribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).setAttribValue(delimiter);
  }

  /**
   * @return the current *delimiter* attribute
   */
  public Delimiter getDelimiter()
  {
     return (Delimiter) ((Attribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   /** set the *recordTerminator* attribute
   * @return the current *recordTerminator* attribute
   */
  public void setRecordTerminator (RecordTerminator recordTerminator)
  {
     ((Attribute) attribHash.get(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME)).setAttribValue(recordTerminator);
  }

  /**
   * @return the current *recordTerminator* attribute
   */
  public RecordTerminator getRecordTerminator()
  {
     return (RecordTerminator) ((Attribute) attribHash.get(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

   //
   //PROTECTED methods
   //

   protected void specificIOStyleToXDF( Writer outputWriter, String indent) 
   throws java.io.IOException
   {

      boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
      Delimiter delimiter = getDelimiter();
      RecordTerminator recordTerminator = getRecordTerminator();
      int stop = getIOAxesOrder().size()-1;

      synchronized (attribHash) {
   
        if (niceOutput) {
          outputWriter.write( Constants.NEW_LINE);
          outputWriter.write( indent);
        }
        outputWriter.write( "<" + classXDFNodeName +">");
  
        String moreIndent = Specification.getInstance().getPrettyXDFOutputIndentation();
  
        if (niceOutput) {
           outputWriter.write( Constants.NEW_LINE);
           outputWriter.write( indent + moreIndent);
        }
  
        outputWriter.write( "<delimitedReadInstructions>");
  
        if (niceOutput) {
           outputWriter.write( Constants.NEW_LINE);
        }
  
        delimiter.toXMLWriter(outputWriter, indent+moreIndent+moreIndent );
        recordTerminator.toXMLWriter(outputWriter, indent+moreIndent+moreIndent);
        
        if (niceOutput) 
           outputWriter.write( indent+moreIndent);
  
        outputWriter.write( "</delimitedReadInstructions>");
  
        // for instuctions are here 
        nestedToXDF(outputWriter, indent+moreIndent, stop, 0, niceOutput);

        if (niceOutput) 
           outputWriter.write(Constants.NEW_LINE + indent);

        outputWriter.write( "</" + classXDFNodeName +">");

      } // end sync 

      if (niceOutput) {
        outputWriter.write( Constants.NEW_LINE);
      }

   }

   //
   // PRIVATE methods
   //

/*
  Format is now:
            <delimitedStyle>
                <delimitedReadInstructions>
                   <!-- next line sez: use any number of space characters as the delimiter -->
                   <delimiter repeatable="yes"><chars value=" "/></delimiter>
                   <recordTerminator><newLine/></recordTerminator>
                </delimitedReadInstructions>
                <for axisIdRef="y-axis">
                   <for axisIdRef="x-axis">
                      <doReadInstructions/>
                   </for>
                </for>
             </delimitedStyle>
*/

   private void nestedToXDF(Writer outputWriter, String indent, int which, int stop, boolean niceOutput) 
   throws java.io.IOException
   {

    if (which < stop) {

      if (niceOutput) 
        outputWriter.write(Constants.NEW_LINE + indent);

      outputWriter.write( "<doReadInstructions/>");
     
    } else {

      if (niceOutput) {
        outputWriter.write( Constants.NEW_LINE + indent);
        // outputWriter.write( Constants.NEW_LINE);
        // outputWriter.write( indent);
      }
      outputWriter.write( "<" + UntaggedInstructionNodeName + " "+UntaggedInstructionAxisIdRefName+"=\"");

//      outputWriter.write( ((AxisInterface) parentArray.getAxes().get(which)).getAxisId() + "\">");
      outputWriter.write( ((AxisInterface) getIOAxesOrder().get(which)).getAxisId() + "\">");
      which--;
      nestedToXDF(outputWriter, indent + Specification.getInstance().getPrettyXDFOutputIndentation(), which, stop, niceOutput);

      if (niceOutput) {
        outputWriter.write( Constants.NEW_LINE + indent);
      }
      outputWriter.write( "</" + UntaggedInstructionNodeName + ">");
    }

   }


   //
   // Protected Methods
   // 

   /** A special protected method used by constructor methods to
       convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

      super.init(); 

      classXDFNodeName = "delimitedStyle";

      attribOrder.add(0, END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, DELIMITER_XML_ATTRIBUTE_NAME);

      attribHash.put(DELIMITER_XML_ATTRIBUTE_NAME, new Attribute(new Delimiter(), Constants.OBJECT_TYPE));
      attribHash.put(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME, 
                       new Attribute(new RecordTerminator(), Constants.OBJECT_TYPE));

   }

}

