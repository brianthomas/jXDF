
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
import java.util.List;

import java.io.OutputStream;

/**XMLDataIOStyle.java: This abstract class indicates how records are to be read/written
 * back out into XDF formatted XML files.
 * @version $Revision$
 */

public abstract class XMLDataIOStyle extends BaseObject {

 //
  //Fields
  //

  protected Array parentArray;
  public final static String DefaultEncoding = Constants.IO_ENCODING_ISO_8859_1;
  public final static String DefaultEndian = Constants.BIG_ENDIAN;
  public final static String UntaggedInstructionNodeName = "for";

  //no-arg constructor
  public XMLDataIOStyle ()
  {
    init();
  }


  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "read";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"endian");
    attribOrder.add(0,"encoding");
    attribOrder.add(0,"readIdRef");
    attribOrder.add(0,"readId");

     //set up the attribute hashtable key with the default initial value
    attribHash.put("endian", new XMLAttribute(DefaultEndian, Constants.STRING_TYPE));
    attribHash.put("encoding", new XMLAttribute(DefaultEncoding, Constants.STRING_TYPE));
    attribHash.put("readIdRef", new XMLAttribute(null, Constants.STRING_TYPE));
    attribHash.put("readId", new XMLAttribute(null, Constants.STRING_TYPE));
  };


  //
  //Get/Set Methods
  //

  /**set the *readId* attribute
     @return: the current *readId* attribute
   */
   public void setReadId (String strReadId)
   {
       ((XMLAttribute) attribHash.get("readId")).setAttribValue(strReadId);
   }

  /**getReadId
   * @return: the current *readId* attribute
   */
  public String getReadId()
  {
    return (String) ((XMLAttribute) attribHash.get("readId")).getAttribValue();
  }



  /**set the *readIdRef* attribute
     @return: the current *readIdRef* attribute
   */
   public void setReadIdRef (String strReadIdRef)
   {
      ((XMLAttribute) attribHash.get("readIdRef")).setAttribValue(strReadIdRef);
   }

  /**getReadIdRef
   * @return: the current *readIdRef* attribute
   */
  public String getReadIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("readIdRef")).getAttribValue();
  }

  /**set the *encoding* attribute
   * @return: the current *encoding* attribute
   */
   public void setEncoding (String strEncoding)
   {
      if (!Utility.isValidIOEncoding(strEncoding)) {
         Log.error("not valid encoding.  'set' request ingored. returning null");
         return;
      }
      ((XMLAttribute) attribHash.get("encoding")).setAttribValue(strEncoding);
    }

  /**getEncoding
   * @return: the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((XMLAttribute) attribHash.get("encoding")).getAttribValue();
  }

  /**set the *endian* attribute
     @return: the current *endian* attribute
   */
   public void setEndian (String strEndian)
   {
       if (!Utility.isValidEndian(strEndian)) {
          Log.error("not valid endian.  'set' request ingored. returning null");
          return;
       }
       ((XMLAttribute) attribHash.get("endian")).setAttribValue(strEndian);
   }

  /**getEndian
   * @return: the current *endian* attribute
   */
  public String getEndian()
  {
    return (String) ((XMLAttribute) attribHash.get("endian")).getAttribValue();
  }


  public Array getParentArray() { return parentArray; }

  //
  // Other Public Methods
  //

  public void toXMLOutputStream (
                                   OutputStream outputstream,
                                   Hashtable XMLDeclAttribs,
                                   String indent,
                                   boolean dontCloseNode,
                                   String newNodeNameString,
                                   String noChildObjectNodeName
                                )
  {
    boolean niceOutput = sPrettyXDFOutput;
    String myIndent;
    if (indent!=null)
      myIndent = indent;
    else
      myIndent = "";

    String moreIndent = myIndent + sPrettyXDFOutputIndentation;

    if (niceOutput)
      writeOut(outputstream, myIndent);

    //open the read block
    writeOut(outputstream, "<read");

    //write out attributes of read, ie.

    synchronized(attribHash) {  //sync, prevent the attribHash' structure be changed
      String attrib;
      if ( (attrib=getEncoding()) !=null)
        writeOut(outputstream, " encoding=" + attrib);
      if ( (attrib=getEndian()) !=null)
        writeOut(outputstream, " endian=" + attrib);
      if ( (attrib=getReadId()) !=null)
        writeOut(outputstream, " readId=" + attrib);
      if ( (attrib=getReadIdRef()) !=null)
        writeOut(outputstream, " readIdRef=" + attrib);

    }
    writeOut(outputstream, ">");

    //specific tailoring for childObj: Tagged, Delimited, Formated
    specificIOStyleToXDF(outputstream, moreIndent);

     //close the read block
    if (niceOutput) {
      writeOut(outputstream, Constants.NEW_LINE);
      writeOut(outputstream, indent);
    }

     writeOut(outputstream, "</read>");
    if (niceOutput) {
      writeOut(outputstream,Constants.NEW_LINE);
     }

  }

  // Kelly, is this needed?
  public Object clone() throws CloneNotSupportedException { 
     return super.clone(); 
  }

  // 
  // Protected Methods
  //

  /** set the parentArray.
   * used when Array clones.
   * should be protected, ie only classes in the same package see see this method
   */
  protected void setParentArray(Array parentArray) {
    Log.debug("in XMLDataIOStyle, setParentArray()");
    this.parentArray = parentArray;
  }


  protected abstract void specificIOStyleToXDF(OutputStream out, String indent);

  /** getReadAxisOrder:
   * Retrieve the order in which the axis will be read in/written out.
   * from the data cube.
   * Returns a scalar ARRAY reference of axisId values.
   */
   public List getReadAxisOrder() {
    List readList = new ArrayList();
    List axisList = getParentArray().getAxisList();
    AxisInterface axisObj;
    for (int i = 0; i< axisList.size(); i++) {
      axisObj = (AxisInterface) axisList.get(i);
      readList.add(axisObj.getAxisId());
    }

    return readList;
  }


}
/* Modification History:
 *
 * $Log$
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
