
// XDF XMLDataIOStyle Class
// CVS $Id$

// XMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;
import java.util.List;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException; 

/** This abstract class indicates how records are to be read/written
 * back out into XDF formatted XML files.
 * @version $Revision$
 */

public abstract class XMLDataIOStyle extends BaseObject {

   //
   //Fields
   //

   /* XML attribute names */
   private static final String ENDIAN_XML_ATTRIBUTE_NAME = new String("endian");
   private static final String ENCODING_XML_ATTRIBUTE_NAME = new String("encoding");
   private static final String ID_XML_ATTRIBUTE_NAME = new String("readId");
   private static final String IDREF_XML_ATTRIBUTE_NAME = new String("readIdRef");

   private List axesIOList = Collections.synchronizedList(new ArrayList());

   /* attribute defaults */
   public final static String DEFAULT_ENCODING = Constants.IO_ENCODING_ISO_8859_1;
   public final static String DEFAULT_ENDIAN = Constants.BIG_ENDIAN;

   /* other */
   protected String UntaggedInstructionNodeName = "for";
   protected String UntaggedInstructionAxisIdRefName = "axisIdRef";
   protected Array parentArray;

  //no-arg constructor
  public XMLDataIOStyle (Array parentArray)
  {
     this.parentArray = parentArray;
     init();
  }

  public XMLDataIOStyle (Array parentArray, Hashtable InitXDFAttributeTable)
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

