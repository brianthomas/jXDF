// XDF XMLDataIOStyle Class
// CVS $Id$

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
import java.util.*;
import java.io.*;

// XMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

/**XMLDataIOStyle.java: This abstract class indicates how records are to be read/written
 * back out into XDF formatted XML files.
 * @version $Revision$
 */

public abstract class XMLDataIOStyle extends BaseObject {

 //
  //Fields
  //

  protected Array parentArray;
  public final static String DefaultEncoding = Constants.ISO_8859_1;
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

  /**setReadId: set the *readId* attribute
   * @return: the current *readId* attribute
   */
  public String setReadId (String strReadId)
  {
    return (String) ((XMLAttribute) attribHash.get("readId")).setAttribValue(strReadId);

  }

  /**getReadId
   * @return: the current *readId* attribute
   */
  public String getReadId()
  {
    return (String) ((XMLAttribute) attribHash.get("readId")).getAttribValue();
  }



  /**setReadIdRef: set the *readIdRef* attribute
   * @return: the current *readIdRef* attribute
   */
  public String setReadIdRef (String strReadIdRef)
  {
    return (String) ((XMLAttribute) attribHash.get("readIdRef")).setAttribValue(strReadIdRef);

  }

  /**getReadIdRef
   * @return: the current *readIdRef* attribute
   */
  public String getReadIdRef()
  {
    return (String) ((XMLAttribute) attribHash.get("readIdRef")).getAttribValue();
  }

  /**setEncoding: set the *encoding* attribute
   * @return: the current *encoding* attribute
   */
  public String setEncoding (String strEncoding)
  {
    if (!Utility.isValidEncoding(strEncoding)) {
      Log.error("not valid encoding.  'set' request ingored. returning null");
      return null;
    }
    return (String) ((XMLAttribute) attribHash.get("encoding")).setAttribValue(strEncoding);

  }

  /**getEncoding
   * @return: the current *encoding* attribute
   */
  public String getEncoding()
  {
    return (String) ((XMLAttribute) attribHash.get("encoding")).getAttribValue();
  }

  /**setEndian: set the *endian* attribute
   * @return: the current *endian* attribute
   */
  public String setEndian (String strEndian)
  {
    if (!Utility.isValidEndian(strEndian)) {
      Log.error("not valid endian.  'set' request ingored. returning null");
      return null;
    }
    return (String) ((XMLAttribute) attribHash.get("endian")).setAttribValue(strEndian);

  }

  /**getEndian
   * @return: the current *endian* attribute
   */
  public String getEndian()
  {
    return (String) ((XMLAttribute) attribHash.get("endian")).getAttribValue();
  }


  public Array setParentArray(Array parentArray) {
    Log.debug("in XMLDataIOStyle, setParentArray()");
    this.parentArray = parentArray;
    return parentArray;
  }


  public Array getParentArray() {
    return parentArray;
  }

  public void toXDFOutputStream ( OutputStream outputstream,
                                  Hashtable XMLDeclAttribs,
                                  String indent
                                )
  {
    String myIndent;
    boolean niceOutput = super.sPrettyXDFOutput;
    if (indent !=null) {
      myIndent = indent;
    }
    else
      myIndent = "";
    String moreIndent = super.sPrettyXDFOutputIndentation;


    //open the read block
    if (niceOutput)
      writeOut(outputstream, myIndent);
    writeOut(outputstream, "<"+classXDFNodeName + ">");
    if (niceOutput)
      writeOut(outputstream, Constants.NEW_LINE);

    String nextIndent = myIndent + moreIndent;
    List indents = new ArrayList();
    List axisList = getParentArray().getAxisList();
    String axisId;
    for (int i = 0; i< axisList.size(); i++) {
      axisId = ((Axis)axisList.get(i)).getAxisId();
      indents.add(nextIndent);
      if (niceOutput)
        writeOut(outputstream, nextIndent);
      writeOut(outputstream,"<"+UntaggedInstructionNodeName + "axisIdRef = \"" + axisId + "\">");
      if (niceOutput)
        writeOut(outputstream, Constants.NEW_LINE);
      nextIndent += moreIndent;
    }
    //now dump ourselves here using the trusty generic method
    super.toXDFOutputStream(outputstream, null, nextIndent);

    //close the instruction
    for (int i = indents.size()-1; i>=0; i++) {
      if (niceOutput)
        writeOut(outputstream, (String) indents.get(i));
      writeOut(outputstream, "</" + UntaggedInstructionNodeName + ">");
      if (niceOutput)
        writeOut(outputstream, Constants.NEW_LINE);

    }

    //close the read block
    if (niceOutput)
      writeOut(outputstream, myIndent);
    writeOut(outputstream, "<//"+classXDFNodeName + ">");
    if (niceOutput)
      writeOut(outputstream, Constants.NEW_LINE);

  }

  /** getReadAxisOrder:
   * Retrieve the order in which the axis will be read in/written out.
   * from the data cube.
   * Returns a scalar ARRAY reference of axisId values.
   */
   public List getReadAxisOrder() {
    List readList = new ArrayList();
    List axisList = getParentArray().getAxisList();
    Axis axisObj;
    for (int i = 0; i< axisList.size(); i++) {
      axisObj = (Axis) axisList.get(i);
      readList.add(axisObj.getAxisId());
    }

    return readList;
  }




}
/* Modification History:
 *
 * $Log$
 * Revision 1.2  2000/10/17 21:51:53  kelly
 * pretty much completed the class, including teh *toXDF* routines.  -k.z.
 *
 * Revision 1.1  2000/10/11 19:06:26  kelly
 * created the class
 *
 */
