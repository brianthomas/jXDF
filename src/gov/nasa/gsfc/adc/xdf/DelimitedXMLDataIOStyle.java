

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
import java.io.OutputStream;

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
   private static final String REPEATABLE_XML_ATTRIBUTE_NAME = "repeatable";
   private static final String END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME = "recordTerminator";

   // as defined in the DTD. 
   public final static String DEFAULT_DELIMITER = " ";
   public final static String DEFAULT_REPEATABLE = "yes";
   public final static String DEFAULT_RECORD_TERMINATOR = Constants.NEW_LINE;

   //
   // Constructors
   //
   public DelimitedXMLDataIOStyle (Array parentArray)
   {
      this.parentArray = parentArray;
      init();
   }

   /**  This constructor takes a Java Hashtable as an initializer of
        the XML attributes of the object to be constructed. The
        Hashtable key/value pairs coorespond to the class XDF attribute
        names and their desired values.
    */
   public DelimitedXMLDataIOStyle ( Array parentArray, Hashtable InitXDFAttributeTable )
   {

      this.parentArray = parentArray;

      // init the XML attributes (to defaults)
      init();

      // init the value of selected XML attributes to HashTable values
      hashtableInitXDFAttributes(InitXDFAttributeTable);
   }


  //
  //Get/Set Methods
  //

  /** set the *delimiter* attribute
   * @return the current *delimiter* attribute
   */
  public void setDelimiter (String strDelimiter)
  {
      ((XMLAttribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).setAttribValue(strDelimiter);
  }

  /**
   * @return the current *delimiter* attribute
   */
  public String getDelimiter()
  {
     return (String) ((XMLAttribute) attribHash.get(DELIMITER_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /** set the *repeatable* attribute
   * @return the current *repeatable* attribute
   */
  public void setRepeatable (String strIsRepeatable)
  {
     if (!strIsRepeatable.equals("yes")  && !strIsRepeatable.equals("no") ) {
        Log.errorln("*repeatable* attribute can only be set to yes|no. Ignoring set request.");
        return;
     }
     ((XMLAttribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).setAttribValue(strIsRepeatable);
  }

  /**
   * @return the current *repeatable* attribute
   */
  public String getRepeatable()
  {
     return (String) ((XMLAttribute) attribHash.get(REPEATABLE_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


   /** set the *recordTerminator* attribute
   * @return the current *recordTerminator* attribute
   */
  public void setRecordTerminator (String strRecordTerminator)
  {
     ((XMLAttribute) attribHash.get(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME)).setAttribValue(strRecordTerminator);
  }

  /**
   * @return the current *recordTerminator* attribute
   */
  public String getRecordTerminator()
  {
     return (String) ((XMLAttribute) attribHash.get(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  //
  //PROTECTED methods
  //

   protected void specificIOStyleToXDF( OutputStream outputstream,String indent) {
    int stop = parentArray.getAxisList().size()-1;
    synchronized (attribHash) {
      nestedToXDF(outputstream, indent, 0, stop);
    }

   }

   //
   // PRIVATE methods
   //

   private void nestedToXDF(OutputStream outputstream, String indent, int which, int stop) {

     String delimiter = getDelimiter();
     String repeatable = getRepeatable();
     String recordTerminator = getRecordTerminator();

    if (which > stop) {
      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      writeOut(outputstream, "<" + classXDFNodeName);
      if (delimiter !=null) { 
        writeOut(outputstream, " "+DELIMITER_XML_ATTRIBUTE_NAME+"=\"");
        writeOutAttribute(outputstream, delimiter);
        writeOut(outputstream, "\"");
      }

      writeOut(outputstream, " "+REPEATABLE_XML_ATTRIBUTE_NAME+"=\"");
      writeOutAttribute(outputstream, repeatable);
      writeOut(outputstream, "\"");

      if (recordTerminator !=null) {
         writeOut(outputstream, " "+END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME+"=\"");
         writeOutAttribute(outputstream, recordTerminator);
         writeOut(outputstream, "\"");
      }
      writeOut(outputstream, "/>");

    }
    else {
      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
      writeOut(outputstream, "<" + UntaggedInstructionNodeName + " "+UntaggedInstructionAxisIdRefName+"=\"");
      writeOut(outputstream, ((AxisInterface) parentArray.getAxisList().get(which)).getAxisId() + "\">");
      which++;
      nestedToXDF(outputstream, indent + Specification.getInstance().getPrettyXDFOutputIndentation(), which, stop);

      if (Specification.getInstance().isPrettyXDFOutput()) {
        writeOut(outputstream, Constants.NEW_LINE);
        writeOut(outputstream, indent);
      }
       writeOut(outputstream, "</" + UntaggedInstructionNodeName + ">");
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
      classXDFNodeName = "textDelimiter";

      attribOrder.add(0, REPEATABLE_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, DELIMITER_XML_ATTRIBUTE_NAME);

      attribHash.put(DELIMITER_XML_ATTRIBUTE_NAME, new XMLAttribute(DEFAULT_DELIMITER, Constants.STRING_TYPE));
      attribHash.put(REPEATABLE_XML_ATTRIBUTE_NAME, new XMLAttribute(DEFAULT_REPEATABLE, Constants.STRING_TYPE));
      attribHash.put(END_OF_LINE_DELIMITER_XML_ATTRIBUTE_NAME, 
                       new XMLAttribute(DEFAULT_RECORD_TERMINATOR, Constants.STRING_TYPE));

   }





}
/* Modification History:
 *
 * $Log$
 * Revision 1.9  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.8  2000/11/27 22:39:26  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.7  2000/11/16 19:57:09  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.6  2000/11/08 19:42:43  thomas
 * Trimmed import path to just needed classes. -b.t.
 *
 * Revision 1.5  2000/11/06 21:14:59  kelly
 * minor fix in *toXDF*
 *
 * Revision 1.4  2000/11/03 21:37:58  thomas
 * Opps, another fix needed. delimiter, repeatable and
 * recordTerminator werent being stored as XMLattributes.
 * Also, changed set mthods so void is returned. Set
 * inital values of XMLAttributes to defined defaults.
 * -b.t.
 *
 * Revision 1.3  2000/11/03 21:22:23  thomas
 * Had to add in XMLAttributes to init method. Added
 * Hashtable init constructor also. -b.t.
 *
 * Revision 1.2  2000/10/31 21:43:11  kelly
 * --completed the *toXDF*.
 * --got rid of the Perl specific stuff  -k.z.
 *
 * Revision 1.1  2000/10/17 21:57:29  kelly
 * created and pretty much completed the class.  -k.z.
 *
 */