  /**set the *readId* attribute
     @return the current *readId* attribute
   */
   public void setReadId (String strReadId)
   {
       ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).setAttribValue(strReadId);
   }

  /**getReadId
   * @return the current *readId* attribute
   */
  public String getReadId()
  {
    return (String) ((XMLAttribute) attribHash.get(ID_XML_ATTRIBUTE_NAME)).getAttribValue();
  }



  /**set the *readIdRef* attribute
     @return the current *readIdRef* attribute
   */
   public void setReadIdRef (String strReadIdRef)
   {
      ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).setAttribValue(strReadIdRef);
   }

  /**getReadIdRef
   * @return the current *readIdRef* attribute
   */
  public String getReadIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get(IDREF_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *encoding* attribute
   * @return the current *encoding* attribute
   */
   public void setEncoding (String strEncoding)
   {
      if (!Utility.isValidIOEncoding(strEncoding)) {
         Log.warnln("setEncoding() got invalid value. Request ignored.");
         return;
      }
      ((XMLAttribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).setAttribValue(strEncoding);
    }

  /**getEncoding
   * @return the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((XMLAttribute) attribHash.get(ENCODING_XML_ATTRIBUTE_NAME)).getAttribValue();
  }

  /**set the *endian* attribute
     @return the current *endian* attribute
   */
   public void setEndian (String strEndian)
   {
       if (!Utility.isValidEndian(strEndian)) {
          Log.warnln("setEndian() got invalid value. Request ignored.");
          return;
       }

       ((XMLAttribute) attribHash.get(ENDIAN_XML_ATTRIBUTE_NAME)).setAttribValue(strEndian);
   }

  /**getEndian
   * @return the current *endian* attribute
   */
  public String getEndian()
  {
    return (String) ((XMLAttribute) attribHash.get(ENDIAN_XML_ATTRIBUTE_NAME)).getAttribValue();
  }


  public Array getParentArray() { return parentArray; }

  //
  // Other Public Methods
  //

  protected void basicXMLWriter (
                                Writer outputWriter,
                                String indent,
                                boolean dontCloseNode,
                                String newNodeNameString,
                                String noChildObjectNodeName
                             )
  throws java.io.IOException
  {

    boolean niceOutput = Specification.getInstance().isPrettyXDFOutput();
    String myIndent;
    if (indent!=null)
      myIndent = indent;
    else
      myIndent = "";

    String moreIndent = myIndent + Specification.getInstance().getPrettyXDFOutputIndentation();

    if (niceOutput)
      outputWriter.write( myIndent);

    //open the read block
    outputWriter.write( "<read");

    //write out attributes of read, ie.

    synchronized(attribHash) {  //sync, prevent the attribHash' structure be changed
      String attrib;
      if ( (attrib=getEncoding()) !=null)  
      { 
         outputWriter.write( " "+ENCODING_XML_ATTRIBUTE_NAME+"=\"");
         outputWriter.write( attrib);
         outputWriter.write( "\"");
      }

      if ( (attrib=getEndian()) !=null)
      { 
         outputWriter.write( " "+ENDIAN_XML_ATTRIBUTE_NAME+"=\"");
         outputWriter.write( attrib);
         outputWriter.write( "\"");
      }

      if ( (attrib=getReadId()) !=null)
      { 
         outputWriter.write( " "+ID_XML_ATTRIBUTE_NAME+"=\"");
         outputWriter.write( attrib);
         outputWriter.write( "\"");
      }

      if ( (attrib=getReadIdRef()) !=null)
      { 
         outputWriter.write( " "+IDREF_XML_ATTRIBUTE_NAME+"=\"");
         outputWriter.write( attrib);
         outputWriter.write( "\"");
      }


    }
    outputWriter.write( ">");

    //specific tailoring for childObj: Tagged, Delimited, Formated
    specificIOStyleToXDF(outputWriter, moreIndent);

     //close the read block
    if (niceOutput) {
      // outputWriter.write( Constants.NEW_LINE);
      outputWriter.write( indent);
    }

     outputWriter.write( "</read>");

//    if (niceOutput) { outputWriter.write(Constants.NEW_LINE); }

  }

   // Kelly, is this needed?
   public Object clone() throws CloneNotSupportedException { 
      return super.clone(); 
   }

   /** getIOAxesOrder:
    * Retrieve the order in which the axes will be read in/written out
    * from the Array. This ordering is NOT nesscesarily the same as
    * that returned by the parent Array.getAxes() method. This method   
    * returns a List of Axis objects in the order of 'fastest' to 'slowest'. 
    */
   public List getIOAxesOrder() {

      synchronized (axesIOList) {
         return axesIOList;
      }

   }

   public void setIOAxesOrder(List axisOrderList) {

      int parentSize = parentArray.getAxes().size();

      if (axisOrderList.size() != parentSize) {
          Log.errorln("Can't setIOAxisOrder(), passed list has wrong number of axes!, Ignoring request.");
          return;
      } else {

         synchronized (axesIOList) {
            axesIOList = Collections.synchronizedList(new ArrayList());
            for (int i = 0; i < parentSize; i++) {
               axesIOList.add(axisOrderList.get(i));
            }
         }
      }

   }

   // 
   // Private Methods
   //

   /** init -- special method used by constructor methods to
    *  convienently build the XML attribute list for a given class.
    */
   protected void init()
   {

      resetXMLAttributes();

      classXDFNodeName = "read";
     
      // order matters! these are in *reverse* order of their
      // occurence in the XDF DTD
      attribOrder.add(0, ENDIAN_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, ENCODING_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, IDREF_XML_ATTRIBUTE_NAME);
      attribOrder.add(0, ID_XML_ATTRIBUTE_NAME);

      //set up the attribute hashtable key with the default initial value
      attribHash.put(ENDIAN_XML_ATTRIBUTE_NAME, new XMLAttribute(DEFAULT_ENDIAN, Constants.STRING_TYPE));
      attribHash.put(ENCODING_XML_ATTRIBUTE_NAME, new XMLAttribute(DEFAULT_ENCODING, Constants.STRING_TYPE));
      attribHash.put(IDREF_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));
      attribHash.put(ID_XML_ATTRIBUTE_NAME, new XMLAttribute(null, Constants.STRING_TYPE));

      setIOAxesOrder(parentArray.getAxes());

   };


  /** set the parentArray.
   * used when Array clones.
   * should be protected, ie only classes in the same package see see this method
   */
  protected void setParentArray(Array parentArray) {
    this.parentArray = parentArray;
  }


  protected abstract void specificIOStyleToXDF(Writer out, String indent)
  throws java.io.IOException; 

}

