// XDF DelimitedXMLDataIOStyle Class
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

// DelimitedXMLDataIOStyle.java Copyright (C) 2000 Brian Thomas,
// ADC/GSFC-NASA, Code 631, Greenbelt MD, 20771

/**DelimitedXMLDataIOStyle.java: DelimitedDataIOStyle is a class that indicates
 * how delimited ASCII records are to be read in
 * @version $Revision$
 */


public abstract class DelimitedXMLDataIOStyle extends XMLDataIOStyle {

 //
  //Fields
  //


  public final static String DefaultDelimiter =" ";
  public final static boolean DefaultRepeatable = true;
  public final static String DefaultRecordTerminator = Constants.NEW_LINE;

  //double check
  protected XMLDataIOStyle parentReadObj;

  //no-arg constructor
  public DelimitedXMLDataIOStyle ()
  {
    init();
  }


  /** init -- special private method used by constructor methods to
   *  conviently build the XML attribute list for a given class.
   */
  private void init()
  {

    classXDFNodeName = "textDelimiter";

    // order matters! these are in *reverse* order of their
    // occurence in the XDF DTD
    attribOrder.add(0,"recordTerminator");
    attribOrder.add(0,"repeatable");
    attribOrder.add(0,"delimiter");

    //set up the attribute hashtable key with the default initial value
    attribHash.put("delimiter", new XMLAttribute(DefaultDelimiter, Constants.STRING_TYPE));
    attribHash.put("repeatable", new XMLAttribute("yes", Constants.STRING_TYPE));
    attribHash.put("recordTerminator", new XMLAttribute(DefaultRecordTerminator, Constants.STRING_TYPE));

  };


  //
  //Get/Set Methods
  //

  /**setDelimiter: set the *delimiter* attribute
   * @return: the current *delimiter* attribute
   */
  public String setDelimiter (String strDelimiter)
  {
    return (String) ((XMLAttribute) attribHash.get("delimiter")).setAttribValue(strDelimiter);

  }

  /**getDelimiter
   * @return: the current *delimiter* attribute
   */
  public String getDelimiter()
  {
    return (String) ((XMLAttribute) attribHash.get("delimiter")).getAttribValue();
  }



  /**setRepeatable: set the *repeatable* attribute
   * @return: the current *repeatable* attribute
   */
  public String setRepeatable (String strIsRepeatable)
  {
    if (!strIsRepeatable.equals("yes")  && !strIsRepeatable.equals("yes") ) {
      Log.error("*repeatable* attribute can only be set to yes or no");
      Log.error("tend to set as" + strIsRepeatable);
      Log.error("invalid. ignoring request");
      return null;
    }
    return (String) ((XMLAttribute) attribHash.get("repeatable")).setAttribValue(strIsRepeatable);

  }

  /**getRepeatable
   * @return: the current *repeatable* attribute
   */
  public String getRepeatable()
  {
    return (String) ((XMLAttribute) attribHash.get("repeatable")).getAttribValue();
  }


   /**setRecordTerminator: set the *recordTerminator* attribute
   * @return: the current *recordTerminator* attribute
   */
  public String setRecordTerminator (String strRecordTerminator)
  {
    return (String) ((XMLAttribute) attribHash.get("recordTerminator")).setAttribValue(strRecordTerminator);

  }

  /**getRecordTerminator
   * @return: the current *recordTerminator* attribute
   */
  public String getRecordTerminator()
  {
    return (String) ((XMLAttribute) attribHash.get("recordTerminator")).getAttribValue();
  }


  public XMLDataIOStyle setParentReadObj(XMLDataIOStyle parentReadObj) {
    Log.debug("in XMLDataIOStyle, setParentReadObj()");
    this.parentReadObj = parentReadObj;
    return parentReadObj;
  }


  public XMLDataIOStyle getParentReadObj() {
    return parentReadObj;
  }

  //double check
  public String regexNotation() {
    Log.error("in DelimitedXMLDataIOStyle, regexNotation, method empty, returning null");
    return null;
  }

  //double check
   public String sprintfNotation() {
    Log.error("in DelimitedXMLDataIOStyle, sprintfNotation, method empty, returning null");
    return null;
  }



}
/* Modification History:
 *
 * $Log$
 * Revision 1.1  2000/10/17 21:57:29  kelly
 * created and pretty much completed the class.  -k.z.
 *
 */