/* Modification History:
 *
 * $Log$
 * Revision 1.25  2001/09/05 22:00:58  thomas
 * removed toXMLoutputstream, toXMLWriter. Made it basicXMLWriter
 *
 * Revision 1.24  2001/07/26 15:55:42  thomas
 * added flush()/close() statement to outputWriter object as
 * needed to get toXMLOutputStream to work properly.
 *
 * Revision 1.23  2001/07/19 21:59:44  thomas
 * yanked XMLDeclAttribs from toXMLOutputStream (only needed
 * in the XDF class)
 *
 * Revision 1.22  2001/07/11 22:35:21  thomas
 * Changes related to adding valueList or removeal of unneeded interface files.
 *
 * Revision 1.21  2001/07/06 19:04:23  thomas
 * toXMLOutputStream and related methods now pass on IOExceptions
 * to the application writer (e.g. they throw the error).
 *
 * Revision 1.20  2001/06/19 19:29:22  thomas
 * *** empty log message ***
 *
 * Revision 1.19  2001/06/18 21:39:28  thomas
 * added back in a write/readAxisOrder mthod (ugh). Now called
 * get/setIOAxes
 *
 * Revision 1.18  2001/05/10 21:45:33  thomas
 * added resetXMLAttributes to init().
 * small change to constructor related to inheritance.
 *
 * Revision 1.17  2001/05/04 20:27:02  thomas
 * Added Interface stuff.
 *
 * Revision 1.16  2001/05/02 18:16:39  thomas
 * Minor changes related to API standardization effort.
 *
 * Revision 1.15  2001/02/07 18:44:04  thomas
 * Converted XML attribute decl
 * to use constants (final static fields within the object). These
 * are private decl for now. -b.t.
 *
 * Revision 1.14  2000/11/27 22:39:25  thomas
 * Fix to allow attribute text to have newline, carriage
 * returns in them (print out as entities: &#010; and
 * &#013;) This allows files printed out to be read back
 * in again(yeah!). -b.t.
 *
 * Revision 1.13  2000/11/27 20:07:11  thomas
 * Removed minor debuging statement. -b.t.
 *
 * Revision 1.12  2000/11/16 20:11:46  kelly
 * fixed documentation.  -k.z.
 *
 * Revision 1.11  2000/11/09 04:50:59  thomas
 * Error in output attribute for node in toXML* method
 * (missing double quotes). Fixed. -b.t.
 *
 * Revision 1.10  2000/11/09 04:24:12  thomas
 * Implimented small efficiency improvements to traversal
 * loops. -b.t.
 *
 * Revision 1.9  2000/11/08 22:30:11  thomas
 * Changed set methods to return void. -b.t.
 *
 * Revision 1.8  2000/11/08 20:28:14  thomas
 * Trimmed down import path to just needed classes -b.t
 *
 * Revision 1.7  2000/11/08 19:18:07  thomas
 * Changed the name of toXDF* methods to toXML* to
 * better reflect the nature of the output (its not XDF
 * unless you call th emethod from strcuture object;
 * otherwise, it wont validate as XDF; it is still XML
 * however). -b.t.
 *
 * Revision 1.6  2000/11/06 21:15:20  kelly
 * minor fix in *toXDF*
 *
 * Revision 1.5  2000/10/31 21:45:13  kelly
 * minor fix to *toXDF*, the read opening/closing node is handled by
 * XMLDataIOSytle now.  -k.z.
 *
 * Revision 1.4  2000/10/30 18:17:01  kelly
 * Axis and FieldAxis now share common interface.  -k.z.
 *
 * Revision 1.3  2000/10/27 21:25:34  kelly
 * minor fix
 *
 * Revision 1.2  2000/10/17 21:51:53  kelly
 * pretty much completed the class, including the *toXDF* routines.  -k.z.
 *
 * Revision 1.1  2000/10/11 19:06:26  kelly
 * created the class
 *
 */
